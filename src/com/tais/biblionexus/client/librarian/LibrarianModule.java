package com.tais.biblionexus.client.librarian;

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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.tais.biblionexus.client.common.Common;
import com.tais.biblionexus.client.common.WaitDialog;
import com.tais.biblionexus.client.entities.Borrowing;
import com.tais.biblionexus.client.entities.LibraryItem;
import com.tais.biblionexus.client.entities.LibraryUser;
import com.tais.biblionexus.client.services.ISBNService;
import com.tais.biblionexus.client.services.ISBNServiceAsync;
import com.tais.biblionexus.client.services.LoginInfo;
import com.tais.biblionexus.client.services.LoginService;
import com.tais.biblionexus.client.services.LoginServiceAsync;
import com.tais.biblionexus.client.services.PersistentService;
import com.tais.biblionexus.client.services.PersistentServiceAsync;
import com.tais.biblionexus.shared.FieldVerifier;

/**
 * This module launchs the interface for Librarian use case, such as registering users,
 * adding books and performing borrowings.
 * 
 * @author Abraham Max Santos Ramos <amsantosr@gmail.com>
 *
 */
public class LibrarianModule implements EntryPoint {

    private final PersistentServiceAsync persistentService = GWT.create(PersistentService.class);
    private final ISBNServiceAsync isbnService = GWT.create(ISBNService.class);
    private final LoginServiceAsync loginService = GWT.create(LoginService.class);
    private LoginInfo loginInfo = null;
    private Long libraryId = null;

    @Override
    public void onModuleLoad() {
        ServiceDefTarget endPointIsbn = (ServiceDefTarget) isbnService;
        endPointIsbn.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/isbn");
        ServiceDefTarget endPointLogin = (ServiceDefTarget) loginService;
        endPointLogin.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/login");
        ServiceDefTarget endPointPersistent = (ServiceDefTarget) persistentService;
        endPointPersistent.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/persistent");

        loginService.login(GWT.getHostPageBaseURL() + "BiblioNexus.html", new AsyncCallback<LoginInfo>() {

            @Override
            public void onSuccess(LoginInfo result) {
                loginInfo = result;
                if (loginInfo.isLoggedIn() && loginInfo.isLibrarian()) {
                    String code = Window.Location.getParameter("libid");
                    if (code != null) {
                        libraryId = Long.valueOf(code);
                    } else {
                        libraryId = loginInfo.getLibrariesAsLibrarian().firstKey();
                    }
                    setupModule();
                } else {
                    Window.Location.assign(GWT.getHostPageBaseURL() + "BiblioNexus.html");
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                RootPanel.get().add(new Label(caught.getMessage()));
            }
        });
    }

    void setupModule() {
        final VerticalPanel vpMainPanel = new VerticalPanel();
        final Image imgApplication = new Image("/biblionexus-logo.png");
        final HorizontalPanel hpTopPanel = new HorizontalPanel();
        final HTML htmlAccount = new HTML(loginInfo.getLibraryName(libraryId) + " | Bienvenido <b>" + loginInfo.getEmailAddress() + "</b> | ");
        final Anchor anchorBack = new Anchor("Volver");
        final Anchor anchorCloseSession = new Anchor("Cerrar sesión");
        final TabPanel tpCentralPanel = new TabPanel();
        final Panel panelUsers = createUsersPanel();
        final Panel panelLibraryItems = createLibraryItemsPanel();
        final Panel panelExemplars = createExemplarsPanel();
        final Panel panelBorrowings = createBorrowingsPanel();

        anchorBack.setHref(GWT.getHostPageBaseURL() + "BiblioNexus.html");
        anchorCloseSession.setHref(loginInfo.getLogoutUrl());
        hpTopPanel.add(htmlAccount);
        hpTopPanel.add(new Label(" | "));
        hpTopPanel.add(anchorBack);
        hpTopPanel.add(new Label(" | "));
        hpTopPanel.add(anchorCloseSession);

        tpCentralPanel.setWidth("90%");
        tpCentralPanel.add(panelUsers, "Usuarios");
        tpCentralPanel.add(panelLibraryItems, "Libros");
        //tpCentralPanel.add(panelExemplars, "Ejemplares");
        tpCentralPanel.add(panelBorrowings, "Préstamos");

        vpMainPanel.setWidth("100%");
        vpMainPanel.add(hpTopPanel);
        vpMainPanel.add(imgApplication);
        vpMainPanel.add(tpCentralPanel);
        vpMainPanel.setCellHorizontalAlignment(hpTopPanel, HasAlignment.ALIGN_RIGHT);
        vpMainPanel.setCellHorizontalAlignment(imgApplication, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(tpCentralPanel, HasAlignment.ALIGN_CENTER);

        RootPanel.get().add(vpMainPanel);
    }
    
    private Panel createUsersPanel() {
        final VerticalPanel vpUsersPanel = new VerticalPanel();
        final HorizontalPanel hpSearchPanel = new HorizontalPanel();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final Button btnRegisterUser = new Button("Registrar");
        final Button btnShowUsers = new Button("Listado");
        final Button btnAssignUser = new Button("Habilitar");
        final Label lblSearch = new Label("Buscar usuario: ");
        final TextBox tbQuery = new TextBox();
        final Button btnSearch = new Button("Buscar");
        final Grid gridUsers = new Grid();
        
        hpButtonsPanel.setSpacing(10);
        hpButtonsPanel.add(btnShowUsers);
        hpButtonsPanel.add(btnRegisterUser);
        hpButtonsPanel.add(btnAssignUser);
        hpSearchPanel.setSpacing(10);
        hpSearchPanel.add(lblSearch);
        hpSearchPanel.add(tbQuery);
        hpSearchPanel.add(btnSearch);
        vpUsersPanel.setSpacing(10);
        vpUsersPanel.add(hpButtonsPanel);
        vpUsersPanel.add(hpSearchPanel);
        vpUsersPanel.add(gridUsers);

        btnRegisterUser.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                DialogBox dlgRegisterUser = Common.createUserDialog(persistentService);
                dlgRegisterUser.center();
                dlgRegisterUser.show();
            }
        });
        
