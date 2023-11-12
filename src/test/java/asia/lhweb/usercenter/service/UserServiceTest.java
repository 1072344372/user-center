package asia.lhweb.usercenter.service;
import java.util.Date;

import asia.lhweb.usercenter.model.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author 罗汉
 * @date 2023/11/13
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testAddUser(){
        User user = new User();
        user.setUsername("lh");
        user.setUserAccount("123");
        user.setGender(0);
        user.setAvatarUrl("null");
        user.setUserPassword("123");
        user.setEmail("123");
        user.setUserStatus(0);
        user.setPhone("123");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        boolean res = userService.save(user);
        System.out.println(user.getId());
        System.out.println(res);

        assertTrue(res);

    }

    @Test
    void userRegister() {
        String userAccount = "luohan";
        String userPassword = "";
        String checkPassword = "123456";
        //效验密码不能为空
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        //
        userAccount = "lh";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        //密码小于8位
        userAccount = "luohan";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        userAccount = "luohan";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        userAccount = "123";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        userAccount = "luohan";


        checkPassword="12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertTrue(result > 0);

    }
}