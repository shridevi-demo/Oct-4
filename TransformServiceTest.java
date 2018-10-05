
@SpringBootTest
@RunWith(SpringRunner.class)
class TransformServiceTest {

    @Autowired
    TransformService transformService;

    @Mock
    PaymentInitWrapper paymentInitWrapper;
    // ^^ I see u used @MockBean there. @MockBean is to mock beans.
    // For normal object you can simple use @Mock

    @Test
    public void testTransFormServiceSuccess() {
        // used to decide flow
        when(paymentInitWrapper.getEventCode()).thenReturn(PaymentConstants.EVENT_CODE_E001);
        // used to populate event
        when(paymentInitWrapper.getRequestId()).thenReturn("21347329873489");
        when(paymentInitWrapper.getPaymentProduct().thenReturn("RTP");
        when(paymentInitWrapper.getPaymentMessage()).thenReturn("messages from xml");
        when(paymentInitWrapper.getMessageFormat()).thenReturn("pacs.008.001.06");
        when(paymentInitWrapper.getEventID()).thenReturn("2332342432432");
        when(paymentInitWrapper.getEventType()).thenReturn("RECVD_CREDIT_TRANSFER");
        when(paymentInitWrapper.getEventCategory()).thenReturn("PAYMNTTRAN");
        when(paymentInitWrapper.getCreationTime()).thenReturn(new Timestamp(System.currentTimeMillis()));

        try {
            PaymentInitWrapper result = transformService.transformMessage(paymentInitWrapper);
            // we verify, a client request was formed out of this info and passed to the mock supplied
            verify(paymentInitWrapper, times(1)).setClientReqObject(any(ClientRequest.class));
        } catch(PayloadTransformException e) {
            fail("Should have passed but failed with " + e.getMessage());
        }
    }

    @Test
    public void testTransFormServiceNull() {
        // used to decide flow
        when(paymentInitWrapper.getEventCode()).thenReturn(null);
        try {
            PaymentInitWrapper result = transformService.transformMessage(paymentInitWrapper);
            fail("Should have failed, but passed with a null event code!");
        } catch(PayloadTransformException e) {
            // do nothing, as it was expected
        }
    }

    @Test
    public void testTransFormServiceUnknownEvent() {
        // used to decide flow
        when(paymentInitWrapper.getEventCode()).thenReturn("UNKNOWN_EVENT");
        try {
            PaymentInitWrapper result = transformService.transformMessage(paymentInitWrapper);
            fail("Should have failed, but passed with an unknown event code!");
        } catch(PayloadTransformException e) {
            // do nothing, as it was expected
        }
    }
}
