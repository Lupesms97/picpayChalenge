package picpay.pagamento.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import picpay.pagamento.api.enums.Status;
import picpay.pagamento.api.model.paymentOrder.PaymentOrder;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private String id;

    private Instant date;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne
    @JoinColumn(name = "payment_order_id")
    private PaymentOrder paymentOrder;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    private Transaction(final PaymentOrder paymentOrder, final Status status) {
        this.status = status;
        this.paymentOrder = paymentOrder;
        this.date = Instant.now();
    }

    public static Transaction createTransaction(final PaymentOrder paymentOrder, final Status status){
        return new Transaction(paymentOrder, status);
    }


}
