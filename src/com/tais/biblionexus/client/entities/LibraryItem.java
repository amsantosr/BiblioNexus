package com.tais.biblionexus.client.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

/**
 * Entity that represents a LibraryItem, common for all libraries
 * 
 * @author Abraham Max Santos Ramos <amsantosr@gmail.com>
 *
 */
@SuppressWarnings("serial")
@Entity
@Searchable
public class LibraryItem implements Serializable {
    @Id
    @SearchableId
    private Long id;
    private String isbn;
    @SearchableProperty
    private String title;
    @SearchableProperty
    private String author;
    private String publisher;
    private String description;
    private String language;
    private int pages;
    private int year;
    
    private String userGoogleAccount = null;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titulo) {
        this.title = titulo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String autor) {
        this.author = autor;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPages() {
        return pages;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public String getUserGoogleAccount() {
        return userGoogleAccount;
    }

    public void setUserGoogleAccount(String userGoogleAccount) {
        this.userGoogleAccount = userGoogleAccount;
    }
    
}
