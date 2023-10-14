package picpay.pagamento.api.service;

import picpay.pagamento.api.enums.Status;
import org.springframework.stereotype.Service;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;
import picpay.pagamento.api.exeptionhandle.UserNotFoundException;
import picpay.pagamento.api.model.PaymentOrder;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.repositories.TransactionRepository;
import picpay.pagamento.api.repositories.UsersRepository;

import java.util.Optional;

@Service
public class UsersService {
    final
    UsersRepository usersRepository;

    final
    WalletService walletService;

    final
    TransactionRepository transactionRepository;

    final
    GetAuth getAuth;

    public UsersService(UsersRepository usersRepository, WalletService walletService, TransactionRepository transactionRepository, GetAuth getAuth) {
        this.usersRepository = usersRepository;
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
        this.getAuth = getAuth;
    }


    public String getGetAuthMethod() {
        return getAuth.getAuthorization();
    }



    public Status payment(PaymentOrder paymentOrder) throws CanPayStoreException {

        User reciver;
        User payer;

        Optional<User> OptionalReciver = usersRepository.findUserByEmail(paymentOrder.getEmailPayee());
        Optional<User> OptionalPayer = usersRepository.findUserByEmail(paymentOrder.getEmailPayer());


        if(OptionalReciver.isPresent()&& OptionalPayer.isPresent()){
            reciver = OptionalReciver.get();
            payer = OptionalPayer.get();
        }else {
            throw new UserNotFoundException("User(s) not found");
        }

        Status status = walletService.paymentExecution(payer, reciver, paymentOrder);

        getGetAuthMethod();


        if(Status.COMPLETED.equals(status)) {

            return status;

        }else {
            throw new RuntimeException("Payment not completed");
        }

    }


}
