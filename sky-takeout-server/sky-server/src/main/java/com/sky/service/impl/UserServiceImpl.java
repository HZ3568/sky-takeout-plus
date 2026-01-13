package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties wechatProperties;

    // 微信登录接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    public User wxLogin(UserLoginDTO userLoginDTO) {

        String openid = getOpenid(userLoginDTO.getCode());

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                   .build();
            userMapper.insert(user);
        }

        return user;
    }

    /**
     * 调用微信接口服务获取openid
     */
    private String getOpenid(String code) {
        Map<String, String> mp = new HashMap<>();
        mp.put("appid", wechatProperties.getAppid());
        mp.put("secret", wechatProperties.getSecret());
        mp.put("js_code", code);
        mp.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN, mp);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}