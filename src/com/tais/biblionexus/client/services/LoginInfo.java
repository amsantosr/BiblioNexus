package com.tais.biblionexus.client.services;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * 
 * @author Abraham Max Santos Ramos
 *
 */
@SuppressWarnings("serial")
public class LoginInfo implements Serializable {

    private boolean loggedIn = false;
    private boolean administrator = false;
    private String loginUrl = null;
    private String logoutUrl = null;
    private String emailAddress = null;
    private String displayName = null;
    private TreeMap<Long,String> librariesAsLibrarian;
    private TreeMap<Long,String> librariesAsUser;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public boolean isLibrarian() {
        return librariesAsLibrarian != null && librariesAsLibrarian.size() > 0;
    }

    public boolean isLibraryUser() {
        return librariesAsUser != null && librariesAsUser.size() > 0;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public void addLibraryAsLibrarian(Long id, String name) {
        if (librariesAsLibrarian == null) {
            librariesAsLibrarian = new TreeMap<Long, String>();
        }
        librariesAsLibrarian.put(id, name);
    }
    
    public void addLibraryAsUser(Long id, String name) {
        if (librariesAsUser == null) {
            librariesAsUser = new TreeMap<Long, String>();
        }
        librariesAsUser.put(id, name);
    }
    
    public TreeMap<Long, String> getLibrariesAsLibrarian() {
        return librariesAsLibrarian;
    }
    
    public TreeMap<Long, String> getLibrariesAsUser() {
        return librariesAsUser;
    }
    
    public boolean isMultiLibrarian() {
        return librariesAsLibrarian.size() > 1;
    }
    
    public boolean isMultiLibraryUser() {
        return librariesAsUser.size() > 1;
    }

    public String getLibraryName(Long libraryId) {
        return librariesAsLibrarian.get(libraryId);
    }
}
