

@SpringBootTest
@RunWith(SpringRunner.class)
public class IncomingPaymentEventsMQListenerTest {

    // 1. Use @Mock instead of @MockBeans for mocking dependencies
    
    @Mock // @MockBean
    EventManagerImpl eventManager;

    @Mock // @MockBean
    ParseServices parseServicesWrapper;

    @Mock // @MockBean
    EventFlowProcessor eventFlowProcessor;

    // our target class to test
    // 2. Create object of class under test and assign this mock dependencies to the class
    @InjectMocks
    IncomingPaymentEventsMQListener messageListener;

    // the above line automatically creates an object and injects the mocks to appropriate fields
    // for better understanding, the above line is equivalent to writing
    // messageListener = new IncomingPaymentEventsMQListener();
    // messageListener.eventManager = eventManager;
    // messageListener.parseServiceWrapper = parseServicesWrapper;
    // messageListener.EventFlowProcessor = eventFlowProcessor;

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
        // nothing
        //messageListener = context.getBean(IncomingPaymentEventsMQListener.class);
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