package picpay.pagamento.api.exeptionhandle;

public class TypeMismatchException extends RuntimeException {
    public TypeMismatchException(String message) {
        super(message);
    }
}
