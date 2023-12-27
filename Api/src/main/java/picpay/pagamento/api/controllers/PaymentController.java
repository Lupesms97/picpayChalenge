package picpay.pagamento.api.controllers;


import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import picpay.pagamento.api.dto.PaymentOrderDto;
import picpay.pagamento.api.dto.ResponsePaymentTryDto;
import picpay.pagamento.api.enums.Status;
import picpay.pagamento.api.exeptionhandle.CanPayStoreException;

import picpay.pagamento.api.service.WalletService;


@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final WalletService walletService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody final PaymentOrderDto paymentOrderDto)  {
        Status response = Status.FAILED; // Inicializa com um valor padrão
        ResponsePaymentTryDto responsePaymentTryDto;

        try {
            response = walletService.paymentExecution(paymentOrderDto);
            responsePaymentTryDto = new ResponsePaymentTryDto(response.toString(), response, null);
        } catch (CanPayStoreException e) {
            responsePaymentTryDto = new ResponsePaymentTryDto(response.toString(), Status.FAILED, e.getMessage());
            return new ResponseEntity<>(responsePaymentTryDto, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(responsePaymentTryDto, HttpStatus.OK);
    }

}
