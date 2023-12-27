package picpay.pagamento.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import picpay.pagamento.api.dto.AuthorizationResponseDto;
import picpay.pagamento.api.dto.PaymentOrderDto;
import picpay.pagamento.api.dto.PaymentOrderWrapper;
import picpay.pagamento.api.enums.Status;
import picpay.pagamento.api.exeptionhandle.CanNotCompletPaymentException;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;
import picpay.pagamento.api.exeptionhandle.UserNotFoundException;
import picpay.pagamento.api.model.paymentOrder.PaymentOrder;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.repositories.UsersRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class UserServiceTeste {

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
    void testPaymentShouldThrowNotCompletException() throws CanPayStoreException {
        // Mock de dados de teste
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setEmailPayee("receiver@example.com");
        paymentOrder.setEmailPayer("payer@example.com");
        paymentOrder.setValue(BigDecimal.valueOf(500));

        PaymentOrderDto paymentOrderDto = new PaymentOrderDto(paymentOrder.getEmailPayer(), paymentOrder.getEmailPayee(),  String.valueOf(paymentOrder.getValue()));

        User receiver = new User();
        User payer = new User();

        Mockito.when(getAuth.getAuthorization(payer.getToken(), receiver.getToken())).thenReturn(new AuthorizationResponseDto("autorizado", Status.COMPLETED));
        Mockito.when(usersRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        Mockito.when(usersRepository.findUserByEmail("payer@example.com")).thenReturn(Optional.of(payer));

        Mockito.when(walletService.paymentExecution(Mockito.any(PaymentOrderWrapper.class))).thenReturn(Status.COMPLETED);


        // Verifica se o status retornado é o esperado
        assertThrows(CanNotCompletPaymentException.class, () -> walletService.paymentExecution(paymentOrderDto));
    }

    @Test
    void testPaymentUserNotFound() {
        // Mock de dados de teste
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setEmailPayee("receiver@example.com");
        paymentOrder.setEmailPayer("payer@example.com");

        PaymentOrderDto paymentOrderDto = new PaymentOrderDto(paymentOrder.getEmailPayer(), paymentOrder.getEmailPayee(), String.valueOf(paymentOrder.getValue()));

        Mockito.when(usersRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.empty());
        Mockito.when(usersRepository.findUserByEmail("payer@example.com")).thenReturn(Optional.empty());

        // Executa o método payment e espera uma exceção UserNotFoundException
        assertThrows(UserNotFoundException.class, () -> walletService.paymentExecution(paymentOrderDto));
    }

    @Test
    void testPaymentNotCompleted() throws CanPayStoreException {
        // Mock de dados de teste
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setEmailPayee("receiver@example.com");
        paymentOrder.setEmailPayer("payer@example.com");

        User receiver = new User();
        User payer = new User();

        PaymentOrderDto paymentOrderDto = new PaymentOrderDto(paymentOrder.getEmailPayer(), paymentOrder.getEmailPayee(), String.valueOf(paymentOrder.getValue()));

        Mockito.when(usersRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        Mockito.when(usersRepository.findUserByEmail("payer@example.com")).thenReturn(Optional.of(payer));

        Mockito.when(walletService.paymentExecution(Mockito.any(PaymentOrderDto.class))).thenReturn(Status.COMPLETED);

        // Executa o método payment e espera uma exceção RuntimeException
        assertThrows(RuntimeException.class, () -> walletService.paymentExecution(paymentOrderDto));
    }
}
