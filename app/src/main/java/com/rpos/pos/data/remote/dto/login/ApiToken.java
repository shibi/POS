
package com.rpos.pos.data.remote.dto.login;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiToken {

    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("api_secret")
    @Expose
    private String apiSecret;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

}
