package edu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.takeout.common.MyUsernamePasswordToken;
import edu.takeout.common.Result;
import edu.takeout.entity.Employee;
import edu.takeout.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
@Api(tags = "员工管理")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @ApiOperation("员工登录")
    @PostMapping("/login")
    Result<Employee> login(@RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        Subject subject = SecurityUtils.getSubject();
        MyUsernamePasswordToken token = new MyUsernamePasswordToken();
        token.setIu(false);
        token.setUsername(employee.getUsername());
        token.setPassword(password.toCharArray());
        try {
            subject.login(token);
            Employee emp = (Employee) subject.getPrincipal();
            if (emp.getStatus() == 0) {
                return Result.error("user disabled");
            }
            subject.getSession().setAttribute("employee", emp.getId());
            return Result.success(emp);
        } catch (UnknownAccountException e) {
            return Result.error("login fail");
        } catch (IncorrectCredentialsException e) {
            return Result.error("wrong password");
        }
    }

    @ApiOperation("员工注销")
    @PostMapping("/logout")
    Result<String> logout() {
        SecurityUtils.getSubject().logout();
        return Result.success("logout");
    }

    @ApiOperation("添加员工")
    @PostMapping
    Result<String> addEmployee(@RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex(employee.getUsername().getBytes()));
        employee.setRoles("employee");
        boolean r = employeeService.save(employee);
        if (r) {
            return Result.success("add employee successful");
        }
        return Result.error("fail to add employee");
    }

    @ApiOperation("获取员工分页")
    @GetMapping("/page")
    Result<Page> page(int page, int pageSize, String name) {
        Page<Employee> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        wrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(p, wrapper);
        return Result.success(p);
    }

    @ApiOperation("更新员工信息")
    @PutMapping
    Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setUpdateUser((Long) SecurityUtils.getSubject().getSession().getAttribute("employee"));
        if (employeeService.updateById(employee)) {
            return Result.success("update employee info successful");
        }
        return Result.error("fail to update employee info");
    }

    /**
     * get employee info by id
     *
     * @param id
     * @return
     */
    @ApiOperation("获取单个员工信息")
    @GetMapping("/{id}")
    Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null)
            return Result.success(employee);
        return Result.error("no employee info");
    }

}