        btnShowUsers.addClickHandler(new ClickHandler() {
            final WaitDialog dlgLoading = new WaitDialog();
            
            @Override
            public void onClick(ClickEvent event) {
                dlgLoading.center();
                dlgLoading.show();
                dlgLoading.setMessage("Recuperando la lista de usuarios...");
                
                persistentService.getLibraryUsers(libraryId, new AsyncCallback<ArrayList<LibraryUser>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        dlgLoading.setMessage(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(ArrayList<LibraryUser> result) {
                        showLibraryUsersGrid(gridUsers, result);
                        dlgLoading.hide(true);
                    }
                });
            }
        });
        
        btnSearch.addClickHandler(new ClickHandler() {
            final WaitDialog dlgSearching = new WaitDialog();
            
            @Override
            public void onClick(ClickEvent event) {
                dlgSearching.center();
                dlgSearching.show();
                dlgSearching.setMessage("Buscando...");
                
                persistentService.getLibraryUser(tbQuery.getText(), new AsyncCallback<LibraryUser>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        dlgSearching.setMessage(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(LibraryUser result) {
                        if (result != null) {
                            ArrayList<LibraryUser> singleArray = new ArrayList<LibraryUser>();
                            singleArray.add(result);
                            showLibraryUsersGrid(gridUsers, singleArray);
                            dlgSearching.hide(true);
                        } else {
                            dlgSearching.setMessage("No se encontró ningún resultado.");
                        }
                    }
                });
            }
        });
        
