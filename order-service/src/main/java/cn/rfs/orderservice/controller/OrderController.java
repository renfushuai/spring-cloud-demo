package cn.rfs.orderservice.controller;

import cn.rfs.orderservice.feign.UserFeign;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RefreshScope
@RestController
@RequestMapping("order")
public class OrderController {
    @Value("${test}")
    private String test;
    @Autowired
    private UserFeign userFeign;
    @GetMapping("/get")
    public String get(){
        return test+"   "+userFeign.getUser();
    }
}
