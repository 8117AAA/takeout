package edu.takeout.controller;

import edu.takeout.common.MyUsernamePasswordToken;
import edu.takeout.common.Result;
import edu.takeout.common.ValidateCodeUtils;
import edu.takeout.entity.User;
import edu.takeout.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("发送验证码")
    @PostMapping("/sendMsg")
    Result<String> sendMsg(@RequestBody User user){
        if (StringUtils.isNotEmpty(user.getPhone())){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.debug(user.getPhone()+"'s validate code : "+code);
            return Result.success("validate code send!");
        }
        return Result.error("fail to send validate code");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    Result<User> login(@RequestBody User user){
        Subject subject = SecurityUtils.getSubject();
        MyUsernamePasswordToken token = new MyUsernamePasswordToken();
        token.setIu(true);
        token.setUsername(user.getPhone());
        token.setPassword("".toCharArray());
        subject.login(token);
        User u = (User) subject.getPrincipal();
        subject.getSession().setAttribute("user",u.getId());
        return Result.success(u);
    }
}
