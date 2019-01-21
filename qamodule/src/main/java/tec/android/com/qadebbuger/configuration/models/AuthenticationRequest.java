package tec.android.com.qadebbuger.configuration.models;

import com.google.gson.annotations.SerializedName;

public class AuthenticationRequest {

    @SerializedName("email")
    private String userName;

    @SerializedName("password")
    private String password;

    @SerializedName("package")
    private String packageName;

    public AuthenticationRequest(String userName, String password, String packageName) {
        this.userName = userName;
        this.password = password;
        this.packageName = packageName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
