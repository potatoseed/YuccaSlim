package com.yuccaworld.yuccaslim.model;

public class ResponseToClient {
    private String accountResponse;
    private boolean entilementActive;

    public ResponseToClient(String accountResponse, boolean entilementActive) {
        this.accountResponse = accountResponse;
        this.entilementActive = entilementActive;
    }

    public String getAccountResponse() {
        return accountResponse;
    }

    public void setAccountResponse(String accountResponse) {
        this.accountResponse = accountResponse;
    }

    public boolean isEntilementActive() {
        return entilementActive;
    }

    public void setEntilementActive(boolean entilementActive) {
        this.entilementActive = entilementActive;
    }
}
