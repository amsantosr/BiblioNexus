package com.tais.biblionexus.client.entities;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

/**
 * Entity that represents a Library
 * 
 * @author Abraham Max Santos Ramos <amsantosr@gmail.com>
 *
 */
@SuppressWarnings("serial")
@Entity
public class Library implements Serializable {
    @Id
    private Long id;
    private String name;
    private String libraryType;
    private String address;
    private String city;
    private String department;
    private String phoneNumber;
    private String headGoogleAccount;
    private String headName;
    private String topics;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLibraryType(String libraryType) {
        this.libraryType = libraryType;
    }

    public String getLibraryType() {
        return libraryType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setHeadGoogleAccount(String headGoogleAccount) {
        this.headGoogleAccount = headGoogleAccount;
    }

    public String getHeadGoogleAccount() {
        return headGoogleAccount;
    }

    public void setHeadName(String headName) {
        this.headName = headName;
    }

    public String getHeadName() {
        return headName;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getTopics() {
        return topics;
    }

}
