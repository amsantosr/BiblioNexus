package com.tais.biblionexus.client.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WaitDialog extends DialogBox {
    final VerticalPanel vpContent = new VerticalPanel();
    final Label lblMessage = new Label();
    final Button btnClose = new Button("Cerrar");
    
    public WaitDialog() {
        super(true, true);
        final WaitDialog thisDialog = this;
        vpContent.add(lblMessage);
        vpContent.add(btnClose);
        thisDialog.add(vpContent);
        
        btnClose.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                thisDialog.hide(true);
            }
        });
    }
    
    public void setMessage(String message) {
        lblMessage.setText(message);
    }
}
