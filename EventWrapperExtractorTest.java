

@SpringBootTest
@RunWith(SpringRunner.class)
public void EventWrapperExtractorTest {

    /**
        This is a simple object with a helper class.
        Doesn't have any dependencies either.
     */
    EventWrapperExtractor eventWrapperExtractor;
    EventWrapper eventWrapper;

    @Mock
    ValueCollector collector;

    @Before
    public void setup() {
        eventWrapperExtractor = new EventWrapperExtractor();
        eventWrapper = new EventWrapper();
        // also reset mock.
        Mockito.reset(collector);
    }

    @Test
    public void testSuccessfullExtract() {

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