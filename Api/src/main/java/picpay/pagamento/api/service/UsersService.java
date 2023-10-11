package picpay.pagamento.api.service;

import picpay.pagamento.api.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import picpay.pagamento.api.exeptionhandle.UserNotFoundException;
import picpay.pagamento.api.model.PaymentOrder;
import picpay.pagamento.api.model.Transaction;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.model.Wallet;
import picpay.pagamento.api.repositories.TransactionRepository;
import picpay.pagamento.api.repositories.UsersRepository;

import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    WalletService walletService;

    @Autowired
    TransactionRepository transactionRepository;





    public User createUser(User user){
        return usersRepository.save(user);
    }

    public Wallet createWallet(User user){
        return walletService.createWallet(user);
    }



    public Status payment(PaymentOrder paymentOrder){

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

        if(Status.COMPLETED.equals(status)) {

            return status;

        }else {
            throw new RuntimeException("Payment not completed");
        }

    }


}
