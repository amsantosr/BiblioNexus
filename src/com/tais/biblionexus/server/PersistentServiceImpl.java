package com.tais.biblionexus.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.compass.core.CompassDetachedHits;
import org.compass.core.CompassSearchSession;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Query;
import com.tais.biblionexus.client.entities.Borrowing;
import com.tais.biblionexus.client.entities.Exemplar;
import com.tais.biblionexus.client.entities.Library;
import com.tais.biblionexus.client.entities.LibraryItem;
import com.tais.biblionexus.client.entities.LibraryUser;
import com.tais.biblionexus.client.entities.Registration;
import com.tais.biblionexus.client.services.PersistentService;

@SuppressWarnings("serial")
public class PersistentServiceImpl extends RemoteServiceServlet implements PersistentService {

    @Override
    public void clearDatabase() {
    }

    @Override
    public void fillLibraries(int quantity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillLibraryItems(int quantity) {
        if (quantity <= 0) {
            return;
        }
        for (int page = 1; quantity > 0; ++page) {
            String requestString = "http://www.textbookland.com/textbooks/computer_science/" + page + "/";
            try {
                URL requestURL = new URL(requestString);
                URLConnection connection = requestURL.openConnection();
                connection.setConnectTimeout(0);
                connection.setReadTimeout(0);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                quantity = processStream(reader, quantity);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<Library> getAllLibraries() throws Exception {
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        if (currentUser == null || !currentUser.getEmail().equals("amsantosr@gmail.com")) {
            throw new Exception("The current user doesn't have the rights to perform the operation");
        }

        Iterator<Library> iteratorLibraries = DAO.getInstance().ofy().query(Library.class).iterator();
        ArrayList<Library> arrayLibraries = new ArrayList<Library>();
        while (iteratorLibraries.hasNext()) {
            arrayLibraries.add(iteratorLibraries.next());
        }
        return arrayLibraries;
    }

    @Override
    public ArrayList<LibraryItem> getAllLibraryItems() throws Exception {
        Iterator<LibraryItem> iteratorLibraryItems = DAO.getInstance().ofy().query(LibraryItem.class).iterator();
        ArrayList<LibraryItem> arrayLibraryItems = new ArrayList<LibraryItem>();
        while (iteratorLibraryItems.hasNext()) {
            arrayLibraryItems.add(iteratorLibraryItems.next());
        }
        return arrayLibraryItems;
    }

    @Override
    public ArrayList<LibraryUser> getLibraryUsers(Long libraryId) throws Exception {
        ArrayList<LibraryUser> result = new ArrayList<LibraryUser>();
        Query<Registration> registrationQuery = DAO.getInstance().ofy().query(Registration.class)
                .filter("libraryId", libraryId);
        Iterator<Registration> registrationIterator = registrationQuery.iterator();
        while (registrationIterator.hasNext()) {
            Registration registration = registrationIterator.next();
            LibraryUser libraryUser = DAO.getInstance().ofy().get(LibraryUser.class, registration.getLibraryUserGoogleAccount());
            result.add(libraryUser);
        }
        return result;
    }

    @Override
    public Library getLibrary(Long libraryId) throws Exception {
        // TODO: verify if the current user has rights to do this
        Library library = DAO.getInstance().ofy().get(Library.class, libraryId);
        return library;
    }

    @Override
    public void persistLibrary(Library library) throws IllegalArgumentException {
        DAO.getInstance().ofy().put(library);
    }

    @Override
    public long persistLibraryItem(LibraryItem libraryItem) throws Exception {
        Key<LibraryItem> generatedKey = DAO.getInstance().ofy().put(libraryItem);
        if (generatedKey != null) {
            /*CompassIndexSession indexSession = DAO.getCompassInstance().openIndexSession();
            indexSession.save(libraryItem);
            indexSession.close();*/
        }
        return generatedKey.getId();
    }

    @Override
    public long persistLibraryUser(LibraryUser libraryUser) throws Exception {
        long registrationId;
        try {
            registrationId = DAO.getInstance().ofy().put(libraryUser).getId();
            /*CompassIndexSession indexSession = DAO.getCompassInstance().openIndexSession();
            indexSession.save(libraryUser);*/
        } finally {
        }
        return registrationId;
    }

    private int processStream(BufferedReader reader, int quantity) throws IOException {
        String line = null, prevLine = null;
        final String publishedLabel = "Published: ";
        final String authorsLabel = "Author(s): ";
        final String isbn13Label = "ISBN 13: ";

        while (quantity > 0) {
            prevLine = line;
            line = reader.readLine();
            if (line == null)
                break;
            int publishedIndex = line.indexOf(publishedLabel);
            if (publishedIndex == -1) {
                continue;
            }
            int titleStartIndex = prevLine.indexOf('>') + 1;
            int titleEndIndex = prevLine.indexOf('<', titleStartIndex);
            String title = prevLine.substring(titleStartIndex, titleEndIndex);
            int yearStartIndex = line.indexOf(' ', publishedIndex + publishedLabel.length()) + 1;
            int yearEndIndex = yearStartIndex + 4;
            int year = 0;
            try {
                year = Integer.parseInt(line.substring(yearStartIndex, yearEndIndex));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            prevLine = line;
            line = reader.readLine();
            int authorsLabelIndex = line.indexOf(authorsLabel);
            int authorsStartIndex = line.indexOf('>', authorsLabelIndex) + 1;
            int authorsEndIndex = line.indexOf('<', authorsStartIndex);
            String authors = line.substring(authorsStartIndex, authorsEndIndex);
            prevLine = line;
            line = reader.readLine();
            int isbn13LabelIndex = line.indexOf(isbn13Label);
            int isbn13StartIndex = isbn13LabelIndex + isbn13Label.length();
            int isbn13EndIndex = isbn13StartIndex + 13;
            String isbn13 = line.substring(isbn13StartIndex, isbn13EndIndex);

            LibraryItem libraryItem = new LibraryItem();
            libraryItem.setIsbn(isbn13);
            libraryItem.setTitle(title);
            libraryItem.setAuthor(authors);
            libraryItem.setYear(year);

            try {
                persistLibraryItem(libraryItem);
                --quantity;
            } catch (Exception e) {
            }
        }
        return quantity;
    }

    @Override
    public void removeLibrary(Long libraryId) {
        DAO.getInstance().ofy().delete(Library.class, libraryId);
    }

    @Override
    public void removeLibraryItem(String isbn) {
        DAO.getInstance().ofy().delete(LibraryItem.class, isbn);
    }

    @Override
    public ArrayList<LibraryItem> searchLibraryItem(String text) throws Exception {
        CompassSearchSession searchSession = DAO.getCompassInstance().openSearchSession();
        CompassDetachedHits hits = searchSession.find(text).detach();
        Object[] datas = hits.getDatas();
        ArrayList<LibraryItem> result = new ArrayList<LibraryItem>();
        for (int index = 0; index < datas.length; ++index) {
            result.add((LibraryItem) datas[index]);
        }
        return result;
    }

    @Override
    public LibraryUser getLibraryUser(String googleAccount) throws Exception {
        LibraryUser libraryUser = DAO.getInstance().ofy().query(LibraryUser.class).filter("googleAccount", googleAccount).get();
        return libraryUser;
    }

    @Override
    public ArrayList<Exemplar> getLibraryExemplars(Long libraryId) throws Exception {
        Query<Exemplar> exemplarQuery = DAO.getInstance().ofy().query(Exemplar.class).filter("libraryId", libraryId);
        Iterator<Exemplar> exemplarIterator = exemplarQuery.iterator();
        ArrayList<Exemplar> results = new ArrayList<Exemplar>();
        while (exemplarIterator.hasNext()) {
            Exemplar exemplar = exemplarIterator.next();
            results.add(exemplar);
        }
        return results;
    }

    @Override
    public ArrayList<Exemplar> searchLibraryExemplar(String text) throws Exception {
        CompassSearchSession search = DAO.getCompassInstance().openSearchSession();
        search.close();
        // TODO Implement search with compass
        return null;
    }

    @Override
    public String persistRegistration(Long libraryId, String googleAccount) throws Exception {
        String registrationId = libraryId + "-" + googleAccount;
        Registration registration = null;
        try {
            registration = DAO.getInstance().ofy().get(Registration.class, registrationId);
        } catch (NotFoundException e) {
            registration = new Registration(libraryId, googleAccount, null);
            Key<Registration> registrationKey = DAO.getInstance().ofy().put(registration);
            return registrationKey.getName();
        }
        throw new Exception("El registro del usuario " + googleAccount + " ya había sido completado"); 
    }

    @Override
    public ArrayList<LibraryUser> getAllUsers() throws Exception {
        ArrayList<LibraryUser> result = new ArrayList<LibraryUser>();
        Iterator<LibraryUser> libraryUserIterator = DAO.getInstance().ofy().query(LibraryUser.class).iterator();
        while (libraryUserIterator.hasNext()) {
            result.add(libraryUserIterator.next());
        }
        return result;
    }

    @Override
    public String persistBorrowing(Long libraryId, String googleAccount, Long libraryItemId) throws Exception {
        Borrowing borrowing = null;
        String borrowingId = libraryId + "-" + googleAccount + "-" + libraryItemId;
        
        try {
            borrowing = DAO.getInstance().ofy().get(Borrowing.class, borrowingId);
        } catch (NotFoundException e) {
            borrowing = new Borrowing();
            borrowing.setLibraryId(libraryId);
            borrowing.setUserGoogleAccount(googleAccount);
            borrowing.setLibraryItemId(libraryItemId);
            borrowing.setId(borrowingId);
            Key<Borrowing> borrowingKey = DAO.getInstance().ofy().put(borrowing);
            LibraryItem libraryItem = DAO.getInstance().ofy().get(LibraryItem.class, libraryItemId);
            libraryItem.setUserGoogleAccount(googleAccount);
            DAO.getInstance().ofy().put(libraryItem);
            
            return borrowingKey.getName();
        }
        throw new Exception("El préstamo del libro " + libraryItemId + " al usuario " + googleAccount + " ya había sido registrado");
    }

    @Override
    public ArrayList<Borrowing> getBorrowings(Long libraryId) throws Exception {
        Iterator<Borrowing> borrowingIterator = DAO.getInstance().ofy().query(Borrowing.class).filter("libraryId", libraryId).iterator();
        ArrayList<Borrowing> result = new ArrayList<Borrowing>();
        while (borrowingIterator.hasNext()) {
            result.add(borrowingIterator.next());
        }
        return result;
    }

    @Override
    public void removeBorrowing(String id) {
        Borrowing borrowing = DAO.getInstance().ofy().get(Borrowing.class, id);
        LibraryItem libraryItem = DAO.getInstance().ofy().get(LibraryItem.class, borrowing.getLibraryItemId());
        libraryItem.setUserGoogleAccount(null);
        DAO.getInstance().ofy().put(libraryItem);
        DAO.getInstance().ofy().delete(Borrowing.class, id);
    }
}
