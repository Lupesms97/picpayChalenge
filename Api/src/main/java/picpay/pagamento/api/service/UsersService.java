package picpay.pagamento.api.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import picpay.pagamento.api.dto.AuthorizationResponseDto;
import picpay.pagamento.api.dto.PaymentOrderWrapper;
import picpay.pagamento.api.enums.Status;
import org.springframework.stereotype.Service;
import picpay.pagamento.api.enums.UserType;
import picpay.pagamento.api.exeptionhandle.CanNotCompletPaymentException;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;
import picpay.pagamento.api.exeptionhandle.UserNotFoundException;
import picpay.pagamento.api.model.paymentOrder.PaymentOrder;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.repositories.UsersRepository;

import java.util.HashMap;
import java.util.Optional;

@Transactional
@Service
public class UsersService {
    private final
    UsersRepository usersRepository;

    private final
    GetAuth getAuth;

    @Autowired
    public UsersService(UsersRepository usersRepository, GetAuth getAuth) {
        this.usersRepository = usersRepository;
        this.getAuth = getAuth;
    }


    public AuthorizationResponseDto getGetAuthMethod(String tokenReciver, String tokenPayer) {
        return getAuth.getAuthorization(tokenReciver, tokenPayer);
    }



//    public Status payment(PaymentOrder paymentOrder) throws CanPayStoreException {
//
//        User reciver;
//        User payer;
//
//        Optional<User> OptionalReciver = usersRepository.findUserByEmail(paymentOrder.getEmailPayee());
//        Optional<User> OptionalPayer = usersRepository.findUserByEmail(paymentOrder.getEmailPayer());
//
//
//
//        reciver = OptionalReciver.orElseThrow(() -> new UserNotFoundException("'User' not found"));
//        payer = OptionalPayer.orElseThrow(() -> new UserNotFoundException("'User' not found"));
//
//        PaymentOrderWrapper paymentOrderWrapper = new PaymentOrderWrapper(payer, reciver, paymentOrder);
//
//
//        Status status = walletService.paymentExecution(paymentOrderWrapper);
//
//       getGetAuthMethod(payer.getToken(), reciver.getToken());
//
//
//        if(Status.COMPLETED.equals(status)) {
//            return status;
//        }else {
//            throw new CanNotCompletPaymentException("Payment not completed");
//        }
//
//    }

    public User creatNewUser(final String identifier,final String name,final String email,final String userType, String password){
        String passwordEncoded = encodePassword(password);
        try {
            UserType parsedUserType = UserType.valueOf(userType.toUpperCase());
            User user = new User(identifier, name, email, parsedUserType, passwordEncoded);
            usersRepository.save(user);
            return user;
        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException("'User Type' not correct: " + userType);
        }
    }


    public void deleteUser(final String identifier){
        Optional<User> user = usersRepository.findById(identifier);
        if(user.isPresent()){
            usersRepository.delete(user.get());
        }else{
            throw new UserNotFoundException("'User' not found");
        }
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    private boolean checkPassword(String password, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(password, encodedPassword);
    }




}
