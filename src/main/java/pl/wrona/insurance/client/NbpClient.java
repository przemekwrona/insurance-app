package pl.wrona.insurance.client;

import jakarta.annotation.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import pl.wrona.insurance.api.ExchangeApi;

@Resource
@FeignClient(value = "${nbp.client.name}", url = "${nbp.client.url}")
public interface NbpClient extends ExchangeApi {
}
