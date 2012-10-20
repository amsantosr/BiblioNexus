package com.tais.biblionexus.client.entities;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@SuppressWarnings("serial")
@Entity
public class Borrowing implements Serializable {
    @Id
    private String id;
    private Long libraryId;
    private String userGoogleAccount;
    private Long libraryItemId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
    }

    public String getUserGoogleAccount() {
        return userGoogleAccount;
    }

    public void setUserGoogleAccount(String userGoogleAccount) {
        this.userGoogleAccount = userGoogleAccount;
    }

    public Long getLibraryItemId() {
        return libraryItemId;
    }

    public void setLibraryItemId(Long libraryItemId) {
        this.libraryItemId = libraryItemId;
    }
}
