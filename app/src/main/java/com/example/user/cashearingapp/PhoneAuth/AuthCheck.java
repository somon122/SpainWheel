package com.example.user.cashearingapp.PhoneAuth;

public class AuthCheck {

    private String deviceId;
    private String PhoneNumber;

    public AuthCheck(String deviceId, String phoneNumber) {
        this.deviceId = deviceId;
        PhoneNumber = phoneNumber;
    }

    public AuthCheck() {
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}
