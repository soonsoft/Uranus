package com.soonsoft.uranus.security.config.api;

import com.soonsoft.uranus.security.entity.UserInfo;
import org.springframework.security.core.Authentication;

public class SecurityResult {

    private Integer httpStatus;

    private String message;

    private String accessToken;

    private String refreshToken;

    private String username;

    private String nickname;

    private Boolean authenticated = Boolean.FALSE;

    public SecurityResult() {

    }

    public SecurityResult(Integer httpStatus, Authentication authentication) {
        this.httpStatus = httpStatus;

        Object user = authentication.getPrincipal();
        if(user instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) user;
            this.username = userInfo.getUserName();
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
        return new JSONBuilder(JSONBuilder.JSONObject)
                .addProperty("httpStatus", this.getHttpStatus())
                .addProperty("message", this.getMessage())
                .addProperty("accessToken", this.getAccessToken())
                .addProperty("refreshToken", this.getRefreshToken())
                .addProperty("username", this.getUsername())
                .addProperty("nickname", this.getNickname())
                .addProperty("isAuthenticated", this.isAuthenticated())
                .toString();
    }

    private static class JSONBuilder {

        private static final Integer JSONObject = 1;
        private static final Integer JSONArray = 2;

        private StringBuilder builder = new StringBuilder();

        private final Integer type;
        
        public JSONBuilder(Integer type) {
            this.type = type == null ? JSONObject : type;
        }

        public JSONBuilder addProperty(String name, Object value) {
            builder.append("\"").append(name).append("\"").append(":");
            if(value instanceof CharSequence || value instanceof Character) {
                builder.append("\"").append(value).append("\"");
            } else if(value instanceof JSONBuilder) {
                builder.append(value.toString()); 
            } else {
                builder.append(value);
            }
            builder.append(",");
            return this;
        }

        @Override
        public String toString() {
            int len = builder.length();
            if(len > 0) {
                builder.deleteCharAt(len - 1);
            }
            if(JSONArray.equals(type)) {
                return new StringBuilder("[").append(builder).append("]").toString();
            }

            if(JSONObject.equals(type)) {
                return new StringBuilder("{").append(builder).append("}").toString();
            }

            return builder.toString();
        }
    }
    
}
