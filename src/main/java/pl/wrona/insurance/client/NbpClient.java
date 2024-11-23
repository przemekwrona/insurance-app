package pl.wrona.insurance.client;

import jakarta.annotation.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import pl.wrona.nbp.api.ExchangeratesApi;

@Resource
@FeignClient(value = "${nbp.client.name}", url = "${nbp.client.url}")
public interface NbpClient extends ExchangeratesApi {
}
