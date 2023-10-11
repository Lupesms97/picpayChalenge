package picpay.pagamento.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import picpay.pagamento.api.enums.Status;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private String id;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne
    @JoinColumn(name = "payment_order_id")
    private PaymentOrder paymentOrder;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    public Transaction(PaymentOrder paymentOrder, Status status) {
        this.date = LocalDateTime.now();
        this.status = status;
        this.paymentOrder = paymentOrder;
    }


}
