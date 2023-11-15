package com.caipiao.common.utils;

import com.caipiao.common.exception.RRException;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/19 13:38
 */
public class JwtUtils {
    /**
     * 私钥加密token
     *
     * @param userInfo      载荷中的数据
     * @param privateKey    私钥
     * @param expireDays 过期时间，单位秒
     * @return
     * @throws Exception
     */
    public static String generateToken(SysCompStaffEntity userInfo, PrivateKey privateKey, int expireDays) {
        return Jwts.builder()
                .claim(Constant.JWT_KEY_ID, userInfo.getStaffId())
                .claim(Constant.JWT_KEY_USER_NAME, userInfo.getStaffName())
                .claim(Constant.JWT_KEY_USER_AVATAR, userInfo.getAvatar())
                .claim(Constant.JWT_KEY_COMPANY_ID, "10000")
                .claim(Constant.JWT_KEY_STAFF_STATUS, userInfo.getStaffStatus())
                .claim(Constant.JWT_KEY_POSITION, userInfo.getPosition())
                .claim(Constant.JWT_KEY_STAFF_TENANT,userInfo.getSubordinateStore())
                .claim(Constant.JWT_KEY_STAFF_USERID,userInfo.getUserId())
                .setExpiration(DateTime.now().plusHours(expireDays).toDate())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }


    /**
     * 公钥解析token
     *
     * @param token     用户请求中的token
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    private static Jws<Claims> parserToken(String token, PublicKey publicKey) throws Exception{
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }


    /**
     * 获取token中的用户信息
     *
     * @param token     用户请求中的令牌
     * @param publicKey 公钥
     * @return 用户信息
     * @throws Exception
     */
    public static SysCompStaffEntity getInfoFromToken(String token, PublicKey publicKey) {
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = parserToken(token, publicKey);
        } catch (Exception e) {
            throw new RRException("invalid token", 401);
        }
        Claims body = claimsJws.getBody();

        SysCompStaffEntity staff = new SysCompStaffEntity();
        staff.setStaffId(body.get(Constant.JWT_KEY_ID,String.class));
        staff.setStaffName(body.get(Constant.JWT_KEY_USER_NAME,String.class));
        staff.setAvatar(body.get(Constant.JWT_KEY_USER_AVATAR,String.class));
        staff.setPosition(body.get(Constant.JWT_KEY_POSITION,String.class));
        staff.setCompanyId("10000");
        staff.setStaffStatus(body.get(Constant.JWT_KEY_STAFF_STATUS,String.class));
        staff.setMobile(body.get(Constant.JWT_KEY_STAFF_MOBILE,String.class));
        staff.setSubordinateStore(body.get(Constant.JWT_KEY_STAFF_TENANT,String.class));
        staff.setUserId(body.get(Constant.JWT_KEY_STAFF_USERID,String.class));

        return staff;
    }

}
