
@RunWith(SpringRunner.class)
public class EnrichmentServiceUnitTest {

    EnrichmentServicesImpl enrichmentService;

    @Before
    public void setup() {
        enrichmentService = new EnrichmentServicesImpl();
    }

    @Test
    public void testEnrichRequest() {
        PaymentInitWrapper newPaymentWrappper = new PaymentInitWrapper();
        PaymentInitWrapper funcR = enrichmentService.enrichRequest(newPaymentWrappper);
        assertEqual(funcR, newPaymentWrappper);
    }

    @Test
    public void testEnrichResponse() {
        PaymentInitWrapper newResponsePaymentWrappper = new PaymentInitWrapper();
        PaymentInitWrapper funcR = enrichmentService.enrichResponset(newResponsePaymentWrappper);
        assertEqual(funcR, newResponsePaymentWrappper);
    }
}
