package picpay.pagamento.api.dto;

import picpay.pagamento.api.model.User;
import picpay.pagamento.api.model.paymentOrder.PaymentOrder;

public record PaymentOrderWrapper(User payer, User payee, PaymentOrder paymentOrder) {
}
