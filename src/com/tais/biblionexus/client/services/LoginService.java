package com.tais.biblionexus.client.services;

import com.google.gwt.user.client.rpc.RemoteService;

public interface LoginService extends RemoteService {
    public LoginInfo login(String requestUri);
}
