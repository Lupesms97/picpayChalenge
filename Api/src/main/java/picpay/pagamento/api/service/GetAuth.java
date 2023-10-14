package picpay.pagamento.api.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "auth", url = "http://localhost:8080/authorization")
public interface GetAuth {

    @RequestMapping(method = RequestMethod.GET, value = "/getAuthorization",
    produces = "application/json")
    String getAuthorization();
}
