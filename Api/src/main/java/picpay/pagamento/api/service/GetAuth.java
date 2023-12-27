package picpay.pagamento.api.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import picpay.pagamento.api.dto.AuthorizationResponseDto;

@FeignClient(name = "auth", url = "http://localhost:8080/authorization")
public interface GetAuth {

    @RequestMapping(method = RequestMethod.GET, value = "/getAuthorization",produces = "application/json")
    AuthorizationResponseDto getAuthorization(@RequestHeader("Token1") String token1, @RequestHeader("Token2") String token2);
}
