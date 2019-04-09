package com.example.user.cashearingapp;

public class RulesClass {

    String userId;
    String question;
    String banglaAns;
    String EnglishAns;

    public RulesClass(String userId, String question, String banglaAns, String englishAns) {
        this.userId = userId;
        this.question = question;
        this.banglaAns = banglaAns;
        EnglishAns = englishAns;
    }

    public RulesClass() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getBanglaAns() {
        return banglaAns;
    }

    public void setBanglaAns(String banglaAns) {
        this.banglaAns = banglaAns;
    }

    public String getEnglishAns() {
        return EnglishAns;
    }

    public void setEnglishAns(String englishAns) {
        EnglishAns = englishAns;
    }
}
