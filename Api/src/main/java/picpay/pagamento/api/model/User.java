package picpay.pagamento.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id", unique = true)
    // CPF ou CNPJ
    private long identificador;

    private String name;

    @Column( unique = true)
    private String email;

    @OneToOne(mappedBy = "userWallet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Wallet wallet;

    public User(long identificador, String name, String email) {
        this.identificador = identificador;
        this.name = name;
        this.email = email;
    }

}
