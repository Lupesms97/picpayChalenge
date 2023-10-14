package picpay.pagamento.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import picpay.pagamento.api.enums.Status;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;
import picpay.pagamento.api.exeptionhandle.UserNotFoundException;
import picpay.pagamento.api.model.PaymentOrder;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.repositories.UsersRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TesteUserService {

    @InjectMocks
    private UsersService usersService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private WalletService walletService;

    @Mock
    GetAuth getAuth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPaymentCompleted() throws CanPayStoreException {
        // Mock de dados de teste
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setEmailPayee("receiver@example.com");
        paymentOrder.setEmailPayer("payer@example.com");
        paymentOrder.setValue(500.d);

        User receiver = new User();
        User payer = new User();

        Mockito.when(getAuth.getAuthorization()).thenReturn("Authorizaded");
        Mockito.when(usersRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        Mockito.when(usersRepository.findUserByEmail("payer@example.com")).thenReturn(Optional.of(payer));

        Mockito.when(walletService.paymentExecution(payer, receiver, paymentOrder)).thenReturn(Status.COMPLETED);

        // Executa o método payment
        Status result = usersService.payment(paymentOrder);

        // Verifica se o status retornado é o esperado
        assertEquals(Status.COMPLETED, result);
    }

    @Test
    void testPaymentUserNotFound() {
        // Mock de dados de teste
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setEmailPayee("receiver@example.com");
        paymentOrder.setEmailPayer("payer@example.com");

        Mockito.when(usersRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.empty());
        Mockito.when(usersRepository.findUserByEmail("payer@example.com")).thenReturn(Optional.empty());

        // Executa o método payment e espera uma exceção UserNotFoundException
        assertThrows(UserNotFoundException.class, () -> usersService.payment(paymentOrder));
    }

    @Test
    void testPaymentNotCompleted() throws CanPayStoreException {
        // Mock de dados de teste
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setEmailPayee("receiver@example.com");
        paymentOrder.setEmailPayer("payer@example.com");

        User receiver = new User();
        User payer = new User();

        Mockito.when(usersRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        Mockito.when(usersRepository.findUserByEmail("payer@example.com")).thenReturn(Optional.of(payer));

        Mockito.when(walletService.paymentExecution(payer, receiver, paymentOrder)).thenReturn(Status.FAILED);

        // Executa o método payment e espera uma exceção RuntimeException
        assertThrows(RuntimeException.class, () -> usersService.payment(paymentOrder));
    }
}
