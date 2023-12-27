package picpay.pagamento.api.exeptionhandle;

public class CanNotCompletPaymentException extends RuntimeException{
    public CanNotCompletPaymentException(String message) {
        super(message);
    }
}
