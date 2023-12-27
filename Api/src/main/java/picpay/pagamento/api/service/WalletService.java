package picpay.pagamento.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import picpay.pagamento.api.dto.AuthorizationResponseDto;
import picpay.pagamento.api.dto.PaymentOrderDto;
import picpay.pagamento.api.dto.PaymentOrderWrapper;
import picpay.pagamento.api.enums.Status;
import picpay.pagamento.api.enums.UserType;
import picpay.pagamento.api.exeptionhandle.CanNotCompletPaymentException;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;
import picpay.pagamento.api.model.paymentOrder.PaymentOrder;
import picpay.pagamento.api.model.Transaction;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.model.Wallet;
import picpay.pagamento.api.model.paymentOrder.PaymentWrapper;
import picpay.pagamento.api.repositories.TransactionRepository;
import picpay.pagamento.api.repositories.UsersRepository;
import picpay.pagamento.api.repositories.WalletRepository;

import java.math.BigDecimal;
import java.util.Optional;


@Transactional
@Service
@RequiredArgsConstructor
public class WalletService {


    private final WalletRepository walletRepository;


    private final TransactionRepository transactionRepository;

    private final UsersService  usersService;


    private final UsersRepository usersRepository;


    private Status paymentExecution(User payer, User reciver, PaymentOrder paymentOrder) throws CanPayStoreException {


        Transaction transaction = new Transaction();
        transaction.setPaymentOrder(paymentOrder);
        transaction.setStatus(Status.PENDING);


        if (payer.getUserType() != UserType.USER_TYPE) {
            transaction.setStatus(Status.FAILED);
            transactionRepository.save(transaction);
            throw new CanPayStoreException("Store can't pay");
        }

        assert payer.getWallet() != null;

        final var payerBalance = payer.getWallet().getBalance();
        final var reciverBalance = reciver.getWallet().getBalance();
        final var paymentValue = paymentOrder.getValue();

        if(payerBalance.compareTo(paymentValue) < 0) {
            transaction.setStatus(Status.CANCELED);
            transactionRepository.save(transaction);
            throw new CanNotCompletPaymentException("Payer don't have enough money");
        }

        final BigDecimal payerNewBalance = payerBalance.subtract(paymentValue);
        final BigDecimal reciverNewBalance = reciverBalance.add(paymentValue);

        return this.getAuthForFinishPayment(transaction, payer, reciver, payerNewBalance, reciverNewBalance);



    }

    private Status getAuthForFinishPayment(
        Transaction transaction,
        User payer,
        User reciver,
        BigDecimal payerNewBalance,
        BigDecimal reciverNewBalance
    ){
        AuthorizationResponseDto authresponse =  usersService.getGetAuthMethod(payer.getToken(), reciver.getToken());
        if (authresponse.status().equals(Status.COMPLETED)){

            transaction.setStatus(Status.COMPLETED);
            transactionRepository.save(transaction);

            this.saveNewBalance(payer, payerNewBalance);
            this.saveNewBalance(reciver, reciverNewBalance);
            return Status.COMPLETED;
        }else{

            transaction.setStatus(Status.FAILED);
            transactionRepository.save(transaction);
            return Status.FAILED;
        }
    }

    public Status paymentExecution(PaymentOrderDto paymentOrderDto) throws CanPayStoreException {

        PaymentWrapper paymentWrapper = new PaymentWrapper(usersRepository);

        PaymentOrderWrapper paymentOrder = paymentWrapper.fromPaymentOrderDto(paymentOrderDto);

        return paymentExecution(paymentOrder.payer(), paymentOrder.payee(), paymentOrder.paymentOrder());

    }
    public Status paymentExecution(PaymentOrderWrapper paymentWrapperDto) throws CanPayStoreException {

        return paymentExecution(paymentWrapperDto.payer(), paymentWrapperDto.payee(), paymentWrapperDto.paymentOrder());

    }
    public Wallet saveNewBalance(final User user, final BigDecimal newBalance){
        Optional<Wallet> walletOptional = walletRepository.findWalletByUserWallet(user);
        Wallet wallet = walletOptional.orElse(Wallet.createWallet(user));
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    public Wallet createWallet(User user){
        User userWithWallet;
        Optional<User> userWithoutWallet = usersRepository.findById(user.getIdentifier());

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






}
