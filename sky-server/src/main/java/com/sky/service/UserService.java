package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;

/**
 * @description：
 * @author： 周海
 * @create： 2024/1/1
 */
public interface UserService {
    
    
    /**
     *  微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
