package com.soulfriends.meditation.model;

import android.text.TextUtils;
import android.util.Patterns;

public class UserLogin {

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String email;
    public String password;

    public boolean isValidEmail() {
        if (this.email != null && !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    public boolean isValidPassword() {
        if (this.password != null && (this.password.length() >= 6 && this.password.length() <= 12)) {
            return true;
        }
        return false;
    }

    public boolean isValidCredential() {
        if (this.email.equalsIgnoreCase("ex@gmail.com") && this.password.equals("123456")) {
            return true;
        }
        return false;
    }

}
