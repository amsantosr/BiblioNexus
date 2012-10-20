package com.tais.biblionexus.client.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

/**
 * Module for registering an Exemplar Book for a Library
 * 
 * @author Abraham Max Santos Ramos
 *
 */
@SuppressWarnings("serial")
@Entity
public class Exemplar implements Serializable {
    @Id
    private Long exemplarId;
    private Long libraryId;
    private Long libraryItemId;
    private String localCode;
    private Date registrationDate;
    private int numberOfLendings = 0;
    private boolean currentlyLent = false;

    public Long getExemplarId() {
        return exemplarId;
    }

    public void setExemplarId(Long exemplarId) {
        this.exemplarId = exemplarId;
    }

    public String getLocalCode() {
        return localCode;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getNumberOfLendings() {
        return numberOfLendings;
    }
    
    public void setNumberOfLendings(int numberOfLendings) {
        this.numberOfLendings = numberOfLendings;
    }
    
    public void incrementNumberOfLendings() {
        ++numberOfLendings;
    }

    public boolean isCurrentlyLent() {
        return currentlyLent;
    }

    public void setCurrentlyLent(boolean currentlyLent) {
        this.currentlyLent = currentlyLent;
    }
    
    public Long getLibraryItemId() {
        return libraryItemId;
    }
    
    public void setLibraryItemId(Long id) {
        this.libraryItemId = id;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
    }

    public Long getLibraryId() {
        return libraryId;
    }
    
}
