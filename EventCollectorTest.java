

@SpringBootTest
@RunWith(SpringRunner.class)
public void EventCollectorTest {

    @MockBean
    HazelcastInstance eventHazelcastInstance;

    @Mock
    IMap<String, EventWrapper> eventMap;

    @Mock
    IMap<String, EventWrapper> eventMapTrace;

    @Mock
    Lock lock;

    @Autowired
    ApplicationContext context;

    EventCollector eventCollector;

    // helper method
    private String getPayload() {
        
        EventWrapper wrapper = new EventWrapper();
        wrapper.FlowID = "3232";

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            return null;
        }
    }

    @Before
    public void setup() {
        when(eventHazelcastInstance.getMap("APIEventsCallbackCache")).thenReturn(eventMap);
        when(eventHazelcastInstance.getMap("APIEventCallbackCacheTrace")).thenReturn(eventMapTrace);
        when(eventHazelcastInstance.getLock(any())).thenReturn(lock);
        eventCollector = context.getBean(EventCollector.class);
    }

    @Test
    public void testSuccessFullInsertevent() {
        String payload = getPayload();
        try {
            eventCollector.insertEvent(payload);
            verify(eventMapTrace, times(1)).put(any(), any()); 
            verify(lock, times(1)).lock();
            verify(eventMap, times(1)).put(any(), any());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed with: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidInsertevent() {
        String payload = "";
        try {
            eventCollector.insertEvent(payload);
            fail("Should have trown error with invalid payload");
        } catch (Exception e) {
            // do nothing. error was expected
        }
    }
}