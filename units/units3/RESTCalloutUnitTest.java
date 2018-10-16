
@Runwith(SpringRunner.java)
public class RESTCalloutUnitTest {

    RESTCallout restCallout;

    @Before
    public void setup() {
        restCallout = new RESTCallout();
    }

    @Test(expected = AuthenticationException.class)
    public void testOathSecurityToken() {
        SecurityParam securityParam = mock(SecurityParam.class);
        when(securityParam.getConsumerKey()).thenReturn("SOME_KEY");
        when(securityParam.getConsumerSecret()).thenReturn("SOME_SECRET");
        when(securityParam.getGrantType()).thenReturn("password");
        
        String token = restCallout.getOAuthSecurityToken(securityParam);

        assertNotNull(token);
    }

    @Test(expected = ConnectException.class)
    public void testEventRequest() {
        
        PaymentInitWrapper paymentInitWrapper = new PaymentInitWrapper();
        paymentInitWrapper.setFlowID("3123");
        paymentInitWrapper.setParentFlowID("313");
        paymentInitWrapper.setEventID("32");
        paymentInitWrapper.setInstrId("320");
        paymentInitWrapper.setTxId("3220");
        paymentInitWrapper.setClientRequestObj(new ClientRequest());

        EventWrapper wrapper = new EventWrapper();
        wrapper.setSecurityType("non-oath");
        wrapper.setSignature("n");
        
        ResponseEntity<String> response = restCallout.sendEventRequest(paymentInitWrapper, wrapper, "//some/url", 0);

        assertNotNull(response);
    }

}