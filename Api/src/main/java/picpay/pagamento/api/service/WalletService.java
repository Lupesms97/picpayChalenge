package picpay.pagamento.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import picpay.pagamento.api.enums.Status;
import picpay.pagamento.api.enums.TypeWallet;
import picpay.pagamento.api.enums.UserType;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;
import picpay.pagamento.api.model.PaymentOrder;
import picpay.pagamento.api.model.Transaction;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.model.Wallet;
import picpay.pagamento.api.repositories.TransactionRepository;
import picpay.pagamento.api.repositories.UsersRepository;
import picpay.pagamento.api.repositories.WalletRepository;

import java.util.Optional;


@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    UsersRepository usersRepository;

    public Wallet saveNewBalance(User user, double newBalance){
        Wallet wallet = walletRepository.findWalletByUserWallet(user).get();
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    public Wallet createWallet(User user){
        User userWithWallet;
        Optional<User> userWithoutWallet = usersRepository.findById(user.getIdentificador());

        if(userWithoutWallet.isPresent() && userWithoutWallet.get().getWallet() == null){
            Wallet wallet = new Wallet();
            userWithWallet = userWithoutWallet.get();
            userWithWallet.setWallet(wallet);
            wallet.setUserWallet(userWithWallet);
        }else{
            throw new RuntimeException("User already have a wallet");
        }
        walletRepository.save(userWithWallet.getWallet());
        usersRepository.save(userWithWallet);

        return userWithWallet.getWallet();

    }

    public Status paymentExecution(User payer, User reciver, PaymentOrder paymentOrder) throws CanPayStoreException {

        Transaction transaction = new Transaction();
        transaction.setPaymentOrder(paymentOrder);
        transaction.setStatus(Status.PENDING);

        if(payer.getWallet()==null && reciver.getWallet()==null) {
            payer.setWallet(this.createWallet(payer));
            reciver.setWallet(this.createWallet(reciver));
        }
        if (payer.getUserType()== UserType.USER_TYPE){
            transaction.setStatus(Status.FAILED);
            transactionRepository.save(transaction);
            throw new CanPayStoreException("Store can't pay");
        }


        assert payer.getWallet() != null;
        if (payer.getWallet().getBalance() >= paymentOrder.getValue()) {
            payer.getWallet().setBalance(payer.getWallet().getBalance() - paymentOrder.getValue());
            reciver.getWallet().setBalance(reciver.getWallet().getBalance() + paymentOrder.getValue());

            this.saveNewBalance(payer, payer.getWallet().getBalance());
            this.saveNewBalance(reciver, reciver.getWallet().getBalance());


            transactionRepository.save(transaction);
            transaction.setStatus(Status.COMPLETED);

            return Status.COMPLETED;

        }else if(payer.getWallet().getBalance() < paymentOrder.getValue()){
            transaction.setStatus(Status.CANCELED);
            transactionRepository.save(transaction);
            return Status.NO_FUNDS;
        }else{
            transaction.setStatus(Status.FAILED);
            transactionRepository.save(transaction);
            return Status.FAILED;
        }

    }



}
