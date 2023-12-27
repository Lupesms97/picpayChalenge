package picpay.pagamento.api.dto;

import picpay.pagamento.api.enums.Status;

public record ResponsePaymentTryDto(String response, Status status, String messageError) {
}
