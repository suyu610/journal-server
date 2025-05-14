package com.uuorb.journal.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson.JSONObject;
import com.uuorb.journal.config.WechatOpenConfig;
import com.uuorb.journal.mapper.UserMapper;
import com.uuorb.journal.model.User;
import com.uuorb.journal.model.dto.WechatLoginResp;
import com.uuorb.journal.util.IDUtil;
import com.uuorb.journal.util.TokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    private WechatOpenConfig wechatOpenConfig;

    @Value("${apple.key}")
    private String APPLE_LOGIN_KEY;

    final String WechatUnionUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    public User getUserByUserId(String userId) {
        User userByUserId = userMapper.getUserByUserId(userId);
        log.info("user:{}", userByUserId);
        return userByUserId;
    }

    public List<User> selectUserByPhone(String telephone) {
        User query = User.builder().telephone(telephone).build();
        return userMapper.selectUser(query);
    }

    public User registerByPhone(String telephone) {
        String userId = IDUtil.userId();
        String nickname = "好享用户" + userId.substring(userId.length() - 4);

        User user = User.builder().userId(userId).nickname(nickname).telephone(telephone).build();

        userMapper.createUser(user);
        return user;
    }

    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    public String loginWithWechat(String code, String platform) {
        if (StrUtil.equalsIgnoreCase(platform, "ios")) {
            String appid = wechatOpenConfig.getAppid();
            String secret = wechatOpenConfig.getSecret();
            try {
                String s = HttpUtil.get(String.format(WechatUnionUrl, appid, secret, code));
                WechatLoginResp wechatLoginResp = JSONObject.parseObject(s, WechatLoginResp.class);
                log.info("getWechatLoginResp:{}", wechatLoginResp);
                String openid = wechatLoginResp.getOpenid();

                String unionid = wechatLoginResp.getUnionid();
                if (unionid == null) {
                    return null;
                }
                User userQuery = User.builder().unionId(unionid).build();
                // userQuery
                List<User> users = userMapper.selectUser(userQuery);
                if (CollectionUtil.isEmpty(users)) {
                    // register
                    String userId = IDUtil.userId();
                    String nickname = "好享用户" + userId.substring(userId.length() - 4);
                    User user = User.builder()
                        .userId(userId)
                        .nickname(nickname)
                        .openid(openid)
                        .unionId(unionid)
                        .build();
                    userMapper.createUser(user);
                    return TokenUtil.generateToken(userId);
                } else {
                    User user = users.get(0);
                    String token = TokenUtil.generateToken(user.getUserId());
                    return token;
                }
            } catch (Exception e) {
                return null;
            }

        }
        return null;

    }

    public String loginWithApple(String code) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String client_id = "com.uuorb.journal"; // 被授权的APP ID
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("kid", "QH4XFS4JS4"); // 参考后台配置
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("iss", "BVNS39X4QM"); // 参考后台配置 team id
        long now = System.currentTimeMillis() / 1000;
        claims.put("iat", now);
        claims.put("exp", now + 86400 * 30); // 最长半年，单位秒
        claims.put("aud", "https://appleid.apple.com"); // 默认值
        claims.put("sub", client_id);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(readKey());
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        String client_secret = Jwts.builder()
            .setHeader(header)
            .setClaims(claims)
            .signWith(SignatureAlgorithm.ES256, privateKey)
            .compact();

        String url = "https://appleid.apple.com/auth/token";

        // POST 请求
        Map<String, Object> form = new HashMap<String, Object>();
        form.put("client_id", client_id);
        form.put("client_secret", client_secret);
        form.put("code", code);
        form.put("grant_type", "authorization_code");
        form.put("redirect_uri", "https://jounral.aceword.xyz/api/token/apple"); // 回调地址
        String response = HttpUtil.post(url, form);
        JSONObject jsonObject = JSONObject.parseObject(response);
        String idToken = (String)jsonObject.get("id_token");

        JWT userJwtFromApple = JWTUtil.parseToken(idToken);
        log.info("apple:{}", idToken);

        log.info("apple:{}", userJwtFromApple.getPayloads());
        String sub = (String)userJwtFromApple.getPayload("sub");
        if (sub == null) {
            return null;
        }
        User userQuery = User.builder().appleId(sub).build();
        List<User> users = userMapper.selectUser(userQuery);
        if (CollectionUtil.isEmpty(users)) {
            // register
            String userId = IDUtil.userId();
            String nickname = "好享用户" + userId.substring(userId.length() - 4);
            User user = User.builder().userId(userId).nickname(nickname).appleId(sub).build();
            userMapper.createUser(user);
            return TokenUtil.generateToken(userId);
        } else {
            User user = users.get(0);
            String token = TokenUtil.generateToken(user.getUserId());
            return token;
        }
    }

    // 秘钥读取
    public byte[] readKey() {

        return Base64.getDecoder().decode(APPLE_LOGIN_KEY); //Base64.getDecoder().decode(temp);
    }
}
