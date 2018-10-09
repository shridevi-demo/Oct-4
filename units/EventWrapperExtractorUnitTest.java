

@RunWith(SpringRunner.class)
public class EventWrapperExtractorTest {

    EventWrapperExtractor eventWrapperExtractor;
    
    @Mock
    ValueCollector collector;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        eventWrapperExtractor = new EventWrapperExtractor();
    }

    @Test
    public void testSuccessfullExtract() {

        EventWrapper eventWrapper = new EventWrapper();
        eventWrapper.MessageID = "32837812321873";
        eventWrapper.ClientID = "321873";

        eventWrapperExtractor.extract(eventWrapper, "MessageID", collector);
        eventWrapperExtractor.extract(eventWrapper, "ClientID", collector);

        // check succesfully invoked for all the call above.
        verify(collector, times(2)).addObject(any());
        // check invoked with correct params
        verify(collector, times(1)).addObject(eventWrapper.MessageID);
        verify(collector, times(1)).addObject(eventWrapper.ClientID);
    }

    @Test
    public void testExtractWithIncorrectArgument() {
        EventWrapper eventWrapper = new EventWrapper();
        eventWrapperExtractor.extract(eventWrapper, "SOME_RANDOM_KEY", collector);
        // should not be invoked, since mapping does not exist
        verify(collector, times(0)).addObject(any());
    }

    @Test
    public void testExtractWithNullArgument() {
        eventWrapperExtractor.extract(eventWrapper, null, collector);
        // should not be invoked, and should not crash either
        verify(collector, times(0)).addObject(any());
    }
}