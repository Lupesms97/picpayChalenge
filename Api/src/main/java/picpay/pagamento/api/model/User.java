package picpay.pagamento.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import picpay.pagamento.api.enums.TypeWallet;
import picpay.pagamento.api.enums.UserType;

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

    @Enumerated(EnumType.STRING)
    private UserType userType;

    public User(long identificador, String name, String email, UserType userType) {
        this.identificador = identificador;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

}
