package com.soonsoft.uranus.security.config.api.jwt.token;

import java.util.ArrayList;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

public class JWTAuthenticationTokenTest {

    private UserInfo user = null;

    {
        List<GrantedAuthority> roles = new ArrayList<>();
        RoleInfo roleInfo = new RoleInfo("admin", "系统管理员");
        roles.add(roleInfo);
        roleInfo = new RoleInfo("admin", "系统管理员");
        roles.add(roleInfo);

        UserInfo userInfo = new UserInfo("zhousong", "1", roles);
        userInfo.setCellPhone("13605160000");
        userInfo.setNickName("周大侠");
        userInfo.setPasswordSalt("0911");

        user = userInfo;
    }

    @Test
    public void test_getAccessToken() {

       JWTAuthenticationToken jwtAuthenticationToken = new JWTAuthenticationToken(user, user.getAuthorities());
       String accessToken = jwtAuthenticationToken.getAccessToken();
       Assert.assertTrue(accessToken.equals(jwtAuthenticationToken.getAccessToken()));

    }

    @Test
    public void test_getRefreshToken() {

       JWTAuthenticationToken jwtAuthenticationToken = new JWTAuthenticationToken(user, user.getAuthorities());
       String refreshToken = jwtAuthenticationToken.getRefreshToken();
       System.out.println(refreshToken);
       Assert.assertTrue(refreshToken.equals(jwtAuthenticationToken.getRefreshToken()));

    }

    @Test
    public void test_accessTokenExpireTime() {
        JWTAuthenticationToken jwtAuthenticationToken = new JWTAuthenticationToken(user, user.getAuthorities());
        jwtAuthenticationToken.setAccessTokenExpireTime(1);

        String accessToken = jwtAuthenticationToken.getAccessToken();

        try {
            Thread.sleep(1 * 60 * 1000);
        } catch(InterruptedException e) {
            Assert.assertFalse(false);
        }

        try {
            DecodedJWT jwt = JWT.decode(accessToken);
            Assert.assertTrue("zhousong".equals(jwt.getClaim("username").asString()));
            Assert.assertFalse(false);
        } catch(JWTDecodeException e) {
            Assert.assertTrue(true);
        }
    }
    
}
