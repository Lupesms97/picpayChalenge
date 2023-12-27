package picpay.pagamento.api.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import picpay.pagamento.api.dto.PaymentOrderWrapper;
import picpay.pagamento.api.enums.UserType;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;
import picpay.pagamento.api.model.paymentOrder.PaymentOrder;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.model.Wallet;
import picpay.pagamento.api.repositories.TransactionRepository;
import picpay.pagamento.api.repositories.UsersRepository;
import picpay.pagamento.api.repositories.WalletRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class WalletServiceTest {


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
        BigDecimal newBalance = BigDecimal.valueOf(100.0d);

        when(walletRepository.findWalletByUserWallet(user)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet result = walletService.saveNewBalance(user,newBalance);

        assertEquals(newBalance, result.getBalance(), String.valueOf(0.001)); // Use a precisão apropriada
    }

    @Test
    void testCreateWalletShouldBeEqualsWalletUserAndUser() {
        User user = User.builder()
            .identifier("17377666714")
            .name("User Test")
            .email("test@example.com")
            .userType(UserType.USER_TYPE)
            .build();

        when(usersRepository.findById("17377666714")).thenReturn(Optional.of(user));
        when(usersRepository.save(user)).thenReturn(user);
        when(walletRepository.save(user.getWallet())).thenReturn(user.getWallet());

        Wallet wallet = walletService.createWallet(user);
        user.setWallet(wallet);

        String walletUser = user.getWallet().getUserWallet().getIdentifier();

        assertEquals(user.getIdentifier(), walletUser);
    }

    @Test
    void testCreateWalletUserAlreadyHasWallet() {
        User user = new User();
        user.setIdentifier("17377666714");
        Wallet wallet = new Wallet();
        user.setWallet(wallet);

        when(usersRepository.findById("17377666714")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> walletService.createWallet(user));
    }

    @Test
    public void testPaymentExecutionThrowsCanPayStoreException() throws CanPayStoreException {
        User payer = mock(User.class);
        User reciver = mock(User.class);
        PaymentOrder paymentOrder = mock(PaymentOrder.class);

        when(payer.getUserType()).thenReturn(UserType.STORE_TYPE);
        payer.setWallet(Wallet.createWallet(payer));

        PaymentOrderWrapper paymentOrderWrapper = new PaymentOrderWrapper(payer, reciver, paymentOrder);

        assertThrows(CanPayStoreException.class, () -> walletService.paymentExecution(paymentOrderWrapper));
    }



}


