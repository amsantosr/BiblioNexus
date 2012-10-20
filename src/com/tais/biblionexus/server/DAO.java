package com.tais.biblionexus.server;

import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.tais.biblionexus.client.entities.Borrowing;
import com.tais.biblionexus.client.entities.Exemplar;
import com.tais.biblionexus.client.entities.Library;
import com.tais.biblionexus.client.entities.LibraryItem;
import com.tais.biblionexus.client.entities.LibraryUser;
import com.tais.biblionexus.client.entities.Registration;

public class DAO extends DAOBase {
    private static DAO singleton = new DAO();
    private static final Compass compass;

    static {
        ObjectifyService.register(LibraryItem.class);
        ObjectifyService.register(Library.class);
        ObjectifyService.register(LibraryUser.class);
        ObjectifyService.register(Registration.class);
        ObjectifyService.register(Exemplar.class);
        ObjectifyService.register(Borrowing.class);

        compass = new CompassConfiguration().configure("/com/tais/biblionexus/server/compass.cfg.xml")
                .addClass(LibraryItem.class).buildCompass();
    }

    public static DAO getInstance() {
        return singleton;
    }

    public static Compass getCompassInstance() {
        return compass;
    }
}
