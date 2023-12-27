package picpay.pagamento.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import picpay.pagamento.api.enums.UserType;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private String identifier;

    private String name;

    private String password;

    @Column( unique = true)
    private String email;

    @OneToOne(mappedBy = "userWallet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String token;

    private LocalDateTime created_at ;

    private LocalDateTime updated_at ;

    private LocalDateTime deleted_at ;

    public User(final String identificador, final String name, final String email, final UserType userType, String password) {
        this.identifier = identificador;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.token =  UUID.randomUUID().toString();
        this.password = password;
    }




}
