package com.liu.entity;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

@Entity(name = "app_package_test")
public class AppInfo {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "app_package")
    private String appPackage;

    private String brief;

    @Column(name = "images")
    private String images;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", appName='" + appName + '\'' +
                ", appPackage='" + appPackage + '\'' +
                ", brief='" + brief + '\'' +
                ", images='" + images + '\'' +
                '}';
    }
}
