

@SpringBootTest
@RunWith(SpringRunner.class)
public class RESTAPIAdapterTest {

    @MockBean
    RESTCallout restCallout;

    @Autowired
    RESTAPIAdapter restAPIAdapter;

    @Mock
    PaymentInitWrapper paymentInitWrapper;

    @Mock
    EventParams eventParams;
    
    // incase you need to reset mocks before every test
    // use this trick
    // @Before
    // public void setup() {
    //     Mockito.injectMock(this);
    // }

    @Test
    public void testProcessForChannel() {
        
        when(paymentInitWrapper.getFlowID()).thenReturn("320");
        when(paymentInitWrapper.getParentID()).thenReturn("e3");
        // ...
        // ... return for all calls made within the function
        // similarly, do mock methods for Eventparams object, if present
        when(eventParams.getClientAPI()).thenReturn("//someapilink");

        // https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        responseHeaders.set("MyResponseHeader", "MyValue");
        ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"lat\":\"12.9338637\",\"lng\":\"77.6210146\"}", responseHeaders, HttpStatus.OK);
        when(restCallout.sendEventRequest(any(PaymentInitWrapper.class),
                                    any(EventParams.class),
                                    anyString(),
                                    anyString(),
                                    anyInt())).thenReturn(responseEntity);

        restAPIAdapter.processForChannel(paymentInitWrapper, eventParams);
        
        // verify
        verify(paymentInitWrapper, times(1)).setEventResponseAsJSON(any(String.class));
        verify(paymentInitWrapper, times(1)).setResponseReceivedTime(any(TimeStamp.class));
    }

    @Test
    public void testProcessForChannelForInvalidResponse() {
        
        when(paymentInitWrapper.getFlowID()).thenReturn("320");
        when(paymentInitWrapper.getParentID()).thenReturn("e3");
        // ...
        // ... return for all calls made within the function
        // similarly, do mock methods for Eventparams object, if present
        when(eventParams.getClientAPI()).thenReturn("//someapilink");

        when(restCallout.sendEventRequest(any(PaymentInitWrapper.class),
                                    any(EventParams.class),
                                    anyString(),
                                    anyString(),
                                    anyInt())).thenThrow(ConnectException.class);

        restAPIAdapter.processForChannel(paymentInitWrapper, eventParams);
        
        // verify
        verify(paymentInitWrapper, times(0)).setEventResponseAsJSON(any(String.class));
        verify(paymentInitWrapper, times(0)).setResponseReceivedTime(any(TimeStamp.class));

        //.. more test verification if needed
    }


    // test for null

    //
}