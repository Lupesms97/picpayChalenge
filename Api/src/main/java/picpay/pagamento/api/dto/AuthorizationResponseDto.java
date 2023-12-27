package picpay.pagamento.api.dto;

import picpay.pagamento.api.enums.Status;


public record AuthorizationResponseDto(String token, Status status) {
}
