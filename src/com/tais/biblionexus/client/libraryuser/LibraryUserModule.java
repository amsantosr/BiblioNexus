package com.tais.biblionexus.client.libraryuser;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tais.biblionexus.client.entities.LibraryItem;
import com.tais.biblionexus.client.services.ISBNService;
import com.tais.biblionexus.client.services.ISBNServiceAsync;
import com.tais.biblionexus.client.services.LoginInfo;
import com.tais.biblionexus.client.services.LoginService;
import com.tais.biblionexus.client.services.LoginServiceAsync;
import com.tais.biblionexus.client.services.PersistentService;
import com.tais.biblionexus.client.services.PersistentServiceAsync;

/**
 * GWT Module that shows the user interface for borrowers
 * 
 * @author Abraham Max Santos Ramos <amsantosr@gmail.com>
 * 
 */
public class LibraryUserModule implements EntryPoint {
    private final ISBNServiceAsync isbnService = GWT.create(ISBNService.class);

    private final LoginServiceAsync loginService = GWT.create(LoginService.class);

    private final PersistentServiceAsync persistentService = GWT.create(PersistentService.class);

    private LoginInfo loginInfo = null;

    @Override
    public void onModuleLoad() {
        ServiceDefTarget endPointIsbn = (ServiceDefTarget) isbnService;
        endPointIsbn.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/isbn");
        ServiceDefTarget endPointLogin = (ServiceDefTarget) loginService;
        endPointLogin.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/login");
        ServiceDefTarget endPointPersistent = (ServiceDefTarget) persistentService;
        endPointPersistent.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/persistent");

        loginService.login(GWT.getHostPageBaseURL() + "LibraryUser.html", new AsyncCallback<LoginInfo>() {

            @Override
            public void onFailure(Throwable caught) {
                RootPanel.get().add(new Label(caught.getMessage()));
            }

            @Override
            public void onSuccess(LoginInfo result) {
                loginInfo = result;
                if (loginInfo.isLoggedIn() && loginInfo.isLibraryUser()) {
                    setupModule();
                } else {
                    Window.Location.assign(GWT.getHostPageBaseURL() + "BiblioNexus.html");
                }
            }
        });
    }

    void setupModule() {
        final Image imgApplication = new Image("/biblionexus-logo.png");
        final VerticalPanel vpMainPanel = new VerticalPanel();
        final HTML htmlEmail = new HTML("Bienvenido <b>" + loginInfo.getEmailAddress() + "</b> | ");
        final Anchor anchorAccountSettings = new Anchor("Perfil");
        final Anchor anchorCloseSession = new Anchor("Cerrar sesión");
        final HorizontalPanel topPanel = new HorizontalPanel();
        final Button btnSearchBooks = new Button("Buscar libros");
        final TextBox tbxQuery = new TextBox();
        final HorizontalPanel hpSearchPanel = new HorizontalPanel();
        final Label lblInformation = new Label();
        final Grid gridBooks = new Grid();

        anchorAccountSettings.setHref("http://notiplemented.html");
        anchorCloseSession.setHref(loginInfo.getLogoutUrl());
        topPanel.setSpacing(5);
        topPanel.add(htmlEmail);
        topPanel.add(new HTML(" | "));
        topPanel.add(anchorAccountSettings);
        topPanel.add(new HTML(" | "));
        topPanel.add(anchorCloseSession);

        hpSearchPanel.setSpacing(20);
        hpSearchPanel.add(tbxQuery);
        hpSearchPanel.add(btnSearchBooks);

        vpMainPanel.setWidth("100%");
        // vpMainPanel.setSpacing(20);
        vpMainPanel.add(topPanel);
        vpMainPanel.add(imgApplication);
        vpMainPanel.add(hpSearchPanel);
        vpMainPanel.add(lblInformation);
        vpMainPanel.add(gridBooks);
        vpMainPanel.setCellHorizontalAlignment(topPanel, HasAlignment.ALIGN_RIGHT);
        vpMainPanel.setCellHorizontalAlignment(imgApplication, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(hpSearchPanel, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(lblInformation, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(gridBooks, HasAlignment.ALIGN_CENTER);

        btnSearchBooks.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                lblInformation.setText("Buscando...");
                persistentService.searchLibraryItem(tbxQuery.getText(), new AsyncCallback<ArrayList<LibraryItem>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        lblInformation.setText(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(ArrayList<LibraryItem> result) {
                        loadBooksGrid(result, gridBooks, lblInformation);
                    }

                });
            }
        });

        RootLayoutPanel.get().add(vpMainPanel);
    }

    private void loadBooksGrid(final ArrayList<LibraryItem> result, final Grid gridBooks, final Label lblInformation) {
        int numberOfResults = result.size();

        lblInformation.setText(numberOfResults + " resultados");
        gridBooks.resize(numberOfResults + 1, 4);
        gridBooks.setBorderWidth(1);
        gridBooks.setText(0, 0, "ISBN");
        gridBooks.setText(0, 1, "Autor");
        gridBooks.setText(0, 2, "Título");
        gridBooks.setText(0, 3, "Año");

        for (int row = 0; row < numberOfResults; ++row) {
            final LibraryItem currentBook = result.get(row);
            gridBooks.setText(row + 1, 0, currentBook.getIsbn());
            gridBooks.setText(row + 1, 1, currentBook.getAuthor());
            gridBooks.setText(row + 1, 2, currentBook.getTitle());
            gridBooks.setText(row + 1, 3, String.valueOf(currentBook.getYear()));
        }
    }
}
