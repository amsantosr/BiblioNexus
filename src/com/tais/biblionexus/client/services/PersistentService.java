package com.tais.biblionexus.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.tais.biblionexus.client.entities.Borrowing;
import com.tais.biblionexus.client.entities.Exemplar;
import com.tais.biblionexus.client.entities.Library;
import com.tais.biblionexus.client.entities.LibraryItem;
import com.tais.biblionexus.client.entities.LibraryUser;

public interface PersistentService extends RemoteService {
    void clearDatabase();
    
    void fillLibraries(int quantity);
    
    void fillLibraryItems(int quantity);

    ArrayList<Library> getAllLibraries() throws Exception;
    
    ArrayList<LibraryItem> getAllLibraryItems() throws Exception;
    
    ArrayList<LibraryUser> getAllUsers() throws Exception;
    
    ArrayList<Borrowing> getBorrowings(Long libraryId) throws Exception;
    
    ArrayList<Exemplar> getLibraryExemplars(Long libraryId) throws Exception;
    
    ArrayList<LibraryUser> getLibraryUsers(Long libraryId) throws Exception;
    
    Library getLibrary(Long libraryId) throws Exception;
    
    LibraryUser getLibraryUser(String googleAccount) throws Exception;
    
    String persistBorrowing(Long libraryId, String googleAccount, Long libraryItemId) throws Exception;
    
    void persistLibrary(Library library) throws Exception;
    
    long persistLibraryItem(LibraryItem libraryItem) throws Exception;
    
    long persistLibraryUser(LibraryUser libraryUser) throws Exception;
    
    String persistRegistration(Long libraryId, String googleAccount) throws Exception;
    
    void removeLibrary(Long libraryId);
    
    void removeLibraryItem(String isbn);
    
    void removeBorrowing(String id);
    
    ArrayList<LibraryItem> searchLibraryItem(String text) throws Exception;
    
    ArrayList<Exemplar> searchLibraryExemplar(String text) throws Exception;
}
