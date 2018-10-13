import java.util.Arrays;

@RunWith(SpringRunner.class)
public class APICallRequestFailureUnitTest {

    @Mock
    IMap<String, EventWrapper> eventMap;

    @Mock
    JmsTemplate jmsTemplate;

    @Mock
    Lock lock;

    @Mock
    HazlecastInstance eventHazlecastInstance;

    APICallRequestFailure apiCallRequestFailure;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(eventHazelcastInstance.getMap("APIEventCallbackCache")).thenReturn(eventMap);
        when(eventHazelcastInstance.getLock(any())).thenReturn(lock);

        EventWrapper w1 = new EventWrapper();
        w1.FlowID = "232";
        EventWrapper w2 = new EventWrapper();
        w1.FlowID = "2332";
        Set<EventWrapper> wrappers = new HashSet<EventWrapper>(Arrays.asList(w1, w2));
        when(eventMap.values(any())).thenReturn(wrappers);
        when(eventMap.get(any())).thenReturn(w1);

        apiCallRequestFailure = new APICallRequestFailure ();
        apiCallRequestFailure.eventHazelcastInstance = eventHazlecastInstance;
        apiCallRequestFailure.jmsTemplate = jmsTemplate;
    }

    @Test
    public void testCheck() {

        apiCallRequestFailure.check();

        verify(lock, times(wrappers.size())).lock();
        verify(jmsTemplate, times(wrappers.size())).send(any(), any());
        verify(eventMap, times(wrappers.size())).put(any(), any(EventWrapper.class));
        verify(lock, times(wrappers.size())).unlock();
    }


}