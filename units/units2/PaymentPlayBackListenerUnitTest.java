import java.beans.Transient;

@RunWith(SpringRunner.class)
public class PaymentPlayBackListenerUnitTest {

    @Mock
    EventFlowProcessor eventFlowProcessor;

    PaymentPlayBackListener listener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        listener = new PaymentPlayBackListener();
        listener.eventFlowProcessor = eventFlowProcessor;
    }

    // helper method
    private String getPayload(PaymentInitWrapper wrapper) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testReceiveMessageForRequestFlow() {
        
        PaymentInitWrapper wrapper = new PaymentInitWrapper();
        String payload = getPayload(wrapper);

        listener.receiveMessage(payload, null, null, null, null, EventStage.FWD_FLOW_VALIDATION_RECOVERABLE_ERROR.toString());

        verify(eventFlowProcessor, times(1)).requestResponseFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void testReceiveMessageForReqRespFlow() {
        
        PaymentInitWrapper wrapper = new PaymentInitWrapper();
        String payload = getPayload(wrapper);

        listener.receiveMessage(payload, null, null, null, null, EventStage.CLIENT_API_RECOVERABLE_ERROR.toString());

        verify(eventFlowProcessor, times(1)).routerFlows(any(PaymentInitWrapper.class));
    }

    @Test
    public void testReceiveMessageForResponseFlow() {
        
        PaymentInitWrapper wrapper = new PaymentInitWrapper();
        String payload = getPayload(wrapper);

        listener.receiveMessage(payload, null, null, null, null, EventStage.RET_FLOW_PARSE_RECOVERABLE_ERROR.toString());

        verify(eventFlowProcessor, times(1)).responseFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void testReceiveMessageForInvalidKey() {
        
        PaymentInitWrapper wrapper = new PaymentInitWrapper();
        String payload = getPayload(wrapper);

        listener.receiveMessage(payload, null, null, null, null, "INVALID_KEY");

        verify(eventFlowProcessor, times(0)).requestResponseFlow(any());
        verify(eventFlowProcessor, times(0)).routerFlows(any());
        verify(eventFlowProcessor, times(0)).responseFlow(any());
    }
}