package com.soonsoft.uranus.security.config.api;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.jwt.IApiToken;

import org.springframework.security.core.Authentication;

public class SecurityResult {

    private Integer httpStatus;

    private String message;

    private String token;

    private String username;

    private String nickname;

    private Boolean authenticated = Boolean.FALSE;

    public SecurityResult() {

    }

    public SecurityResult(Integer httpStatus, Authentication authentication) {
        this.httpStatus = httpStatus;

        if(authentication instanceof IApiToken) {
            this.token = ((IApiToken)authentication).getToken();
        }

        Object user = authentication.getPrincipal();
        if(user instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) user;
            this.username = userInfo.getUsername();
            this.nickname = userInfo.getNickName();
        } else {
            this.username = (String) user;
            this.nickname = this.username;
        }

        this.authenticated = authentication.isAuthenticated();
        
    }

    public SecurityResult(Integer httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }
    

    @Override
    public String toString() {
        return new JSONBuilder()
                .addProperty("httpStatus", this.getHttpStatus())
                .addProperty("message", this.getMessage())
                .addProperty("token", this.getToken())
                .addProperty("username", this.getUsername())
                .addProperty("nickname", this.getNickname())
                .addProperty("isAuthenticated", this.isAuthenticated())
                .toString(JSONBuilder.JSONObject);
    }

    private static class JSONBuilder {
        private StringBuilder builder = new StringBuilder();

        private static final Integer JSONObject = 1;
        private static final Integer JSONArray = 2;

        @Override
        public String toString() {
            return builder.toString();
        }

        public JSONBuilder addProperty(String name, Object value) {
            builder.append("\"").append(name).append("\"").append(":");
            if(value instanceof CharSequence || value instanceof Char) {
                builder.append("\"").append(value).append("\"");
            } else {
                builder.append(value);
            }
            return this;
        }

        public String toString(Integer type) {
            if(JSONArray.equals(type)) {
                return new StringBuilder("{").append(builder).append("}").toString();
            }

            if(JSONObject.equals(type)) {
                return new StringBuilder("[").append(builder).append("]").toString();
            }

            return toString();
        }
    }
    
}