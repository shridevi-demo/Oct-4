

@SpringBootTest
@RunWith(SpringRunner.class)
public class ValidationServiceTest {

    @Autowired
    ValidationService validationService;

    @Mock
    PaymentInitWrapper paymentInitWrapper;

    @Test
    public void testValidateRequestSuccess() {
        try {
            when(paymentInitWrapper.getMessageFormat()).thenReturn(PaymentConstants.ISO_MESSAGE_FORMAT_E003_E004);
        
            PaymentInitWrapper funcR = validationService.validateRequest(paymentInitWrapper);
        
            assertEqual(funcR, paymentInitWrapper);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidateRequestFailure() {
        try {
            when(paymentInitWrapper.getMessageFormat()).thenReturn("SOME_INVALID_FORMAT");
        
            PaymentInitWrapper funcR = validationService.validateRequest(paymentInitWrapper);
        
            fail("Should have failed with invalid message format");
        } catch(paymentInitException e) {
            // success, expected, do nothing. since passed an invalid value
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidateResponseSuccess() {
        try {
            //when(paymentInitWrapper.getEventResponseAsJSON()).thenReturn("{\"\": \"\"}"); // << add a json which can be parse as JsonNode
            when(paymentInitWrapper.getEventResponseAsJSON()).thenReturn(null);
    
            PaymentInitWrapper funcR = validationService.validateResponse(paymentInitWrapper);
    
            assertEqual(funcR, paymentInitWrapper);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidateResponseFailure() {
        try {
            when(paymentInitWrapper.getEventResponseAsJSON()).thenReturn("{\"random\": \"json\"}");
        
            PaymentInitWrapper funcR = validationService.validateResponse(paymentInitWrapper);
        
            fail("Should have failed with invalid json");
        } catch(paymentInitException e) {
            // success, expected, do nothing. since passed an invalid value
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
