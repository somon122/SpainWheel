package com.example.user.cashearingapp;

public class MyWorkClass {

    private String workId;
    private String workImageUrl;
    private String workDescription;

    public MyWorkClass(String workId, String workImageUrl, String workDescription) {
        this.workId = workId;
        this.workImageUrl = workImageUrl;
        this.workDescription = workDescription;
    }

    public MyWorkClass() {
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getWorkImageUrl() {
        return workImageUrl;
    }

    public void setWorkImageUrl(String workImageUrl) {
        this.workImageUrl = workImageUrl;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }
}
