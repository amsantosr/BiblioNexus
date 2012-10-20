package com.tais.biblionexus.client.administrator;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tais.biblionexus.client.common.Common;
import com.tais.biblionexus.client.common.WaitDialog;
import com.tais.biblionexus.client.entities.Library;
import com.tais.biblionexus.client.entities.LibraryUser;
import com.tais.biblionexus.client.services.LoginInfo;
import com.tais.biblionexus.client.services.LoginService;
import com.tais.biblionexus.client.services.LoginServiceAsync;
import com.tais.biblionexus.client.services.PersistentService;
import com.tais.biblionexus.client.services.PersistentServiceAsync;

/**
 * Module for Administrator user, i.e. me :)
 * 
 * @author Abraham Max Santos Ramos <amsantosr@gmail.com>
 * 
 */
public class AdministratorModule implements EntryPoint {

    private final LoginServiceAsync loginService = GWT.create(LoginService.class);

    private final PersistentServiceAsync persistentService = GWT.create(PersistentService.class);

    private LoginInfo loginInfo = null;

    @Override
    public void onModuleLoad() {
        ServiceDefTarget endPointLogin = (ServiceDefTarget) loginService;
        endPointLogin.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/login");
        ServiceDefTarget endPointPersistent = (ServiceDefTarget) persistentService;
        endPointPersistent.setServiceEntryPoint(GWT.getHostPageBaseURL() + "servlet/persistent");

        loginService.login(GWT.getHostPageBaseURL() + "BiblioNexus.html", new AsyncCallback<LoginInfo>() {

            @Override
            public void onFailure(Throwable caught) {
                RootPanel.get().add(new Label(caught.getMessage()));
            }

            @Override
            public void onSuccess(LoginInfo result) {
                loginInfo = result;
                if (loginInfo.isLoggedIn() && loginInfo.isAdministrator()) {
                    setupModule();
                } else {
                    Window.Location.assign(GWT.getHostPageBaseURL() + "BiblioNexus.html");
                }
            }
        });
    }

    void setupModule() {
        final HorizontalPanel hpTopPanel = new HorizontalPanel();
        final Image imgApplication = new Image("/biblionexus-logo.png");
        final VerticalPanel vpMainPanel = new VerticalPanel();
        final HTML htmlWelcoming = new HTML("Bienvenido <b>" + loginInfo.getEmailAddress() + " | ");
        final Anchor anchorBack = new Anchor("Volver");
        final Anchor anchorCloseSession = new Anchor("Cerrar sesión");
        final TabPanel tpCentralPanel = new TabPanel();
        final Panel panelUsers = createUsersPanel();
        final Panel panelLibraries = createLibrariesPanel();
        final Administrator panelAdministrator = new Administrator();
        
        anchorBack.setHref(GWT.getHostPageBaseURL() + "BiblioNexus.html");
        anchorCloseSession.setHref(loginInfo.getLogoutUrl());
        //hpTopPanel.setSpacing(5);
        hpTopPanel.add(htmlWelcoming);
        hpTopPanel.add(anchorBack);
        hpTopPanel.add(new HTML(" | "));
        hpTopPanel.add(anchorCloseSession);
        
        tpCentralPanel.setWidth("90%");
        tpCentralPanel.add(panelUsers, "Usuarios");
        tpCentralPanel.add(panelLibraries, "Librerías");
        tpCentralPanel.add(panelAdministrator, "Temporal");

        vpMainPanel.setWidth("100%");
        // vpMainPanel.setSpacing(20);
        vpMainPanel.add(hpTopPanel);
        vpMainPanel.add(imgApplication);
        vpMainPanel.add(tpCentralPanel);
        vpMainPanel.setCellHorizontalAlignment(hpTopPanel, HasAlignment.ALIGN_RIGHT);
        vpMainPanel.setCellHorizontalAlignment(imgApplication, HasAlignment.ALIGN_CENTER);
        vpMainPanel.setCellHorizontalAlignment(tpCentralPanel, HasAlignment.ALIGN_CENTER);

        RootLayoutPanel.get().add(vpMainPanel);
    }
    
