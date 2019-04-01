package com.example.user.cashearingapp;

public class WithdrawSubmit {

    private String addressNumber;
    private int amount;

    public WithdrawSubmit(String addressNumber, int amount) {
        this.addressNumber = addressNumber;
        this.amount = amount;
    }

    public WithdrawSubmit() {
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
