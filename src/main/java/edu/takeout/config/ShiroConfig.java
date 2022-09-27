package edu.takeout.config;

import edu.takeout.common.CustomModularRealmAuthenticator;
import edu.takeout.realm.EmployeeRealm;
import edu.takeout.realm.UserRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean filterFactoryBean(@Qualifier("manager") DefaultWebSecurityManager manager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(manager);
        Map<String, String> map = new LinkedHashMap<>();

        map.put("/backend/**", "anon");
        map.put("/front/**", "anon");
        map.put("/employee/login", "anon");
        map.put("/employee/logout", "anon");
        map.put("/user/login", "anon");
        map.put("/user/sendMsg", "anon");
        map.put("/swagger-ui/**", "anon");

        map.put("/employee","roles[admin]");
        map.put("/employee/**","roles[admin]");

        map.put("/setmeal","roles[admin,employee]");
        map.put("/setmeal/status/**","roles[admin,employee]");

        map.put("/dish","roles[admin,employee]");
        map.put("/dish/status/**","roles[admin,employee]");

        map.put("/category","roles[admin,employee]");

        map.put("/order","roles[admin,employee]");

//        map.put("/**", "authc");
        factoryBean.setFilterChainDefinitionMap(map);
        return factoryBean;
    }


    @Bean
    public DefaultWebSecurityManager manager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setAuthenticator(customModularRealmAuthenticator());
        List<Realm> realms = new ArrayList<>();
        realms.add(employeeRealm());
        realms.add(userRealm());
        manager.setRealms(realms);
        return manager;
    }

    @Bean
    public EmployeeRealm employeeRealm() {
        return new EmployeeRealm();
    }

    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

    @Bean
    public CustomModularRealmAuthenticator customModularRealmAuthenticator() {
        return new CustomModularRealmAuthenticator();
    }
}