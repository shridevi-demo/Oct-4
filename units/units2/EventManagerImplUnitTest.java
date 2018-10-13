
@RunWith(SpringRunner.class)
public class EventManagerImplUnitTest {

    @Mock
    KafkaTemplate<String, String> kafkaEventCollectorQueueService;

    @Mock
    IMap<String, EventWrapper> eventMap;

    @Mock
    IMap<String, EventWrapper> eventMapTrace;

    @Mock
    Lock lock;

    @Mock
    HazlecastInstance eventHazlecastInstance;

    EventManagerImpl eventManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(eventHazelcastInstance.getMap("APIEventsCallbackCache")).thenReturn(eventMap);
        when(eventHazelcastInstance.getMap("APIEventCallbackCacheTrace")).thenReturn(eventMapTrace);
        when(eventHazelcastInstance.getLock(any())).thenReturn(lock);

        eventManager = new EventManagerImpl();
        eventManager.kafkaEventCollectorQueueService = kafkaEventCollectorQueueService;
        eventManager.eventHazlecastInstance = eventHazlecastInstance;
    }

    @Test
    public void testPushEvent() {
        PaymentInitWrapper payload = new PaymentInitWrapper();
        payload.setFlowID("233243");
        payload.setParentFlowID("23324332");
        // .. set all other params which are being used to create event wrapper object inside pushevent()

        eventManager.pushEvent(EventStage.SOME_FLOW, payload);
        verify(eventMapTrace, times(1)).put(anyString(), any(EventWrapper.class)); 
        verify(lock, times(1)).lock();
        verify(eventMap, times(1)).put(eq(payload.getFlowID()), any(EventWrapper.class));
        verify(lock, times(1)).unlock();
    }

    @Test
    public void testPushEventWithNullFlowId() {
        PaymentInitWrapper payload = new PaymentInitWrapper();
        payload.setFlowID(null);
        payload.setParentFlowID(null);
        // .. continue setting other fields like before

        eventManager.pushEvent(EventStage.SOME_FLOW, payload); // in case of null flow id, nothing should be invoked
        verify(eventMapTrace, times(0)).put(anyString(), any(EventWrapper.class));
        verify(lock, times(0)).lock();
        verify(eventMap, times(0)).put(eq(payload.getFlowID()), any(EventWrapper.class));
        verify(lock, times(0)).unlock();
    }

    @Test
    public void testKafkaEventCollector() {
        PaymentInitWrapper payload = new PaymentInitWrapper();
        payload.setFlowID("233243");
        payload.setParentFlowID("23324332");
        // .. continue setting other fields like before

        // use reflection to enable kafka
        Field field = ReflectionUtils.findField(EventManagerImpl.class, "isEnabled");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, eventManager, true);

        eventManager.pushEvent(EventStage.SOME_FLOW, payload);

        // in case isEnabled true, shall go to if block
        // so check if a call has been made to KafkaTemplate
        verify(kafkaEventCollectorQueueService, times(1)).send(anyString(), anyString());
    }
}
