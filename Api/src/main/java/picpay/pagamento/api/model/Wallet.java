package picpay.pagamento.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Queue;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_id")
    private String id;

    private BigDecimal balance;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private Queue<Transaction> transactions;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User userWallet;

    public void addTransaction(final Transaction transaction){
        this.transactions.add(transaction);
    }

    private Wallet( User userWallet) {
        this.balance = BigDecimal.ZERO;
        this.userWallet = userWallet;
    }

    public static Wallet createWallet(final User userWallet){
        return new Wallet(userWallet);
    }
}
