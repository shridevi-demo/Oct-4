

@RunWith(SpringRunner.class)
public class RESTAPIAdapterUnitTest {

    @Mock
    RESTCallout restCallout;

    @Mock
    PaymentInitWrapper paymentInitWrapper;

    @Mock
    EventParams eventParams;

    RESTAPIAdapter restAPIAdapter;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        restAPIAdapter = new RESTAPIAdapter();
        restAPIAdapter.restCallout = restCallout;
    }

    @Test
    public void testProcessForChannel() {
        
        when(paymentInitWrapper.getFlowID()).thenReturn("320");
        when(paymentInitWrapper.getParentID()).thenReturn("e3");
        // ...
        // ... return for all calls made within the function
        // similarly, do mock methods for Eventparams object, if present
        when(eventParams.getClientAPI()).thenReturn("http://somelink/");

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(URI.create("http://somelink/"));
        responseHeaders.set("MyResponseHeader", "MyValue");
        ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"lat\":\"12.9338637\",\"lng\":\"77.6210146\"}", responseHeaders, HttpStatus.OK);
        when(restCallout.sendEventRequest(any(PaymentInitWrapper.class),
                                    any(EventParams.class),
                                    anyString(),
                                    anyString(),
                                    anyInt())).thenReturn(responseEntity);

        restAPIAdapter.processForChannel(paymentInitWrapper, eventParams);

        verify(paymentInitWrapper, times(1)).setEventResponseAsJSON(any(String.class));
        verify(paymentInitWrapper, times(1)).setResponseReceivedTime(any(TimeStamp.class));
    }
}