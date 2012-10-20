package com.tais.biblionexus.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tais.biblionexus.client.entities.Borrowing;
import com.tais.biblionexus.client.entities.Exemplar;
import com.tais.biblionexus.client.entities.Library;
import com.tais.biblionexus.client.entities.LibraryItem;
import com.tais.biblionexus.client.entities.LibraryUser;

public interface PersistentServiceAsync {

    void persistLibraryItem(LibraryItem libraryItem, AsyncCallback<Long> callback);

    void getAllLibraryItems(AsyncCallback<ArrayList<LibraryItem>> callback);

    void removeLibraryItem(String isbn, AsyncCallback<Void> callback);

    void persistLibrary(Library biblioteca, AsyncCallback<Void> callback);

    void getAllLibraries(AsyncCallback<ArrayList<Library>> callback);

    void removeLibrary(Long libraryId, AsyncCallback<Void> callback);

    void clearDatabase(AsyncCallback<Void> callback);

    void fillLibraryItems(int quantity, AsyncCallback<Void> callback);

    void fillLibraries(int quantity, AsyncCallback<Void> callback);

    void persistLibraryUser(LibraryUser libraryUser, AsyncCallback<Long> callback);

    void getLibraryUsers(Long libraryId, AsyncCallback<ArrayList<LibraryUser>> callback);

    void getLibrary(Long libraryId, AsyncCallback<Library> callback);

    void searchLibraryItem(String text, AsyncCallback<ArrayList<LibraryItem>> callback);

    void getLibraryUser(String googleAccount, AsyncCallback<LibraryUser> callback);

    void getLibraryExemplars(Long libraryId, AsyncCallback<ArrayList<Exemplar>> callback);

    void searchLibraryExemplar(String text, AsyncCallback<ArrayList<Exemplar>> callback);

    void persistRegistration(Long libraryId, String googleAccount, AsyncCallback<String> callback);

    void getAllUsers(AsyncCallback<ArrayList<LibraryUser>> callback);

    void persistBorrowing(Long libraryId, String googleAccount, Long libraryItemId, AsyncCallback<String> callback);

    void getBorrowings(Long libraryId, AsyncCallback<ArrayList<Borrowing>> callback);

    void removeBorrowing(String id, AsyncCallback<Void> callback);

}
