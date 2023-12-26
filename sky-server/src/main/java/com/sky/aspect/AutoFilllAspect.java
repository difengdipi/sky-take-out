package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @description： 自定义切面，实现公共字段自动填充处理逻辑
 * @author： 周海
 * @create： 2023/12/26
 */
@Aspect
@Component
@Slf4j
public class AutoFilllAspect {
    
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){
    
    }
    
    /**
     * 设置前置通知，再通知中为公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws Exception {
        log.info("开始进行公共字段的自动填充...");
        
        //1.获取当前被拦截的方法上的数据库类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        
        //2.获取当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){return ;}
        //得到实体对象
        Object entity = args[0];
        
        //3.准备数据，为公共实体对象济进行赋值
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        
        //4.根据当前不同的操作类型，为对应的属性通过反射进行赋值
        if(operationType == OperationType.INSERT) {
            //插入操作，为四个公共字段进行赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                
                //通过反射为对象属性进行赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
                
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            
        }else if(operationType == OperationType.UPDATE){
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);
            
        }
        
        
        
    }
}
