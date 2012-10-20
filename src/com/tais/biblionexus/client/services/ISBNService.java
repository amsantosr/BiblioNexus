package com.tais.biblionexus.client.services;

import com.google.gwt.user.client.rpc.RemoteService;

public interface ISBNService extends RemoteService {

    public String getBookXMLInfo(String isbn);
}
