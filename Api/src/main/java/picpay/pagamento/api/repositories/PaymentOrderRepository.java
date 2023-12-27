package picpay.pagamento.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import picpay.pagamento.api.model.paymentOrder.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
}
