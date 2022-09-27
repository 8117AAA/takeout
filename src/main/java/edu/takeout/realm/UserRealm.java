package edu.takeout.realm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.takeout.entity.User;
import edu.takeout.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {
    @Override
    public String getName() {
        return "UserRealm";
    }

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, username));
        if (user == null) {
            user = new User();
            user.setPhone(username);
            userService.save(user);
        }
        System.out.println("save");
        System.out.println(user);
        return new SimpleAuthenticationInfo(user, "", getName());
    }
}
