
@RunWith(SpringRunner.class)
public class EventFlowProcessorUnitTest {
    
    @Mock
    ValidationService validationService;

    @Mock
    TransformService transformService;

    @Mock
    EnrichmentService enrichmentService;

    @Mock
    EventManagerImpl eventManager;

    @Mock
    Router routerObj;

    @Mock
    MessagePublisher messagePublisher;

    EventFlowProcessor eventFlowProcessor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        eventFlowProcessor = new EventFlowProcessor();
        eventFlowProcessor.validationService = validationService;
        eventFlowProcessor.transformService = transformService;
        eventFlowProcessor.enrichmentService = enrichmentService;
        eventFlowProcessor.eventManager = eventManager;
        eventFlowProcessor.routerObj = routerObj;
        eventFlowProcessor.messagePublisher = messagePublisher;
    }

    @Test 
    public void testRequestResponseFlow() throws Exception {
        
        PaymentInitWrapper paymentInitWrapper = new PaymentInitWrapper();
        
        when(validationService.validateRequest(any())).thenReturn(paymentInitWrapper);
        when(enrichmentService.enrichRequest(any())).thenReturn(paymentInitWrapper);
        when(transformService.transformMessage(any())).thenReturn(paymentInitWrapper);
        when(routerObj.publishToSubscribedChannels(any())).thenReturn(paymentInitWrapper);

        eventFlowProcessor.requestResponseFlow(paymentInitWrapper);

        verify(validationService, times(1)).validateRequest(any(PaymentInitWrapper.class));
        verify(enrichmentService, times(1)).enrichRequest(any(PaymentInitWrapper.class));
        verify(transformService, times(1)).transformMessage(any(PaymentInitWrapper.class));
        verify(routerObj, times(1)).publishToSubscribedChannels(any(PaymentInitWrapper.class));

        verify(eventManager, atLeast(3)).pushEvent(any(EventStage.class), any(PaymentInitWrapper.class));
    }

    @Test 
    public void testRequestResponseFlowFailure() throws Exception {
        
        PaymentInitWrapper paymentInitWrapper = new PaymentInitWrapper();
        PaymentInitWrapper paymentInitWrapperWithError = new PaymentInitWrapper();
        paymentInitWrapperWithError.addIrrecoverableError(EventStage.FWD_FLOW_VALIDATION_IRRECOVERABLE_ERROR.toString()); // <<<<< change this
        
        when(validationService.validateRequest(any())).thenReturn(paymentInitWrapperWithError);
        when(enrichmentService.enrichRequest(any())).thenReturn(paymentInitWrapper);
        when(transformService.transformMessage(any())).thenReturn(paymentInitWrapper);
        when(routerObj.publishToSubscribedChannels(any())).thenReturn(paymentInitWrapper);

        eventFlowProcessor.requestResponseFlow(paymentInitWrapper);

        verify(validationService, times(1)).validateRequest(any(PaymentInitWrapper.class));
        verify(enrichmentService, times(0)).enrichRequest(any(PaymentInitWrapper.class));
        verify(transformService, times(0)).transformMessage(any(PaymentInitWrapper.class));
        verify(routerObj, times(0)).publishToSubscribedChannels(any(PaymentInitWrapper.class));

        verify(eventManager, atLeast(1)).pushEvent(eq(EventStage.FWD_FLOW_VALIDATION_IRRECOVERABLE_ERROR), any(PaymentInitWrapper.class)); // << change this
    }

    @Test 
    public void testRouterFlow() throws Exception {
        
        PaymentInitWrapper requestPaymentInitWrapper = new PaymentInitWrapper();
        EventWrapper eventParamsObj = new EventWrapper();
        requestPaymentInitWrapper.setEventParamsObj(eventParamsObj);

        PaymentInitWrapper responsePaymentInitWrapper = new PaymentInitWrapper();
        
        when(routerObj.publishToAChannels(any(), an())).thenReturn(responsePaymentInitWrapper);

        eventFlowProcessor.routerFlows(requestPaymentInitWrapper);
        
        verify(routerObj, times(1)).publishToAChannels(requestPaymentInitWrapper, eventParamsObj);
    }

    @Test 
    public void testResponseFlow() throws Exception {


        PaymentInitWrapper paymentInitWrapper = new PaymentInitWrapper();
        
        when(validationService.validateResponse(any())).thenReturn(paymentInitWrapper);
        when(enrichmentService.enrichResponse(any())).thenReturn(paymentInitWrapper);
        when(transformService.transformResponseMessage(any())).thenReturn(paymentInitWrapper);
        
        eventFlowProcessor.responseFlow(requestPaymentInitWrapper);
        
        verify(validationService, times(1)).validateResponse(any(PaymentInitWrapper.class));
        verify(enrichmentService, times(1)).enrichResponse(any(PaymentInitWrapper.class));
        verify(transformService, times(1)).transformResponseMessage(any(PaymentInitWrapper.class));

        verify(eventManager, atLeast(3)).pushEvent(any(EventStage.class), any(PaymentInitWrapper.class));
    }
}