    private Panel createUsersPanel() {
        final VerticalPanel vpUsersPanel = new VerticalPanel();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final HorizontalPanel hpSearchPanel = new HorizontalPanel();
        final Button btnShowUsers = new Button("Listado");
        final Button btnRegisterUser = new Button("Registrar");
        final Label lblLookFor = new Label("Buscar: ");
        final TextBox tbQuery = new TextBox();
        final Button btnSearch = new Button("Buscar");
        final Grid gridUsers = new Grid();
        
        hpButtonsPanel.add(btnShowUsers);
        hpButtonsPanel.add(btnRegisterUser);
        
        hpSearchPanel.add(lblLookFor);
        hpSearchPanel.add(tbQuery);
        hpSearchPanel.add(btnSearch);
        
        vpUsersPanel.setSpacing(10);
        vpUsersPanel.add(hpButtonsPanel);
        vpUsersPanel.add(hpSearchPanel);
        vpUsersPanel.add(gridUsers);
        
        btnShowUsers.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                final WaitDialog dlgWait = new WaitDialog();
                dlgWait.center();
                dlgWait.show();
                dlgWait.setMessage("Recuperando la lista de usuarios...");
                
                persistentService.getAllUsers(new AsyncCallback<ArrayList<LibraryUser>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        dlgWait.setMessage(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(ArrayList<LibraryUser> result) {
                        showLibraryUsersGrid(gridUsers, result);
                        dlgWait.hide(true);
                    }
                });
            }
        });
        
        btnRegisterUser.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DialogBox dlgRegisterLibrarian = Common.createUserDialog(persistentService);
                dlgRegisterLibrarian.center();
                dlgRegisterLibrarian.show();
            }
        });
        
        btnSearch.addClickHandler(new ClickHandler() {
            final WaitDialog waitDialog = new WaitDialog();
            
            @Override
            public void onClick(ClickEvent event) {
                waitDialog.center();
                waitDialog.show();
                waitDialog.setMessage("Buscando...");
                persistentService.getLibraryUser(tbQuery.getText(), new AsyncCallback<LibraryUser>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        waitDialog.setMessage(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(LibraryUser result) {
                        if (result != null) {
                            ArrayList<LibraryUser> singleArray = new ArrayList<LibraryUser>();
                            singleArray.add(result);
                            showLibraryUsersGrid(gridUsers, singleArray);
                            waitDialog.hide(true);
                        } else {
                            waitDialog.setMessage("No se ha encontrado al usuario.");
                        }
                    }
                });
            }
        });
        
        return vpUsersPanel;
    }
    
    private Panel createLibrariesPanel() {
        final VerticalPanel vpLibrariesPanel = new VerticalPanel();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final Button btnRefreshLibrariesGrid = new Button("Listado");
        final Button btnRegisterLibrary = new Button("Registrar");
        final Grid gridLibraries = new Grid();
        
        hpButtonsPanel.add(btnRefreshLibrariesGrid);
        hpButtonsPanel.add(btnRegisterLibrary);
        
        vpLibrariesPanel.setSpacing(10);
        vpLibrariesPanel.add(hpButtonsPanel);
        vpLibrariesPanel.add(gridLibraries);
        
        btnRefreshLibrariesGrid.addClickHandler(new ClickHandler() {
            final WaitDialog dlgWait = new WaitDialog();

            @Override
            public void onClick(ClickEvent event) {
                dlgWait.center();
                dlgWait.show();
                dlgWait.setMessage("Recuperando información...");
                persistentService.getAllLibraries(new AsyncCallback<ArrayList<Library>>() {

                    @Override
                    public void onSuccess(ArrayList<Library> result) {
                        loadLibrariesGrid(gridLibraries, result);
                        dlgWait.hide(true);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        dlgWait.setMessage(caught.getMessage());
                    }
                });
            }
        });

        btnRegisterLibrary.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DialogBox dlgRegisterLibrary = createLibraryDialog(null);
                dlgRegisterLibrary.center();
                dlgRegisterLibrary.show();
            }
        });
        
        return vpLibrariesPanel;
    }

    private void loadLibrariesGrid(Grid librariesGrid, ArrayList<Library> librariesArray) {
        librariesGrid.resize(librariesArray.size() + 1, 5);
        librariesGrid.setBorderWidth(1);
        librariesGrid.setText(0, 0, "ID");
        librariesGrid.setText(0, 1, "Nombre");
        librariesGrid.setText(0, 2, "Administrador");
        librariesGrid.setText(0, 3, "Editar");
        librariesGrid.setText(0, 4, "Eliminar");
        for (int index = 0; index < librariesArray.size(); ++index) {
            final Library currentLibrary = librariesArray.get(index);
            librariesGrid.setText(index + 1, 0, String.valueOf(currentLibrary.getId()));
            librariesGrid.setText(index + 1, 1, currentLibrary.getName());
            librariesGrid.setText(index + 1, 2, currentLibrary.getHeadName());
            librariesGrid.setWidget(index + 1, 3, new Button("Editar", new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    DialogBox libraryDialog = createLibraryDialog(currentLibrary.getId());
                    libraryDialog.center();
                    libraryDialog.show();
                }
            }));
            librariesGrid.setWidget(index + 1, 4, new Button("X", new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    // create a DialogBox and confirm the request
                }
            }));
        }
    }

    private DialogBox createLibraryDialog(Long libraryId) {
        final DialogBox dlgRegisterLibrary = new DialogBox();
        final VerticalPanel vpDialogPanel = new VerticalPanel();
        final Grid gridForm = new Grid(9, 2);
        final Button btnAddLibrary = new Button("Guardar");
        final Button btnClose = new Button("Cerrar");
        final HorizontalPanel hpFormButtons = new HorizontalPanel();
        final TextBox tbName = new TextBox();
        final TextArea txaAddress = new TextArea();
        final TextBox tbCity = new TextBox();
        final ListBox lbDepartment = Common.createListBoxDepartment();
        final TextBox tbPhoneNumber = new TextBox();
        final TextBox tbGoogleAccount = new TextBox();
        final TextBox tbHeadName = new TextBox();
        final TextArea taTopics = new TextArea();
        final ListBox lbxLibraryType = new ListBox(false);
        final Label lblInformation = new Label();
        
        class WidgetManager {
            public void enableWidgets(boolean flag) {
                tbName.setEnabled(flag);
                lbxLibraryType.setEnabled(flag);
                txaAddress.setEnabled(flag);
                tbCity.setEnabled(flag);
                lbDepartment.setEnabled(flag);
                tbPhoneNumber.setEnabled(flag);
                tbGoogleAccount.setEnabled(flag);
                tbHeadName.setEnabled(flag);
                taTopics.setEnabled(flag);
            }
            
            public void clearWidgets() {
                tbName.setText("");
                lbxLibraryType.setSelectedIndex(0);
                txaAddress.setText("");
                tbCity.setText("");
                lbDepartment.setSelectedIndex(0);
                tbPhoneNumber.setText("");
                tbGoogleAccount.setText("");
                tbHeadName.setText("");
                taTopics.setText("");
            }
        }
        
        final WidgetManager widgetManager = new WidgetManager();

        
        // setup library type widgets
        lbxLibraryType.addItem("Pública");
        lbxLibraryType.addItem("Escolar");
        lbxLibraryType.addItem("De instituto");
        lbxLibraryType.addItem("Universitaria");
        lbxLibraryType.addItem("De Organismo no gubernamental");
        lbxLibraryType.addItem("Organismo internacional");
        lbxLibraryType.addItem("Especializada o científica");

        gridForm.setText(0, 0, "Nombre del establecimiento: ");
        gridForm.setText(1, 0, "Tipo de librería: ");
        gridForm.setText(2, 0, "Dirección: ");
        gridForm.setText(3, 0, "Ciudad: ");
        gridForm.setText(4, 0, "Departamento: ");
        gridForm.setText(5, 0, "Número telefónico: ");
        gridForm.setText(6, 0, "Cuenta de Google del bibliotecario principal: ");
        gridForm.setText(7, 0, "Nombre del responsable: ");
        gridForm.setText(8, 0, "Tópicos de la librería: ");

        gridForm.setWidget(0, 1, tbName);
        gridForm.setWidget(1, 1, lbxLibraryType);
        gridForm.setWidget(2, 1, txaAddress);
        gridForm.setWidget(3, 1, tbCity);
        gridForm.setWidget(4, 1, lbDepartment);
        gridForm.setWidget(5, 1, tbPhoneNumber);
        gridForm.setWidget(6, 1, tbGoogleAccount);
        gridForm.setWidget(7, 1, tbHeadName);
        gridForm.setWidget(8, 1, taTopics);

        hpFormButtons.setSpacing(10);
        hpFormButtons.add(btnAddLibrary);
        hpFormButtons.add(btnClose);

        vpDialogPanel.add(gridForm);
        vpDialogPanel.add(lblInformation);
        vpDialogPanel.add(hpFormButtons);
        vpDialogPanel.setCellHorizontalAlignment(lblInformation, HasAlignment.ALIGN_CENTER);
        vpDialogPanel.setCellHorizontalAlignment(hpFormButtons, HasAlignment.ALIGN_CENTER);
        
        btnAddLibrary.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                lblInformation.setText("Guardando los datos...");
                widgetManager.enableWidgets(false);
                Library library = new Library();
                library.setName(tbName.getText());
                library.setLibraryType(lbxLibraryType.getItemText(lbxLibraryType.getSelectedIndex()));
                library.setAddress(txaAddress.getText());
                library.setCity(tbCity.getText());
                library.setDepartment(lbDepartment.getItemText(lbDepartment.getSelectedIndex()));
                library.setPhoneNumber(tbPhoneNumber.getText());
                library.setHeadGoogleAccount(tbGoogleAccount.getText());
                library.setHeadName(tbHeadName.getText());
                library.setTopics(taTopics.getText());

                persistentService.persistLibrary(library, new AsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        lblInformation.setText("Los datos se han guardado");
                        widgetManager.clearWidgets();
                        widgetManager.enableWidgets(true);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        lblInformation.setText(caught.getMessage());
                    }
                });
            }
        });

        btnClose.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dlgRegisterLibrary.hide(true);
            }
        });

        dlgRegisterLibrary.getCaption().setHTML("<b>Registrar librería</b>");
        dlgRegisterLibrary.setWidget(vpDialogPanel);
        
        if (libraryId != null) {
            widgetManager.enableWidgets(false);
            persistentService.getLibrary(libraryId, new AsyncCallback<Library>() {

                @Override
                public void onFailure(Throwable caught) {
                    lblInformation.setText(caught.getMessage());
                    widgetManager.enableWidgets(true);
                }

                @Override
                public void onSuccess(Library result) {
                    tbName.setText(result.getName());
                    for (int index = 0; index < lbxLibraryType.getItemCount(); ++index) {
                        if (lbxLibraryType.getItemText(index).equals(result.getLibraryType())) {
                            lbxLibraryType.setSelectedIndex(index);
                            break;
                        }
                    }
                    txaAddress.setText(result.getAddress());
                    tbCity.setText(result.getCity());
                    for (int index = 0; index < lbxLibraryType.getItemCount(); ++index) {
                        if (lbDepartment.getItemText(index).equals(result.getDepartment())) {
                            lbDepartment.setSelectedIndex(index);
                            break;
                        }
                    }
                    tbPhoneNumber.setText(result.getPhoneNumber());
                    tbGoogleAccount.setText(result.getHeadGoogleAccount());
                    tbHeadName.setText(result.getHeadName());
                    taTopics.setText(result.getTopics());
                    widgetManager.enableWidgets(true);
                }
            });
        }

        return dlgRegisterLibrary;
    }

    private void showLibraryUsersGrid(final Grid gridUsers, final ArrayList<LibraryUser> libraryUsers) {
        int numberOfResults = libraryUsers.size();
        gridUsers.resize(numberOfResults + 1, 6);
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
}
