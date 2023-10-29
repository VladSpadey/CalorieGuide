package com.example.calorieguide.Utils;

public class UserData {
    private String uid;
    private String email;
    private Long activityBmr;
    private Double initialWeight;
    private Double latestWeight;
    private Double height;
    private Double activityLevelMultiplier;
    private Double age;
    private String sex;

    public UserData() {}
    public UserData(String uid, String email, Long activityBmr, Double initialWeight, Double latestWeight, Double height, Double activityLevel, Double age, String sex) {
        this.uid = uid;
        this.email = email;
        this.activityBmr = activityBmr;
        this.initialWeight = initialWeight;
        this.latestWeight = latestWeight;
        this.height = height;
        this.activityLevelMultiplier = activityLevel;
        this.age = age;
        this.sex = sex;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getActivityBmr() {
        return activityBmr;
    }

    public void setActivityBmr(Long bmr) {
        this.activityBmr = bmr;
    }

    public Double getInitialWeightget() {
        return initialWeight;
    }

    public void setInitialWeight(Double initialWeight) {
        this.initialWeight = initialWeight;
    }

    public Double getLatestWeight() {
        return latestWeight;
    }

    public void setLatestWeight(Double latestWeight) {
        this.latestWeight = latestWeight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getActivityLevelMultiplier() {
        return activityLevelMultiplier;
    }

    public void setActivityLevelMultiplier(Double activityLevelMultiplier) {
        this.activityLevelMultiplier = activityLevelMultiplier;
    }

    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
