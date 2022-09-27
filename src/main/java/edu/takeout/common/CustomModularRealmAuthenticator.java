package edu.takeout.common;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.Collection;

public class CustomModularRealmAuthenticator extends ModularRealmAuthenticator {
    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        assertRealmsConfigured();
        MyUsernamePasswordToken token = (MyUsernamePasswordToken) authenticationToken;
        boolean flag = token.isIu();
        Collection<Realm> realms = getRealms();
        for(Realm realm:realms){
            System.out.println(realm.getName());
            if (flag && realm.getName().contains("UserRealm"))
                return doSingleRealmAuthentication(realm, token);
            if (!flag && realm.getName().contains("EmployeeRealm"))
                return doSingleRealmAuthentication(realm, token);
        }
        System.out.println("========null");
        return null;
    }
}
