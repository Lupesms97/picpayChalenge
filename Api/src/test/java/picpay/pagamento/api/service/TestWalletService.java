package picpay.pagamento.api.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.model.Wallet;
import picpay.pagamento.api.repositories.TransactionRepository;
import picpay.pagamento.api.repositories.UsersRepository;
import picpay.pagamento.api.repositories.WalletRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TestWalletService {


    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveNewBalance() {
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setUserWallet(user);
        double newBalance = 100.0;

        Mockito.when(walletRepository.findWalletByUserWallet(user)).thenReturn(Optional.of(wallet));
        Mockito.when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet result = walletService.saveNewBalance(user, newBalance);

        assertEquals(newBalance, result.getBalance(), 0.001); // Use a precisão apropriada
    }

    @Test
    void testCreateWallet() {
        User user = new User();
        user.setIdentificador(1L);
        user.setWallet(null);

        Mockito.when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(usersRepository.save(user)).thenReturn(user);

        Wallet result = walletService.createWallet(user);

        assertEquals(user, result.getUserWallet());
    }

    @Test
    void testCreateWalletUserAlreadyHasWallet() {
        User user = new User();
        user.setIdentificador(1L);
        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        Mockito.when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> walletService.createWallet(user));
    }

    // Adicione mais testes para paymentExecution de acordo com diferentes cenários
}


