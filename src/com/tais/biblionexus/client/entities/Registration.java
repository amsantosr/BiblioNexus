package com.tais.biblionexus.client.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

/**
 * Entity that stores information about registering a user to a library.
 * It can store whether the user is a Librarian or a LibraryUser.
 * 
 * @author Abraham Max Santos Ramos <amsantosr@gmail.com>
 *
 */
@SuppressWarnings("serial")
@Entity
public class Registration implements Serializable {
    @Id
    private String registrationId = null;
    private Long libraryId = null;
    private String userGoogleAccount = null;
    private Date registrationDate = null;
    
    public Registration() {
    }
    
    public Registration(Long libraryId, String libraryUserId, Date registrationDate) {
        this.registrationId = libraryId + "-" + libraryUserId;
        this.setLibraryId(libraryId);
        this.setLibraryUserGoogleAccount(libraryUserId);
        this.setRegistrationDate(registrationDate);
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
    }

    public String getLibraryUserGoogleAccount() {
        return userGoogleAccount;
    }

    public void setLibraryUserGoogleAccount(String googleAccount) {
        this.userGoogleAccount = googleAccount;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    
}
