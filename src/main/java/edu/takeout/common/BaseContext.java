package edu.takeout.common;

import org.apache.shiro.SecurityUtils;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
//        return threadLocal.get();
        Long uid = (Long) SecurityUtils.getSubject().getSession().getAttribute("user");
        if(uid != null) return uid;
        return (Long) SecurityUtils.getSubject().getSession().getAttribute("employee");
    }

}