        btnAssignUser.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                DialogBox dlgAssignUser = createAssignUserDialog();
                dlgAssignUser.center();
                dlgAssignUser.show();
            }
        });
        
        return vpUsersPanel;
    }
    
    private Panel createLibraryItemsPanel() {
        final VerticalPanel vpLibraryItemsPanel = new VerticalPanel();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final HorizontalPanel hpSearchPanel = new HorizontalPanel();
        final Button btnAddRandom = new Button("Añadir libros de ejemplo");
        final Button btnAddLibraryItem = new Button("Registrar");
        final Button btnShowLibraryItems = new Button("Listado");
        final Label lblLookFor = new Label("Buscar :");
        final TextBox tbQuery = new TextBox();
        final Button btnSearch = new Button("Buscar");
        final Grid gridLibraryItems = new Grid();

        hpButtonsPanel.add(btnShowLibraryItems);
        hpButtonsPanel.add(btnAddLibraryItem);
        hpButtonsPanel.add(btnAddRandom);
        hpSearchPanel.add(lblLookFor);
        hpSearchPanel.add(tbQuery);
        hpSearchPanel.add(btnSearch);
        
        vpLibraryItemsPanel.add(hpButtonsPanel);
        vpLibraryItemsPanel.add(hpSearchPanel);
        vpLibraryItemsPanel.add(gridLibraryItems);
        
        btnShowLibraryItems.addClickHandler(new ClickHandler() {
            final WaitDialog dlgWait = new WaitDialog();
            
            @Override
            public void onClick(ClickEvent event) {
                dlgWait.setMessage("Recuperando la lista de libros...");
                dlgWait.center();
                dlgWait.show();
                persistentService.getAllLibraryItems(new AsyncCallback<ArrayList<LibraryItem>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        dlgWait.setMessage(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(ArrayList<LibraryItem> result) {
                        showLibraryItemsGrid(gridLibraryItems, result);
                        dlgWait.hide(true);
                    }
                });
            }
        });
        
        btnAddLibraryItem.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DialogBox dlgAddBook = createAddBookDialog();
                dlgAddBook.center();
                dlgAddBook.show();
            }
        });
        
        btnAddRandom.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                DialogBox dlgAddRandom = createAddRandomBooksDialog();
                dlgAddRandom.center();
                dlgAddRandom.show();
            }
        });
        
        btnSearch.addClickHandler(new ClickHandler() {
            WaitDialog dlgWait = new WaitDialog();
            
            @Override
            public void onClick(ClickEvent event) {
                dlgWait.center();
                dlgWait.show();
                dlgWait.setMessage("Buscando...");
                persistentService.searchLibraryItem(tbQuery.getText(), new AsyncCallback<ArrayList<LibraryItem>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        dlgWait.setMessage(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(ArrayList<LibraryItem> result) {
                        showLibraryItemsGrid(gridLibraryItems, result);
                        dlgWait.hide(true);
                    }
                });
            }
        });
        
        return vpLibraryItemsPanel;
    }
    
    private Panel createExemplarsPanel() {
        final VerticalPanel vpExemplarsPanel = new VerticalPanel();
        final HorizontalPanel hpSearchPanel = new HorizontalPanel();
        final Label lblLookFor = new Label("Buscar :");
        final TextBox tbQuery = new TextBox();
        final Button btnSearch = new Button("Buscar");
        final Grid gridExemplars = new Grid();

        hpSearchPanel.add(lblLookFor);
        hpSearchPanel.add(tbQuery);
        hpSearchPanel.add(btnSearch);

        vpExemplarsPanel.add(hpSearchPanel);
        vpExemplarsPanel.add(gridExemplars);
        
        btnSearch.addClickHandler(new ClickHandler() {
            WaitDialog dlgWait = new WaitDialog();
            
            @Override
            public void onClick(ClickEvent event) {
                dlgWait.center();
                dlgWait.show();
                dlgWait.setMessage("Buscando...");
                
                
            }
        });
        
        return vpExemplarsPanel;
    }
    
    private Panel createBorrowingsPanel() {
        final VerticalPanel vpBorrowingsPanel = new VerticalPanel();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final Button btnShowList = new Button("Listado");
        final Grid gridBorrowings = new Grid();
        
        vpBorrowingsPanel.add(hpButtonsPanel);
        vpBorrowingsPanel.add(gridBorrowings);
        hpButtonsPanel.add(btnShowList);
        
        btnShowList.addClickHandler(new ClickHandler() {
            final WaitDialog dlgWait = new WaitDialog();
            
            @Override
            public void onClick(ClickEvent event) {
                dlgWait.center();
                dlgWait.show();
                dlgWait.setMessage("Recuperando la lista de préstamos...");
                persistentService.getBorrowings(libraryId, new AsyncCallback<ArrayList<Borrowing>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        dlgWait.setMessage(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(ArrayList<Borrowing> result) {
                        showBorrowingsGrid(gridBorrowings, result);
                        dlgWait.hide(true);
                    }
                });
            }
        });
        
        return vpBorrowingsPanel;
    }

    private DialogBox createAddBookDialog() {
        final DialogBox dlgAddBook = new DialogBox();
        final VerticalPanel vpMainPanel = new VerticalPanel();
        final HorizontalPanel hpISBN = new HorizontalPanel();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final Grid gridForm = new Grid(8, 2);
        final TextBox tbxISBN = new TextBox();
        final Button btnLookForISBN = new Button("Buscar ISBN");
        final PopupPanel popupISBNResult = new PopupPanel();
        final TextBox tbxTitle = new TextBox();
        final TextBox tbxAuthor = new TextBox();
        final TextBox tbxPublisher = new TextBox();
        final TextArea taDescription = new TextArea();
        final TextBox tbxLanguage = new TextBox();
        final IntegerBox ibxPages = new IntegerBox();
        final IntegerBox ibxYear = new IntegerBox();
        final Label lblInformation = new Label();
        final Button btnAddBook = new Button("Agregar");
        final Button btnCloseDialog = new Button("Cerrar");
        
        class WidgetManager {
            public void enableWidgets(boolean flag) {
                tbxISBN.setEnabled(flag);
                btnLookForISBN.setEnabled(flag);
                tbxTitle.setEnabled(flag);
                tbxAuthor.setEnabled(flag);
                tbxPublisher.setEnabled(flag);
                tbxLanguage.setEnabled(flag);
                ibxPages.setEnabled(flag);
                ibxYear.setEnabled(flag);
                btnAddBook.setEnabled(flag);
            }
            
            public void clearWidgets() {
                tbxISBN.setText("");
                tbxTitle.setText("");
                tbxAuthor.setText("");
                tbxPublisher.setText("");
                taDescription.setText("");
                tbxLanguage.setText("");
                ibxPages.setText("");
                ibxYear.setText("");
            }
        }
        
        final WidgetManager widgetManager = new WidgetManager();

        tbxISBN.setMaxLength(13);
        tbxISBN.setVisibleLength(13);
        tbxTitle.setVisibleLength(50);
        tbxAuthor.setVisibleLength(50);

        hpISBN.add(tbxISBN);
        hpISBN.add(btnLookForISBN);

        /*
         * lbLanguage.addItem("Español"); lbLanguage.addItem("Inglés");
         * lbLanguage.addItem("Portugués"); lbLanguage.addItem("Italiano");
         * lbLanguage.addItem("Francés"); lbLanguage.addItem("Alemán");
         * lbLanguage.addItem("Otro");
         */

        gridForm.setText(0, 0, "Código ISBN: ");
        gridForm.setText(1, 0, "Título: ");
        gridForm.setText(2, 0, "Autor: ");
        gridForm.setText(3, 0, "Editor: ");
        gridForm.setText(4, 0, "Descripción: ");
        gridForm.setText(5, 0, "Idioma: ");
        gridForm.setText(6, 0, "Páginas: ");
        gridForm.setText(7, 0, "Año de publicación: ");

        gridForm.setWidget(0, 1, hpISBN);
        gridForm.setWidget(1, 1, tbxTitle);
        gridForm.setWidget(2, 1, tbxAuthor);
        gridForm.setWidget(3, 1, tbxPublisher);
        gridForm.setWidget(4, 1, taDescription);
        gridForm.setWidget(5, 1, tbxLanguage);
        gridForm.setWidget(6, 1, ibxPages);
        gridForm.setWidget(7, 1, ibxYear);

        hpButtonsPanel.setSpacing(10);
        hpButtonsPanel.add(btnAddBook);
        hpButtonsPanel.add(btnCloseDialog);

        vpMainPanel.add(gridForm);
        vpMainPanel.add(lblInformation);
        vpMainPanel.add(hpButtonsPanel);
        vpMainPanel.setCellHorizontalAlignment(lblInformation, HorizontalPanel.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(hpButtonsPanel, HasAlignment.ALIGN_CENTER);

        dlgAddBook.getCaption().setHTML("<b>Agregar Libro</b>");
        dlgAddBook.setWidget(vpMainPanel);

        btnLookForISBN.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (FieldVerifier.isValidISBN(tbxISBN.getText())) {
                    widgetManager.enableWidgets(false);
                    isbnService.getBookXMLInfo(tbxISBN.getText(), new AsyncCallback<String>() {

                        @Override
                        public void onSuccess(String libroXMLInfo) {
                            fillInAddBookForm(libroXMLInfo);
                            widgetManager.enableWidgets(true);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            widgetManager.enableWidgets(true);
                            popupISBNResult.setWidget(new Label("No se encontró información"));
                            popupISBNResult.setAutoHideEnabled(true);
                            popupISBNResult.showRelativeTo(tbxISBN);
                        }
                    });
                } else {
                    popupISBNResult.setWidget(new Label("Código ISBN inválido"));
                    popupISBNResult.setAutoHideEnabled(true);
                    popupISBNResult.showRelativeTo(tbxISBN);
                }
            }

            private void fillInAddBookForm(String xml) {
                try {
                    Document dom = XMLParser.parse(xml);
                    try {
                        String title = dom.getElementsByTagName("Title").item(0).getFirstChild().getNodeValue();
                        tbxTitle.setText(title);
                    } finally {
                    }
                    try {
                        String author = dom.getElementsByTagName("AuthorsText").item(0).getFirstChild().getNodeValue();
                        tbxAuthor.setText(author);
                    } finally {
                    }
                    try {
                        String publisher = dom.getElementsByTagName("PublisherText").item(0).getFirstChild()
                                .getNodeValue();
                        tbxPublisher.setText(publisher);
                    } finally {
                    }
                    try {
                        String summary = dom.getElementsByTagName("Summary").item(0).getFirstChild().getNodeValue();
                        taDescription.setText(summary);
                    } finally {
                    }
                    try {
                        String languageCode = dom.getElementsByTagName("Details").item(0).getAttributes()
                                .getNamedItem("language").getNodeValue();
                        tbxLanguage.setText(languageCode);
                    } finally {
                    }
                } finally {
                }
            }
        });

        btnAddBook.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                lblInformation.setText("Guardando los datos...");
                widgetManager.enableWidgets(false);
                LibraryItem libraryItem = new LibraryItem();
                libraryItem.setIsbn(tbxISBN.getText());
                libraryItem.setTitle(tbxTitle.getText());
                libraryItem.setAuthor(tbxAuthor.getText());
                libraryItem.setPublisher(tbxPublisher.getText());
                libraryItem.setDescription(taDescription.getText());
                libraryItem.setLanguage(tbxLanguage.getText());
                libraryItem.setPages(ibxPages.getValue());
                libraryItem.setYear(ibxYear.getValue());

                persistentService.persistLibraryItem(libraryItem, new AsyncCallback<Long>() {

                    @Override
                    public void onSuccess(Long result) {
                        widgetManager.clearWidgets();
                        widgetManager.enableWidgets(true);
                        lblInformation.setText("Datos de libro almacenados");
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        widgetManager.enableWidgets(true);
                        lblInformation.setText("Ocurrió un error al guardar los datos");
                    }
                });
            }
        });

        btnCloseDialog.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dlgAddBook.hide(true);
            }
        });

        return dlgAddBook;
    }

    private void showLibraryItemsGrid(final Grid gridLibraryItems, final ArrayList<LibraryItem> libraryItemsArray) {
        int numberOfResults = libraryItemsArray.size();
        gridLibraryItems.resize(numberOfResults + 1, 5);
        gridLibraryItems.setBorderWidth(1);
        gridLibraryItems.setHTML(0, 0, "<b>ISBN</b>");
        gridLibraryItems.setHTML(0, 1, "<b>Autor</b>");
        gridLibraryItems.setHTML(0, 2, "<b>Título</b>");
        gridLibraryItems.setHTML(0, 3, "<b>Año</b>");
        gridLibraryItems.setHTML(0, 4, "<b>Estado</b>");

        for (int row = 0; row < numberOfResults; ++row) {
            final LibraryItem libraryItem = libraryItemsArray.get(row);
            gridLibraryItems.setText(row + 1, 0, libraryItem.getIsbn());
            gridLibraryItems.setText(row + 1, 1, libraryItem.getAuthor());
            gridLibraryItems.setText(row + 1, 2, libraryItem.getTitle());
            gridLibraryItems.setText(row + 1, 3, String.valueOf(libraryItem.getYear()));
            
            String borrower = libraryItem.getUserGoogleAccount();
            if (borrower != null) {
                gridLibraryItems.setText(row + 1, 4, "Prestado");
            } else {
                final Button btnLend = new Button("Prestar");
                btnLend.addClickHandler(new ClickHandler() {
                    
                    @Override
                    public void onClick(ClickEvent event) {
                        DialogBox lendDialog = createLendDialog(libraryItem.getId());
                        lendDialog.center();
                        lendDialog.show();
                    }
                });
                gridLibraryItems.setWidget(row + 1, 4, btnLend);
            }
        }
    }

    private void showLibraryUsersGrid(final Grid gridUsers, final ArrayList<LibraryUser> libraryUsers) {
        int numberOfResults = libraryUsers.size();
        gridUsers.resize(numberOfResults + 1, 5);
        gridUsers.setBorderWidth(1);
        gridUsers.setHTML(0, 0, "<b>DNI</b>");
        gridUsers.setHTML(0, 1, "<b>Google Account</b>");
        gridUsers.setHTML(0, 2, "<b>Apellido paterno</b>");
        gridUsers.setHTML(0, 3, "<b>Apellido materno</b>");
        gridUsers.setHTML(0, 4, "<b>Nombres</b>");

        for (int row = 0; row < numberOfResults; ++row) {
            final LibraryUser currentUser = libraryUsers.get(row);
            gridUsers.setText(row + 1, 0, currentUser.getDni());
            gridUsers.setText(row + 1, 1, currentUser.getGoogleAccount());
            gridUsers.setText(row + 1, 2, currentUser.getFatherLastName());
            gridUsers.setText(row + 1, 3, currentUser.getMotherLastName());
            gridUsers.setText(row + 1, 4, currentUser.getName());
        }
    }

    private DialogBox createAddRandomBooksDialog() {
        final DialogBox dialog = new DialogBox();
        final VerticalPanel verPanel = new VerticalPanel();
        final HorizontalPanel horPanelWidgets = new HorizontalPanel();
        final HorizontalPanel horPanelButtons = new HorizontalPanel();
        final Label lblQuantity = new Label("Cantidad: ");
        final IntegerBox ibxQuantity = new IntegerBox();
        final Button btnAdd = new Button("Agregar");
        final Button btnClose = new Button("Cerrar");
        final Label lblInformation = new Label("");

        ibxQuantity.setMaxLength(3);
        ibxQuantity.setVisibleLength(3);
        horPanelWidgets.setSpacing(10);
        horPanelWidgets.add(lblQuantity);
        horPanelWidgets.add(ibxQuantity);
        horPanelButtons.add(btnAdd);
        horPanelButtons.add(btnClose);
        horPanelButtons.setSpacing(10);
        lblInformation.setStyleName("informacionStyle");
        verPanel.setSpacing(10);
        verPanel.add(horPanelWidgets);
        verPanel.add(lblInformation);
        verPanel.add(horPanelButtons);
        dialog.setText("Agregar libros aleatorios");
        dialog.setWidget(verPanel);

        btnAdd.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                lblInformation.setText("Generando datos...");
                persistentService.fillLibraryItems(ibxQuantity.getValue(), new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        lblInformation.setText(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Void result) {
                        lblInformation.setText("Datos generados");
                    }
                });
            }
        });

        btnClose.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dialog.hide(true);
            }
        });

        return dialog;
    }
    
    private DialogBox createAssignUserDialog() {
        final DialogBox dlgAssignUser = new DialogBox();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final VerticalPanel vpDialogPanel = new VerticalPanel();
        final Label lblInformation = new Label();
        final Button btnAssign = new Button("Asignar a esta librería");
        final Button btnCancel = new Button("Cerrar");
        final Grid gridForm = new Grid(1, 2);
        final TextBox tbGoogleAccount = new TextBox();

        gridForm.setText(0, 0, "Cuenta de Google: ");
        gridForm.setWidget(0, 1, tbGoogleAccount);

        hpButtonsPanel.setSpacing(10);
        hpButtonsPanel.add(btnAssign);
        hpButtonsPanel.add(btnCancel);
        vpDialogPanel.add(gridForm);
        vpDialogPanel.add(lblInformation);
        vpDialogPanel.add(hpButtonsPanel);
        vpDialogPanel.setCellHorizontalAlignment(lblInformation, HasAlignment.ALIGN_CENTER);
        vpDialogPanel.setCellHorizontalAlignment(hpButtonsPanel, HasAlignment.ALIGN_CENTER);
        dlgAssignUser.getCaption().setHTML("<b>Asignar usuario a esta librería</b>");
        dlgAssignUser.setWidget(vpDialogPanel);

        btnAssign.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                lblInformation.setText("Registrando usuario...");
                btnAssign.setEnabled(false);
                tbGoogleAccount.setEnabled(false);
                persistentService.persistRegistration(libraryId, tbGoogleAccount.getText(), new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        lblInformation.setText(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(String result) {
                        lblInformation.setText("Registered con ID: " + result);
                    }
                });
            }
        });

        btnCancel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dlgAssignUser.hide(true);
            }
        });

        return dlgAssignUser;
    }
    
    DialogBox createLendDialog(Long libraryItemId) {
        final DialogBox dlgLendDialog = new DialogBox();
        final VerticalPanel vpContent = new VerticalPanel();
        final Grid gridWidgets = new Grid();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final Label lblInfo = new Label();
        final TextBox tbLibraryItemId = new TextBox();
        final TextBox tbUserGoogleAccount = new TextBox();
        final Button btnClose = new Button("Cerrar");
        final Button btnLend = new Button("Prestar");
        
        tbLibraryItemId.setText(String.valueOf(libraryItemId));
        gridWidgets.resize(2, 2);
        gridWidgets.setText(0, 0, "Código de libro");
        gridWidgets.setText(1, 0, "Código de usuario");
        gridWidgets.setWidget(0, 1, tbLibraryItemId);
        gridWidgets.setWidget(1, 1, tbUserGoogleAccount);
        
        hpButtonsPanel.add(btnLend);
        hpButtonsPanel.add(btnClose);
        
        vpContent.add(gridWidgets);
        vpContent.add(lblInfo);
        vpContent.add(hpButtonsPanel);
        
        dlgLendDialog.setHTML("Prestar libro");
        dlgLendDialog.setWidget(vpContent);
        
        btnLend.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                persistentService.persistBorrowing(libraryId, tbUserGoogleAccount.getText(), Long.valueOf(tbLibraryItemId.getText()), new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        lblInfo.setText(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(String result) {
                        lblInfo.setText("El libro ha sido prestado con código " + result);
                    }
                });
            }
        });
        
        btnClose.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                dlgLendDialog.hide(true);
            }
        });
        
        return dlgLendDialog;
    }
    
    public void showBorrowingsGrid(final Grid gridBorrowings, final ArrayList<Borrowing> borrowingsArray) {
        final int numberOfResults = borrowingsArray.size();
        gridBorrowings.resize(numberOfResults + 1, 3);
        gridBorrowings.setHTML(0, 0, "<b>Usuario</b>");
        gridBorrowings.setHTML(0, 1, "<b>Libro</b>");
        gridBorrowings.setHTML(0, 2, "<b>Devolución</b>");
        
        for (int row = 0; row < numberOfResults; ++row) {
            final Borrowing borrowing = borrowingsArray.get(row);
            gridBorrowings.setText(row + 1, 0, borrowing.getUserGoogleAccount());
            gridBorrowings.setText(row + 1, 1, String.valueOf(borrowing.getLibraryItemId()));
            final Button btnDevolver = new Button("Devolver", new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    boolean ok = Window.confirm("¿Desea eliminar el préstamo?");
                    if (ok) {
                        persistentService.removeBorrowing(borrowing.getId(), new AsyncCallback<Void>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                Window.alert(caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Void result) {
                                Window.alert("El préstamo ha sido eliminado");
                            }
                        });
                    }
                }
            });
            gridBorrowings.setWidget(row + 1, 2, btnDevolver);
        }
    }

}
