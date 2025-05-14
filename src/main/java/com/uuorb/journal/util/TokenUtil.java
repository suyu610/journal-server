package com.uuorb.journal.util;

import cn.hutool.core.util.StrUtil;
import com.uuorb.journal.constant.JwtConstant;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: JwtTokenUtil
 * @Author: 小霍
 * @UpdateUser: 小霍
 * @Version: 0.0.1
 */
@Slf4j
public class TokenUtil {
    private static String secretKey;
    private static Duration accessTokenExpireTime;
    private static Duration refreshTokenExpireTime;
    private static Duration refreshTokenExpireAppTime;
    private static String issuer;

    public static void setTokenSettings(TokenSettings tokenSettings) {
        secretKey = tokenSettings.getSecretKey();
        accessTokenExpireTime = tokenSettings.getAccessTokenExpireTime();
        refreshTokenExpireTime = tokenSettings.getRefreshTokenExpireTime();
        refreshTokenExpireAppTime = tokenSettings.getRefreshTokenExpireAppTime();
        issuer = tokenSettings.getIssuer();
    }

    public static String generateToken(String subject, Map<String, Object> claims) {
        return generateToken(issuer, subject, claims, accessTokenExpireTime.toMillis(), secretKey);
    }


    public static String generateToken(String userId) {
        String subject = "com.uuorb.journal";
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstant.JWT_USER_ID, userId);
        return generateToken(issuer, subject, claims, accessTokenExpireTime.toMillis(), secretKey);
    }
    public static byte[] strToByteArray(String str) {
        if (str == null) { return null; } byte[] byteArray = str.getBytes(); return byteArray; }

    /**
     * 签发token
     *
     * @param issuer    签发人
     * @param subject   代表这个JWT的主体，即它的所有人 一般是用户id
     * @param claims    存储在JWT里面的信息 一般放些用户的权限/角色信息
     * @param ttlMillis 有效时间(毫秒)
     * @return java.lang.String
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static String generateToken(String issuer, String subject, Map<String, Object> claims, long ttlMillis, String secret) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

//        byte[] signingKey = DatatypeConverter.parseBase64Binary(secret);
        byte[] signingKey = strToByteArray(secret);



        JwtBuilder builder = Jwts.builder();
        builder.setHeaderParam("typ", "JWT");
        if (null != claims) {
            builder.setClaims(claims);
        }
        if (StrUtil.isNotEmpty(issuer)) {
            builder.setSubject(subject);
        }
        if (StrUtil.isNotEmpty(issuer)) {
            builder.setIssuer(issuer);
        }
        builder.setIssuedAt(now);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        builder.signWith(signatureAlgorithm, signingKey);
        return builder.compact();
    }

    // 上面我们已经有生成 access_token 的方法，下面加入生成 refresh_token 的方法(PC 端过期时间短一些)

    /**
     * 生产 PC refresh_token
     *
     * @param subject
     * @param claims
     * @return java.lang.String
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static String getRefreshToken(String subject, Map<String, Object> claims) {
        return generateToken(issuer, subject, claims, refreshTokenExpireTime.toMillis(), secretKey);
    }

    /**
     * 生产 App端 refresh_token
     *
     * @param subject
     * @param claims
     * @return java.lang.String
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static String getRefreshAppToken(String subject, Map<String, Object> claims) {
        return generateToken(issuer, subject, claims, refreshTokenExpireAppTime.toMillis(), secretKey);
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token
     * @return io.jsonwebtoken.Claims
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            byte[] signingKey = strToByteArray(secretKey);

            claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            if (e instanceof ClaimJwtException) {
                claims = ((ClaimJwtException) e).getClaims();
            }
        }
        return claims;
    }

    public static Claims getClaimsFromToken(String token, String secret) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            if (e instanceof ClaimJwtException) {
                claims = ((ClaimJwtException) e).getClaims();
            }
        }
        return claims;
    }

    /**
     * 获取用户openid
     *
     * @param token
     * @return java.lang.String
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static String getUserOpenid(String token) {
        String openid = null;
        try {
            Claims claims = getClaimsFromToken(token);
            openid = (String) claims.get(JwtConstant.JWT_USER_OPENID);
        } catch (Exception e) {
            log.error("error={}", e);
        }
        return openid;
    }

    /**
     * 获取用户openid
     *
     * @param token
     * @return java.lang.String
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static String getUserId(String token) {
        String id = null;
        try {
            Claims claims = getClaimsFromToken(token);
            id = (String) claims.get(JwtConstant.JWT_USER_ID);
        } catch (Exception e) {
            log.error("error={}", e);
        }
        return id;
    }


    /**
     * 验证token 是否过期
     *
     * @param token
     * @return java.lang.Boolean
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("error={}", e);
            return true;
        }
    }

    /**
     * 校验令牌，并查看当前的openid是否为数据库中的openid
     *
     * @param token
     * @return java.lang.Boolean
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static Boolean validateToken(String token) {
        Claims claimsFromToken = getClaimsFromToken(token);
        return (null != claimsFromToken && !isTokenExpired(token));
    }


    /**
     * 校验令牌是否为管理员
     *
     * @param token
     * @return java.lang.Boolean
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static Boolean validateAdmin(String token) {
        Claims claimsFromToken = getClaimsFromToken(token);
        String role = (String) claimsFromToken.get(JwtConstant.ROLES_INFOS_KEY);
        System.out.println(role);
        return (null != claimsFromToken && !isTokenExpired(token) && StrUtil.equals(role, JwtConstant.ROLES_ADMIN_VALUE));
    }

    /**
     * 刷新token
     *
     * @param refreshToken
     * @param claims       主动去刷新的时候 改变JWT payload 内的信息
     * @return java.lang.String
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static String refreshToken(String refreshToken, Map<String, Object> claims) {
        String refreshedToken;
        try {
            Claims parserclaims = getClaimsFromToken(refreshToken);
            /**
             * 刷新token的时候如果为空说明原先的 用户信息不变 所以就引用上个token里的内容
             */
            if (null == claims) {
                claims = parserclaims;
            }
            refreshedToken = generateToken(parserclaims.getIssuer(), parserclaims.getSubject(), claims, accessTokenExpireTime.toMillis(), secretKey);
        } catch (Exception e) {
            refreshedToken = null;
            log.error("error={}", e);
        }
        return refreshedToken;
    }

    /**
     * 获取token的剩余过期时间
     *
     * @param token
     * @return long
     * @throws
     * @Author: 小霍
     * @UpdateUser:
     * @Version: 0.0.1
     */
    public static long getRemainingTime(String token) {
        long result = 0;
        try {
            long nowMillis = System.currentTimeMillis();
            result = getClaimsFromToken(token).getExpiration().getTime() - nowMillis;
        } catch (Exception e) {
            log.error("error={}", e);
        }
        return result;
    }


}


