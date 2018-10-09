

@RunWith(SpringRunner.class)
public class IncomingPaymentEventsMQListenerUnitTest {

    @Mock
    EventManagerImpl eventManager;

    @Mock
    ParseServices parseServicesWrapper;

    @Mock
    EventFlowProcessor eventFlowProcessor;

    IncomingPaymentEventsMQListener messageListener;

    @Before public void setup() {
        MockitoAnnotations.initMocks(this);
        messageListener = new IncomingPaymentEventsMQListener();
        messageListener.eventManager = eventManager;
        messageListener.parseServiceWrapper = parseServicesWrapper;
        messageListener.EventFlowProcessor = eventFlowProcessor;
    }

    // helper method
    private String getPaymentInitWrapper(PaymentmentInitWrapper paymentmentInitWrapper) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testOnMessageSuccess() {

        PaymentInitWrapper paymentmentInitWrapper = new PaymentInitWrapper();
        String text = getPaymentInitWrapper(paymentmentInitWrapper);
        TextMessage textMessage = mock(TextMessage.class);
        
        when(textMessage.getText()).thenReturn(text);
        when(parseServicesWrapper.parseRequestMessage(any())).thenReturn(paymentmentInitWrapper);

        messageListener.onMessage(textMessage);

        verify(eventManager, times(1)).pushEvent(eq(EventStage.FWD_FLOW_PARSE_SUCCESS), any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(1)).requestResponseFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void testOnMessageRecoverableError() {

        PaymentInitWrapper paymentmentInitWrapper = new PaymentInitWrapper();
        paymentmentInitWrapper.addRecoverableError("SOME ERROR"); // << add one from the constants
        String text = getPaymentInitWrapper(paymentmentInitWrapper);
        TextMessage textMessage = mock(TextMessage.class);

        when(textMessage.getText()).thenReturn(text);
        when(parseServicesWrapper.parseRequestMessage(any())).thenReturn(paymentmentInitWrapper);

        messageListener.onMessage(textMessage);

        verify(eventManager, times(1)).pushEvent(eq(EventStage.FWD_FLOW_PARSE_RECOVERABLE_ERROR), any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(1)).requestResponseFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void testOnMessageIrrecoverableError() {

        PaymentInitWrapper paymentmentInitWrapper = new PaymentInitWrapper();
        paymentmentInitWrapper.addIrrecoverableError("SOME ERROR"); // << add one from the constants
        String text = getPaymentInitWrapper(paymentmentInitWrapper);
        TextMessage textMessage = mock(TextMessage.class);
        
        when(textMessage.getText()).thenReturn(text);
        when(parseServicesWrapper.parseRequestMessage(any())).thenReturn(paymentmentInitWrapper);

        messageListener.onMessage(textMessage);

        verify(eventManager, times(1)).pushEvent(eq(EventStage.FWD_FLOW_PARSE_IRRECOVERABLE_ERROR), any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).requestResponseFlow(any(PaymentInitWrapper.class)); // for irrecoverable error, should not be invoked
    }
}