package picpay.pagamento.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import picpay.pagamento.api.model.User;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);
}
