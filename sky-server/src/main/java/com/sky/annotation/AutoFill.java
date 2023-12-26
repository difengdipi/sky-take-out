package com.sky.annotation;

/**
 * @description：自定义注解，用于标记，某个方法需要进行功能字段填充处理
 * @author： 周海
 * @create： 2023/12/26 17:01
 */

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型，UPDATE INSERT
    OperationType value();
}
