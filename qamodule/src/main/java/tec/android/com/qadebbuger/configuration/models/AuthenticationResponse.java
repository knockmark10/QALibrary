package tec.android.com.qadebbuger.configuration.models;

import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {

    @SerializedName("details")
    private String details;

    @SerializedName("status")
    private String status;

    public AuthenticationResponse(String details, String status) {
        this.details = details;
        this.status = status;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return
                "AuthenticationResponse{" +
                        "details = '" + details + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}