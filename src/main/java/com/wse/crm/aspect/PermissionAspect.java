package com.wse.crm.aspect;

import com.wse.crm.annotation.RequirePermission;
import com.wse.crm.exceptions.NoLoginException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionAspect {

    @Resource
    private HttpSession session;


    @Around(value = "@annotation(com.wse.crm.annotation.RequirePermission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 判断当前登录用户是否拥有权限
        List<String> permissions = (List<String>) session.getAttribute("permissions");
        // 如果没有权限，则抛出异常
        if (permissions == null || permissions.size() == 0) {
            throw  new NoLoginException();
        }

        // 判断目标方法上注解设置的权限码
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        // 得到目标方法上的注解
        RequirePermission requirePermission = methodSignature.getMethod().getDeclaredAnnotation(RequirePermission.class);
        //判断当前用户是否拥有该权限码
        if (!permissions.contains(requirePermission.code())) {
            throw  new NoLoginException();
        }

        // 必须调用该方法，目标方法才会被执行
        Object object = pjp.proceed();
        return object;
    }

}
