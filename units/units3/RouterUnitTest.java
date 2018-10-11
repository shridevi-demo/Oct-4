
@RunWith(SpringRunner.class)
public class RouterUnitTest {

    @Mock
    EventManagerImpl eventStateManager;

    @Mock
    RESTAPIAdapter apiRouterTransform;

    @Mock
    EmailAdapter emailRouterTransform;

    @Mock
    JmsTemplate errorJmsTemplate;

    @Mock
    ResourseLoader resourseLoader;

    Router router;

    // helps waiting till result is fetched
    // from completable future
    private void sleepFor(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        router = new Router();
        router.eventStateManager = eventStateManager;
        router.apiRouterTransform = apiRouterTransform;
        router.emailRouterTransform = emailRouterTransform;
        //router.errorJmsTemplate = errorJmsTemplate; private field!
        //router.resourseLoader = resourseLoader; used anywhere?
    }

    @Test
    public void testPublishToAChannelSuccess() {
        
        PaymentInitWrapper paymentInitWrapper = new PaymentInitWrapper();
        
        EventWrapper eventParamsObj = new EventWrapper();
        eventParamsObj.setClientAPI("//somelink");

        when(apiRouterTransform.processForChannel(any(), any())).thenReturn(CompletableFuture.of(paymentInitWrapper));

        router.publishToAChannel(paymentInitWrapper, eventParamsObj);

        sleepFor(1);

        verify(apiRouterTransform, times(1)).processForChannel(any(), any());
        verify(eventStateManager, times(1)).pushEvent(eq(EventStage.CLIENT_API_SUCCESS), any(PaymentInitWrapper.class));
        verify(eventFlowProcessor, times(1)).pushresposneFlow(any(PaymentInitWrapper.class));
    }

    @Test
    public void testPublishToAChannelWithRecoverableError() {
        
        PaymentInitWrapper paymentInitWrapper = new PaymentInitWrapper();
        paymentInitWrapper.addRecoverableError(Error.CLIENT_RECOVERABLE_ERRPR); // << use one from the error codes file
        
        EventWrapper eventParamsObj = new EventWrapper();
        eventParamsObj.setClientAPI("//somelink");

        when(apiRouterTransform.processForChannel(any(), any())).thenReturn(CompletableFuture.of(paymentInitWrapper));

        router.publishToAChannel(paymentInitWrapper, eventParamsObj);

        sleepFor(1);

        verify(apiRouterTransform, times(1)).processForChannel(any(), any());
        verify(eventStateManager, times(1)).pushEvent(eq(EventStage.CLIENT_API_RECOVERABLE_ERROR), any(PaymentInitWrapper.class));
        //verify(errorJmsTemplate, times(1)).convertAndSend(any(), any());
    }

    @Test
    public void testPublishToAChannelWithIrrecoverableError() {
        
        PaymentInitWrapper paymentInitWrapper = new PaymentInitWrapper();
        paymentInitWrapper.addIrrecoverableError(Error.CLIENT_IRRECOVERABLE_ERRPR); // << use one from the error codes file
        
        EventWrapper eventParamsObj = new EventWrapper();
        eventParamsObj.setClientAPI("//somelink");

        when(apiRouterTransform.processForChannel(any(), any())).thenReturn(CompletableFuture.of(paymentInitWrapper));

        router.publishToAChannel(paymentInitWrapper, eventParamsObj);

        sleepFor(1);

        verify(apiRouterTransform, times(1)).processForChannel(any(), any());
        verify(eventStateManager, times(1)).pushEvent(eq(EventStage.CLIENT_API_IRRECOVERABLE_ERROR), any(PaymentInitWrapper.class));
        //verify(errorJmsTemplate, times(1)).convertAndSend(any(), any());
    }
}