package com.tais.biblionexus.client.login;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tais.biblionexus.client.services.LoginInfo;
import com.tais.biblionexus.client.services.LoginService;
import com.tais.biblionexus.client.services.LoginServiceAsync;

/**
 * Application main class
 * 
 * @author Abraham Max Santos Ramos <amsantosr@gmail.com>
 */
public class BiblioNexus implements EntryPoint {

    private final LoginServiceAsync loginService = GWT.create(LoginService.class);

    private LoginInfo loginInfo = null;

    public void onModuleLoad() {
        ServiceDefTarget endPointLogin = (ServiceDefTarget) loginService;
        endPointLogin.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/login");
        
        loginService.login(GWT.getHostPageBaseURL() + "BiblioNexus.html", new AsyncCallback<LoginInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                RootLayoutPanel.get().add(new Label(caught.getMessage()));
            }

            @Override
            public void onSuccess(LoginInfo result) {
                loginInfo = result;
                if (loginInfo.isLoggedIn()) {
                    if (loginInfo.isAdministrator()) {
                        if (loginInfo.isLibrarian() || loginInfo.isLibraryUser()) {
                            loadSelectMode();
                        } else {
                            Window.Location.assign(GWT.getHostPageBaseURL() + "Administrator.html");
                        }
                    } else if (loginInfo.isMultiLibrarian() || loginInfo.isMultiLibraryUser()) {
                        loadSelectMode();
                    } else if (loginInfo.isLibrarian() && loginInfo.isLibraryUser()) {
                        loadSelectMode();
                    } else if (loginInfo.isLibrarian()) {
                        Window.Location.assign(GWT.getHostPageBaseURL() + "Librarian.html");
                    } else if (loginInfo.isLibraryUser()) {
                        Window.Location.assign(GWT.getHostPageBaseURL() + "LibraryUser.html");
                    } else {
                        loadUnregisteredUser();
                    }
                } else {
                    loadLogin();
                }
            }
        });
    }

    private void loadLogin() {
        final VerticalPanel vpMainPanel = new VerticalPanel();
        final Anchor anchorStartSession = new Anchor();
        final Image imgLogo = new Image("/biblionexus-logo.png");
        final HTML htmlLogin = new HTML("Acceda a BiblioNexus desde su cuenta de Google.");
        final HTML htmlCredits = new HTML("Desarrollado por: <b>Abraham Max Santos Ramos</b>");

        imgLogo.setAltText("Logo de BiblioNexus");
        anchorStartSession.setText("Iniciar sesión");
        anchorStartSession.setHref(loginInfo.getLoginUrl());
        vpMainPanel.setWidth("100%");
        vpMainPanel.setSpacing(30);
        vpMainPanel.add(imgLogo);
        vpMainPanel.add(htmlCredits);
        vpMainPanel.add(htmlLogin);
        vpMainPanel.add(anchorStartSession);
        vpMainPanel.setCellHorizontalAlignment(imgLogo, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(htmlCredits, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(htmlLogin, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(anchorStartSession, HasAlignment.ALIGN_CENTER);

        RootLayoutPanel.get().add(vpMainPanel);
    }
    
    private void loadSelectMode() {
        final String html = "Bienvenido <b>" + loginInfo.getEmailAddress() + "</b> | <a href=\"" + loginInfo.getLogoutUrl() + "\">Cerrar sesión</a>";
        final HTML htmlWelcoming = new HTML(html);
        final VerticalPanel vpMainPanel = new VerticalPanel();
        final Image imgLogo = new Image("/biblionexus-logo.png");
        final FlexTable ftOptions = new FlexTable();
        
        ftOptions.setBorderWidth(1);

        imgLogo.setAltText("Logo de BiblioNexus");
        vpMainPanel.setWidth("100%");
        //vpMainPanel.setSpacing(30);
        vpMainPanel.add(htmlWelcoming);
        vpMainPanel.add(imgLogo);
        vpMainPanel.setCellHorizontalAlignment(htmlWelcoming, HasAlignment.ALIGN_RIGHT);
        vpMainPanel.setCellHorizontalAlignment(imgLogo, HasAlignment.ALIGN_CENTER);
        
        if (loginInfo.isAdministrator()) {
            ftOptions.setText(0, 0, "Administrar Sistema BiblioNexus");
            ftOptions.setWidget(0, 1, new Button("Administrar", new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    Window.Location.assign("/Administrator.html");
                }
            }));
        }

        TreeMap<Long,String> librariesAsLibrarian = loginInfo.getLibrariesAsLibrarian();
        if (librariesAsLibrarian != null) {
            Set<Entry<Long,String>> entries = librariesAsLibrarian.entrySet();
            Iterator<Entry<Long,String>> entriesIterator = entries.iterator();
            while (entriesIterator.hasNext()) {
                int newRow = ftOptions.getRowCount();
                final Entry<Long,String> entry = entriesIterator.next();
                ftOptions.setText(newRow, 0, entry.getValue());
                ftOptions.setWidget(newRow, 1, new Button("Administrar", new ClickHandler() {
                    
                    @Override
                    public void onClick(ClickEvent event) {
                        Window.Location.assign("/Librarian.html?libid=" + entry.getKey());
                    }
                }));
            }
        }

        TreeMap<Long,String> librariesAsUser = loginInfo.getLibrariesAsUser();
        if (librariesAsUser != null) {
            Set<Entry<Long,String>> entries = librariesAsUser.entrySet();
            Iterator<Entry<Long,String>> entriesIterator = entries.iterator();
            while (entriesIterator.hasNext()) {
                int newRow = ftOptions.getRowCount();
                final Entry<Long,String> entry = entriesIterator.next();
                ftOptions.setText(newRow, 0, entry.getValue());
                ftOptions.setWidget(newRow, 1, new Button("Relizar búsqueda", new ClickHandler() {
                    
                    @Override
                    public void onClick(ClickEvent event) {
                        Window.Location.assign("/LibraryUser.html?libid=" + entry.getKey());
                    }
                }));
            }
        }
        
        vpMainPanel.add(ftOptions);
        vpMainPanel.setCellHorizontalAlignment(ftOptions, HasAlignment.ALIGN_CENTER);
        
        RootLayoutPanel.get().add(vpMainPanel);
    }
    
    private void loadUnregisteredUser() {
        final VerticalPanel vpMainPanel = new VerticalPanel();
        final Image imgLogo = new Image("/biblionexus-logo.png");
        final Anchor anchorCloseSession = new Anchor("Cerrar sesión de Google");
        final Label lblError = new Label("Lo sentimos pero su cuenta de Google no está asociada a ningún servicio de BiblioNexus.");
        
        anchorCloseSession.setHref(loginInfo.getLogoutUrl());
        vpMainPanel.setWidth("100%");
        vpMainPanel.setSpacing(30);
        vpMainPanel.add(imgLogo);
        vpMainPanel.add(lblError);
        vpMainPanel.add(anchorCloseSession);
        vpMainPanel.setCellHorizontalAlignment(lblError, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(anchorCloseSession, HasAlignment.ALIGN_CENTER);
        RootLayoutPanel.get().add(vpMainPanel);
    }
}
