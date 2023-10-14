package picpay.pagamento.api.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import picpay.pagamento.api.enums.Status;
import picpay.pagamento.api.enums.UserType;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;
import picpay.pagamento.api.model.PaymentOrder;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.model.Wallet;
import picpay.pagamento.api.repositories.TransactionRepository;
import picpay.pagamento.api.repositories.UsersRepository;
import picpay.pagamento.api.repositories.WalletRepository;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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

        when(walletRepository.findWalletByUserWallet(user)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet result = walletService.saveNewBalance(user, newBalance);

        assertEquals(newBalance, result.getBalance(), 0.001); // Use a precisão apropriada
    }

    @Test
    void testCreateWalletShouldBeEqualsWalletUserAndUser() {
        User user = User.builder()
            .identificador(1L)
            .name("User Test")
            .email("test@example.com")
            .userType(UserType.USER_TYPE)
            .build();

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(user)).thenReturn(user);
        when(walletRepository.save(user.getWallet())).thenReturn(user.getWallet());

        Wallet wallet = walletService.createWallet(user);
        user.setWallet(wallet);

        Long walletUser = user.getWallet().getUserWallet().getIdentificador();

        assertEquals(user.getIdentificador(), walletUser);
    }

    @Test
    void testCreateWalletUserAlreadyHasWallet() {
        User user = new User();
        user.setIdentificador(1L);
        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> walletService.createWallet(user));
    }

    @Test
    void testPaymentExecutionShouldNotCompletBecauseIsStore() {
        User user = User.builder()
            .identificador(1L)
            .name("User Test")
            .email("test@example.com")
            .userType(UserType.USER_TYPE)
            .build();

        User store = User.builder()
            .identificador(2L)
            .name("Store Test")
            .email("store@Example.com")
            .userType(UserType.STORE_TYPE)
            .build();

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.save(user)).thenReturn(user);
        when(usersRepository.findById(2L)).thenReturn(Optional.of(store));
        when(usersRepository.save(store)).thenReturn(store);
        when(walletRepository.save(user.getWallet())).thenReturn(user.getWallet());
        when(walletRepository.save(store.getWallet())).thenReturn(store.getWallet());
        when(transactionRepository.save(ArgumentMatchers.any())).thenReturn(true);

        Wallet walletUser = walletService.createWallet(user);
        user.setWallet(walletUser);

        Wallet walletStore = walletService.createWallet(store);
        store.setWallet(walletStore);

        PaymentOrder paymentOrder = PaymentOrder.builder()
            .paymentOrderId(UUID.randomUUID().toString())
            .emailPayer(store.getEmail())
            .emailPayee(user.getEmail())
            .value(100.0d)
            .build();

        assertThrows(CanPayStoreException.class, () -> walletService.paymentExecution(store, user, paymentOrder));
    }
    @Test()
    public void testPaymentExecutionThrowsCanPayStoreException() throws CanPayStoreException {
        // Crie objetos mock para User, Wallet e PaymentOrder
        User payer = mock(User.class);
        User reciver = mock(User.class);
        PaymentOrder paymentOrder = mock(PaymentOrder.class);

        // Defina o tipo de usuário do pagador como UserType.USER_TYPE
        when(payer.getUserType()).thenReturn(UserType.USER_TYPE);

        // Chame o método que você quer testar


        assertThrows(CanPayStoreException.class, () -> walletService.paymentExecution(payer, reciver, paymentOrder));
    }






    // Adicione mais testes para paymentExecution de acordo com diferentes cenários
}


