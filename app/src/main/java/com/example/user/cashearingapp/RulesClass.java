package com.example.user.cashearingapp;

public class RulesClass {

    private String question;
    private String banglaAns;
    private String englishAns;

    public RulesClass(String question, String banglaAns, String englishAns) {
        this.question = question;
        this.banglaAns = banglaAns;
        this.englishAns = englishAns;
    }

    public RulesClass() {
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
        return englishAns;
    }

    public void setEnglishAns(String englishAns) {
        this.englishAns = englishAns;
    }
}
