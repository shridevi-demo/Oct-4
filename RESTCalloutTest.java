

@SpringBootTest
@RunWith(SpringRunner.class)
public class RESTCalloutTest {

    @Autowired
    RESTCallout restcallout;

    @Mock
    PaymentInitWrapper paymentInitWrapper;

    @Mock
    EventParams eventParams;

    @Before
    public void setup() {
        when(paymentInitWrapper).getFlowID().thenReturn("3132");
        when(paymentInitWrapper).getParentFlowID().thenReturn("313");
        when(paymentInitWrapper).getEventID().thenReturn("32");
        when(paymentInitWrapper).getInstrId().thenReturn("320");
        when(paymentInitWrapper).getTxId().thenReturn("33220");
    }

    @Test
    public void test() {

    } // succeed - when no error while executing
    // fails - there is some error

    @Test(expected = ConnectException.class)
    public void assert_handle_incorrect_url() {
        restcallout.sendEventRequest(paymentInitWrapper, eventParams, "//nonexisting/url",
            "{\"someparam\": \"somevalue\"}", 1);
    }
}