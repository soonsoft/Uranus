package com.soonsoft.uranus.web.error.properties;

public class ErrorMessageProperties {

    private String badRequestMessage;

    private String unauthorizedMessage;

    private String forbiddenMessage;

    private String unfoundMessage;

    private String serverErrorMessage;

    public String getBadRequestMessage() {
        return badRequestMessage;
    }

    public void setBadRequestMessage(String badRequestMessage) {
        this.badRequestMessage = badRequestMessage;
    }

    public String getUnauthorizedMessage() {
        return unauthorizedMessage;
    }

    public void setUnauthorizedMessage(String unauthorizedMessage) {
        this.unauthorizedMessage = unauthorizedMessage;
    }

    public String getForbiddenMessage() {
        return forbiddenMessage;
    }

    public void setForbiddenMessage(String forbiddenMessage) {
        this.forbiddenMessage = forbiddenMessage;
    }

    public String getUnfoundMessage() {
        return unfoundMessage;
    }

    public void setUnfoundMessage(String unfoundMessage) {
        this.unfoundMessage = unfoundMessage;
    }

    public String getServerErrorMessage() {
        return serverErrorMessage;
    }

    public void setServerErrorMessage(String serverErrorMessage) {
        this.serverErrorMessage = serverErrorMessage;
    }
    
}
