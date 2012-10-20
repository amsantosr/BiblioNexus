package com.tais.biblionexus.client.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tais.biblionexus.client.entities.LibraryUser;
import com.tais.biblionexus.client.services.PersistentServiceAsync;

/**
 * Utility class to create shared widgets between modules
 * 
 * @author Abraham Max Santos Ramos <amsantosr@gmail.com>
 *
 */
public class Common {

    public static ListBox createListBoxDepartment() {
        final ListBox lbDepartment = new ListBox();
        lbDepartment.addItem("Amazonas");
        lbDepartment.addItem("Ancash");
        lbDepartment.addItem("Apurímac");
        lbDepartment.addItem("Arequipa");
        lbDepartment.addItem("Ayacucho");
        lbDepartment.addItem("Cajamarca");
        lbDepartment.addItem("Cuzco");
        lbDepartment.addItem("Huancavelica");
        lbDepartment.addItem("Huánuco");
        lbDepartment.addItem("Ica");
        lbDepartment.addItem("Junín");
        lbDepartment.addItem("La Libertad");
        lbDepartment.addItem("Lambayeque");
        lbDepartment.addItem("Lima");
        lbDepartment.addItem("Loreto");
        lbDepartment.addItem("Madre de Dios");
        lbDepartment.addItem("Moquegua");
        lbDepartment.addItem("Pasco");
        lbDepartment.addItem("Piura");
        lbDepartment.addItem("Puno");
        lbDepartment.addItem("San Martín");
        lbDepartment.addItem("Tacna");
        lbDepartment.addItem("Tumbes");
        lbDepartment.addItem("Ucayali");
        return lbDepartment;
    }
    
    public static DialogBox createUserDialog(final PersistentServiceAsync persistentService) {
        final DialogBox dlgRegisterLibrarian = new DialogBox();
        final HorizontalPanel hpButtonsPanel = new HorizontalPanel();
        final VerticalPanel vpDialogPanel = new VerticalPanel();
        final Label lblInformation = new Label();
        final Button btnSave = new Button("Guardar");
        final Button btnCancel = new Button("Cerrar");
        final Grid gridForm = new Grid(9, 2);
        final TextBox tbFirstName = new TextBox();
        final TextBox tbFatherLastName = new TextBox();
        final TextBox tbMotherLastName = new TextBox();
        final TextBox tbDNI = new TextBox();
        final TextBox tbPhoneNumber = new TextBox();
        final TextBox tbAddress = new TextBox();
        final TextBox tbCity = new TextBox();
        final ListBox lbDepartment = createListBoxDepartment();
        final TextBox tbGoogleAccount = new TextBox();
        final Button btnVerifyAccount = new Button("Buscar");
        final HorizontalPanel hpVerifyAccount = new HorizontalPanel();
        
        class EnableWidgets {
            public void enable(boolean flag) {
                tbGoogleAccount.setEnabled(flag);
                tbFirstName.setEnabled(flag);
                tbFatherLastName.setEnabled(flag);
                tbMotherLastName.setEnabled(flag);
                tbDNI.setEnabled(flag);
                tbPhoneNumber.setEnabled(flag);
                tbAddress.setEnabled(flag);
                tbCity.setEnabled(flag);
                lbDepartment.setEnabled(flag);
            }
        }
        
        final EnableWidgets enableWidgets = new EnableWidgets();

        gridForm.setText(0, 0, "Cuenta de Google (e-mail): ");
        gridForm.setText(1, 0, "Nombres: ");
        gridForm.setText(2, 0, "Apellido paterno: ");
        gridForm.setText(3, 0, "Apellido materno: ");
        gridForm.setText(4, 0, "DNI: ");
        gridForm.setText(5, 0, "Teléfono: ");
        gridForm.setText(6, 0, "Dirección: ");
        gridForm.setText(7, 0, "Ciudad: ");
        gridForm.setText(8, 0, "Departamento: ");

        gridForm.setWidget(0, 1, hpVerifyAccount);
        gridForm.setWidget(1, 1, tbFirstName);
        gridForm.setWidget(2, 1, tbFatherLastName);
        gridForm.setWidget(3, 1, tbMotherLastName);
        gridForm.setWidget(4, 1, tbDNI);
        gridForm.setWidget(5, 1, tbPhoneNumber);
        gridForm.setWidget(6, 1, tbAddress);
        gridForm.setWidget(7, 1, tbCity);
        gridForm.setWidget(8, 1, lbDepartment);

        hpVerifyAccount.add(tbGoogleAccount);
        hpVerifyAccount.add(btnVerifyAccount);
        hpButtonsPanel.setSpacing(10);
        hpButtonsPanel.add(btnSave);
        hpButtonsPanel.add(btnCancel);
        vpDialogPanel.add(gridForm);
        vpDialogPanel.add(lblInformation);
        vpDialogPanel.add(hpButtonsPanel);
        vpDialogPanel.setCellHorizontalAlignment(lblInformation, HasAlignment.ALIGN_CENTER);
        vpDialogPanel.setCellHorizontalAlignment(hpButtonsPanel, HasAlignment.ALIGN_CENTER);
        dlgRegisterLibrarian.getCaption().setHTML("<b>Agregar Usuario</b>");
        dlgRegisterLibrarian.setWidget(vpDialogPanel);
        
        btnVerifyAccount.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                btnVerifyAccount.setText("Verificando...");
                btnVerifyAccount.setEnabled(false);
                tbGoogleAccount.setEnabled(false);
                persistentService.getLibraryUser(tbGoogleAccount.getText(), new AsyncCallback<LibraryUser>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        tbGoogleAccount.setEnabled(true);
                        btnVerifyAccount.setEnabled(true);
                        lblInformation.setText(caught.getMessage());
                    }

                    @Override
                    public void onSuccess(LibraryUser result) {
                        btnVerifyAccount.setEnabled(true);
                        btnVerifyAccount.setText("Verificar");
                        tbGoogleAccount.setEnabled(true);
                        if (result == null) {
                            lblInformation.setText("Cuenta de Google disponible para registro");
                        } else {
                            lblInformation.setText("La cuenta de Google ya ha sido registrada");
                        }
                    }
                });
            }
        });

        btnSave.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                lblInformation.setText("Guardando los datos...");
                enableWidgets.enable(false);
                LibraryUser libraryUser = new LibraryUser();
                libraryUser.setName(tbFirstName.getText());
                libraryUser.setFatherLastName(tbFatherLastName.getText());
                libraryUser.setMotherLastName(tbMotherLastName.getText());
                libraryUser.setDni(tbDNI.getText());
                libraryUser.setPhoneNumber(tbPhoneNumber.getText());
                libraryUser.setAddress(tbAddress.getText());
                libraryUser.setCity(tbCity.getText());
                libraryUser.setDepartment(lbDepartment.getItemText(lbDepartment.getSelectedIndex()));
                libraryUser.setGoogleAccount(tbGoogleAccount.getText());

                persistentService.persistLibraryUser(libraryUser, new AsyncCallback<Long>() {

                    @Override
                    public void onSuccess(Long result) {
                        lblInformation.setText("Los datos se han guardado.");
                        enableWidgets.enable(true);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        lblInformation.setText(caught.getMessage());
                        enableWidgets.enable(true);
                    }
                });
            }
        });

        btnCancel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dlgRegisterLibrarian.hide(true);
            }
        });

        return dlgRegisterLibrarian;
    }
    
}
