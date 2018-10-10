
@RunWith(SpringRunner.class)
public class EventCollectorTest {

    @Mock
    HazelcastInstance eventHazelcastInstance;

    @Mock
    IMap<String, EventWrapper> eventMap;

    @Mock
    IMap<String, EventWrapper> eventMapTrace;

    @Mock
    Lock lock;

    EventCollector eventCollector;

    @Before
    public void setup() {
        
        MockitoAnnotations.initMocks(this);
        when(eventHazelcastInstance.getMap("APIEventsCallbackCache")).thenReturn(eventMap);
        when(eventHazelcastInstance.getMap("APIEventCallbackCacheTrace")).thenReturn(eventMapTrace);
        when(eventHazelcastInstance.getLock(any())).thenReturn(lock);
        
        eventCollector = new EventCollector();
        eventCollector.eventHazelcastInstance = eventHazelcastInstance;
    }

    // helper method
    private String getPayload(EventWrapper wrapper) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testSuccessFullInsertevent() {
        
        EventWrapper wrapper = new EventWrapper();
        wrapper.FlowID = "3232";
        String payload = getPayload(wrapper);
        when(eventMap.get(anyString())).thenReturn(wrapper);

        try {
            eventCollector.insertEvent(payload);
            verify(eventMapTrace, times(1)).put(anyString(), any(EventWrapper.class)); 
            verify(lock, times(1)).lock();
            verify(eventMap, times(1)).put(eq(wrapper.FlowID), any(EventWrapper.class));
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