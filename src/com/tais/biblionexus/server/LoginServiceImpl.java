package com.tais.biblionexus.server;

import java.util.Iterator;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Query;
import com.tais.biblionexus.client.entities.Library;
import com.tais.biblionexus.client.entities.Registration;
import com.tais.biblionexus.client.services.LoginInfo;
import com.tais.biblionexus.client.services.LoginService;

@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

    @Override
    public LoginInfo login(String requestUri) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        LoginInfo loginInfo = new LoginInfo();

        if (user != null) {
            loginInfo.setLoggedIn(true);
            loginInfo.setEmailAddress(user.getEmail());
            loginInfo.setDisplayName(user.getNickname());
            loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
            if (user.getEmail().equals("amsantosr@gmail.com")) {
                loginInfo.setAdministrator(true);
            }
            Query<Library> librarianQuery = DAO.getInstance().ofy().query(Library.class)
                    .filter("headGoogleAccount", user.getEmail());
            Iterator<Library> libraryIterator = librarianQuery.iterator();
            while (libraryIterator.hasNext()) {
                Library library = libraryIterator.next();
                loginInfo.addLibraryAsLibrarian(library.getId(), library.getName());
            }
            Query<Registration> libraryUserRegistrationQuery = DAO.getInstance().ofy().query(Registration.class)
                    .filter("libraryUserGoogleAccount", user.getEmail());
            Iterator<Registration> registrationIterator = libraryUserRegistrationQuery.iterator();
            while (registrationIterator.hasNext()) {
                Registration registration = registrationIterator.next();
                Long libraryId = registration.getLibraryId();
                Library library = DAO.getInstance().ofy().query(Library.class).filter("id", libraryId).get();
                loginInfo.addLibraryAsUser(registration.getLibraryId(), library.getName());
            }
        } else {
            loginInfo.setLoggedIn(false);
            loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
        }
        return loginInfo;
    }

}
