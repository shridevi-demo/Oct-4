

@SpringBootTest
@RunWith(SpringRunner.class)
public class EnrichmentServiceTest {

    @Autowired
    EnrichmentServicesImpl enrichmentService;

    // @Mock
    // PaymentInitWrapper paymentInitWrapper;

    @Test
    public void testEnrichRequest() {
        PaymentInitWrapper newPaymentWrappper = new PaymentInitWrapper();
        PaymentInitWrapper funcR = enrichmentService.enrichRequest(newPaymentWrappper);
        //PaymentInitWrapper funcR = enrichmentService.enrichRequest(paymentInitWrapper);
        assertEqual(funcR, newPaymentWrappper);
        //assertEqual(funcR, paymentInitWrapper);
    }

    @Test
    public void testEnrichResponse() {
        PaymentInitWrapper newResponsePaymentWrappper = new PaymentInitWrapper();
        PaymentInitWrapper funcR = enrichmentService.enrichResponset(newResponsePaymentWrappper);
        assertEqual(funcR, newResponsePaymentWrappper);
    }
}
