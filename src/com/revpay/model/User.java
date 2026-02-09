package com.revpay.model;

public class User {

    private long userId;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private String transactionPin;
    private String userType;
    private double walletBalance;

    public User() {}

    public User(String fullName, String email, String phone,
                String passwordHash, String transactionPin,
                String userType) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.transactionPin = transactionPin;
        this.userType = userType;
    }

    // Getters and Setters
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getTransactionPin() { return transactionPin; }
    public void setTransactionPin(String transactionPin) { this.transactionPin = transactionPin; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }
}
