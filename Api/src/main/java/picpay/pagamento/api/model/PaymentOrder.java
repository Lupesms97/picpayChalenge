package picpay.pagamento.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_order_id")
    private String paymentOrderId;

    private String emailPayee;

    private String emailPayer;

    private Double value;
}
