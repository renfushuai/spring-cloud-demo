package cn.rfs.userservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("user")
public class UserController {
    @Value("${test}")
    private String test;
    @GetMapping("/get")
    public String get(){
        return test;
    }
}
