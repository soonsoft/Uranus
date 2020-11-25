package com.soonsoft.uranus.security.config.api;

import com.soonsoft.uranus.security.entity.UserInfo;

import org.springframework.security.core.Authentication;

public class SecurityResult {

    private Integer httpStatus;

    private String message;

    private String ticket;

    private String username;

    private String nickname;

    private Boolean authenticated = Boolean.FALSE;

    public SecurityResult() {

    }

    public SecurityResult(Integer httpStatus, Authentication authentication) {
        this.httpStatus = httpStatus;

        Object ticket = authentication.getDetails();
        this.ticket = ticket != null ? ticket.toString() : null;

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

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
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
        return 
            new StringBuilder()
                .append("{")
                .append("\"").append("httpStatus").append("\"").append(":").append(this.getHttpStatus()).append(",")
                .append("\"").append("message").append("\"").append(":").append(getStringValue(this.getMessage())).append(",")
                .append("\"").append("ticket").append("\"").append(":").append(getStringValue(this.getTicket())).append(",")
                .append("\"").append("username").append("\"").append(":").append(getStringValue(this.getUsername())).append(",")
                .append("\"").append("nickname").append("\"").append(":").append(getStringValue(this.getNickname())).append(",")
                .append("\"").append("isAuthenticated").append("\"").append(":").append(this.isAuthenticated())
                .append("}")
                .toString();
    }

    private static String getStringValue(String value) {
        if(value == null) {
            return "null";
        }

        return "\"" + value + "\"";
    }
    
}
