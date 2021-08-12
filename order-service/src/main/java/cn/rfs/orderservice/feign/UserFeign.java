package cn.rfs.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("user-service")
public interface UserFeign {
    @GetMapping("/user/get")
    String getUser();
}
