package com.rpos.pos.domain.requestmodel.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequestJson {

    @SerializedName("usr")
    @Expose
    private String usr;
    @SerializedName("pwd")
    @Expose
    private String pwd;

    @SerializedName("site")
    @Expose
    private String site;

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
