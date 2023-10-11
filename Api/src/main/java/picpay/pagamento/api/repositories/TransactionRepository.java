package picpay.pagamento.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import picpay.pagamento.api.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
