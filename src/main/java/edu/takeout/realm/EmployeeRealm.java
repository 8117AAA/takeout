package edu.takeout.realm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.takeout.entity.Employee;
import edu.takeout.service.EmployeeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class EmployeeRealm extends AuthorizingRealm {

    @Override
    public String getName() {
        return "EmployeeRealm";
    }

    @Autowired
    private EmployeeService employeeService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Subject subject = SecurityUtils.getSubject();
        Employee employee = (Employee) subject.getPrincipal();
        Set<String> roles = new HashSet<>();
        roles.add(employee.getRoles());
        return new SimpleAuthorizationInfo(roles);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        Employee employee = employeeService.getOne(new LambdaQueryWrapper<Employee>().eq(Employee::getUsername, username));
        if (employee == null) return null;
        return new SimpleAuthenticationInfo(employee,employee.getPassword(),getName());
    }
}
