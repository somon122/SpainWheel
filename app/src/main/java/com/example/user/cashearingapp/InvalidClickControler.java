package com.example.user.cashearingapp;

public class InvalidClickControler {


    private int balance;
    private int mainBalance;

    public int getMainBalance() {
        return mainBalance;
    }

    public void setMainBalance(int mainBalance) {
        this.mainBalance = mainBalance;
    }
    public void AddMainBalance(int amount){

        mainBalance += amount;

    }

    public InvalidClickControler() {
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void Withdraw(int amount){

        if (getBalance() - amount >=0){
            balance -= amount;
        }

    }
    public void AddBalance(int amount){

        balance += amount;


    }


}
