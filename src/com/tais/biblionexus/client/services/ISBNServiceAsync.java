package com.tais.biblionexus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISBNServiceAsync {

    void getBookXMLInfo(String isbn, AsyncCallback<String> callback);

}
