

@SpringBootTest
@RunWith(SpringRunner.class)
public class IncomingPaymentEventsMQListenerTest {

    @MockBean
    EventManagerImpl eventManager;

    @MockBean
    ParseServices parseServicesWrapper;

    @MockBean
    EventFlowProcessor eventFlowProcessor;

    @Autowired
    ApplicationContext context;

    PaymentmentInitWrapper paymentmentInitWrapper;

    IncomingPaymentEventsMQListener messageListener;

    // helper method
    private String getPaymentInitWrapper(PaymentmentInitWrapper paymentmentInitWrapper) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            return null;
        }
    }

    @Before
    public void setup() {
        messageListener = context.getBean(IncomingPaymentEventsMQListener.class);
    }

    @Test
    public void testOnMessageSuccess() {

        PaymentInitWrapper paymentmentInitWrapper = new PaymentInitWrapper();
        String text = getPaymentInitWrapper(paymentmentInitWrapper);
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(text);

        messageListener.onMessage(textMessage);

        verify(eventManager, times(1)).pushEvent(EventStage.FWD_FLOW_PARSE_SUCCESS, any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(1)).requestResponseFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void testOnMessageRecoverableError() {

        PaymentInitWrapper paymentmentInitWrapper = new PaymentInitWrapper();
        paymentmentInitWrapper.addRecoverableError("SOME ERROR"); // << add one from the constants
        String text = getPaymentInitWrapper(paymentmentInitWrapper);
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(text);

        messageListener.onMessage(textMessage);

        verify(eventManager, times(1)).pushEvent(EventStage.FWD_FLOW_PARSE_RECOVERABLE_ERROR, any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(1)).requestResponseFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void testOnMessageIrrecoverableError() {

        PaymentInitWrapper paymentmentInitWrapper = new PaymentInitWrapper();
        paymentmentInitWrapper.addIrrecoverableError("SOME ERROR"); // << add one from the constants
        String text = getPaymentInitWrapper(paymentmentInitWrapper);
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(text);

        messageListener.onMessage(textMessage);

        verify(eventManager, times(1)).pushEvent(EventStage.FWD_FLOW_PARSE_IRRECOVERABLE_ERROR, any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(0)).requestResponseFlow(any(PaymentInitWrapper.class)); // for irrecoverable error, should not be invoked
    }
}