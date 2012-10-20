package com.tais.biblionexus.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.tais.biblionexus.client.services.ISBNService;
import com.tais.biblionexus.shared.FieldVerifier;

@SuppressWarnings("serial")
public class ISBNServiceImpl extends RemoteServiceServlet implements ISBNService {
    private static final String accessKey = "FAE6FKPN";
    private static final String requestFormatString = "http://isbndb.com/api/books.xml?access_key=%s&results=details,texts&index1=isbn&value1=%s"; 

    @Override
    public String getBookXMLInfo(String isbn) {
        if (!FieldVerifier.isValidISBN(isbn)) {
            return null;
        }
        String requestString = String.format(requestFormatString, accessKey, isbn);
        try {
            URL requestURL = new URL(requestString);
            URLConnection connection = requestURL.openConnection();
            connection.setConnectTimeout(0);
            connection.setReadTimeout(0);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append('\n');
            }
            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
