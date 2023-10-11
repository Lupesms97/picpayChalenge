package picpay.pagamento.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.model.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String>{
    Optional<Wallet> findWalletByUserWallet(User user);

    Optional<Wallet> findWalletByUserWalletIdentificador(long identificador);



}
