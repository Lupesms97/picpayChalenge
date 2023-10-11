package picpay.pagamento.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import picpay.pagamento.api.enums.TypeWallet;

import java.io.Serializable;
import java.util.Queue;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_id")
    private String id;

    private double balance;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private Queue<Transaction> transactions;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User userWallet;

    @Enumerated(EnumType.STRING)
    private TypeWallet typeWallet;

    public void addTransaction(Transaction transaction){
        this.transactions.add(transaction);
    }


}
