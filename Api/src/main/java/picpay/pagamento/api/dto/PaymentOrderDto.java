package picpay.pagamento.api.dto;

public record PaymentOrderDto(String emailPayer, String emailPayee, String value) {
}
