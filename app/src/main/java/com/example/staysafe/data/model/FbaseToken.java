package com.example.staysafe.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FbaseToken {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("fbaseToken")
    @Expose
    private String fbaseToken;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFbaseToken() {
        return fbaseToken;
    }

    public void setFbaseToken(String fbaseToken) {
        this.fbaseToken = fbaseToken;
    }

}
