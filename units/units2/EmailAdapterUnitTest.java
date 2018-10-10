
@RunWith(SpringRunner.class)
public class EmailAdapterUnitTest {

    EmailAdapter emailAdapter;

    @Before
    public void setup() {
        emailAdapter = new EmailAdapter();
    }

    @Test
    public void testProcessForChannel() {
        
        PaymentInitWrapper paymentInitWrapper = new PaymentInitWrapper();
        EventParams eventParams = new EventParams();
        
        CompletableFuture<PaymentInitWrapper> result = null;
        try {
            result = emailAdapter.processForChannel(paymentInitWrapper, eventParams);
        } catch (Exception e) {
            fail("Failed with: " + e.getMessage());
        }
        if (result == null) fail("Null received");
        assertEquals(paymentInitWrapper, result.get());
    }
}