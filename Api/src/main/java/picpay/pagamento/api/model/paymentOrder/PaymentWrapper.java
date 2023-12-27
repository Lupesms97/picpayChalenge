package picpay.pagamento.api.model.paymentOrder;

import com.fasterxml.jackson.core.io.BigDecimalParser;
import lombok.RequiredArgsConstructor;
import picpay.pagamento.api.dto.PaymentOrderDto;
import picpay.pagamento.api.dto.PaymentOrderWrapper;
import picpay.pagamento.api.exeptionhandle.UserNotFoundException;
import picpay.pagamento.api.model.User;
import picpay.pagamento.api.repositories.UsersRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;


@RequiredArgsConstructor
public  class PaymentWrapper {

    private final UsersRepository usersRepository;

    public  PaymentOrderWrapper fromPaymentOrderDto(PaymentOrderDto paymentOrderDto){


        String payerKey = paymentOrderDto.emailPayer();
        String payeeKey = paymentOrderDto.emailPayee();

        HashMap<String, User> payeeMap =  getUserFromIdentifier(paymentOrderDto.emailPayee());
        HashMap<String, User> payerMap = getUserFromIdentifier(paymentOrderDto.emailPayer());


        User payer = payerMap.get(payerKey);
        User payee = payeeMap.get(payeeKey);
        BigDecimal payment = BigDecimalParser.parse(String.valueOf(paymentOrderDto.value()));

        PaymentOrder p = PaymentOrder.builder()
                .emailPayee(payee.getEmail())
                .emailPayer(payer.getEmail())
                .value(payment)
                .build();


        return new PaymentOrderWrapper(payer, payee, p);

    }

    public HashMap<String,User> getUserFromIdentifier(String identifier){
        HashMap<String,User> users = new HashMap<>();
        Optional<User> user = usersRepository.findById(identifier);
        if(user.isPresent()){
            users.put(identifier,user.get());
            return users;
        }else{
            throw new UserNotFoundException("'User' not found");
        }
    }
}
