package cn.rfs.userservice;

import cn.rfs.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UserServiceApplication.class)
class UserServiceApplicationTests {
    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        String user = userService.getUser(1);
        System.out.println(user);
    }

}
