package fr.insapp.insapp.models.credentials;

import fr.insapp.insapp.models.SessionToken;
import fr.insapp.insapp.models.User;

/**
 * Created by thomas on 11/07/2017.
 */

public class SessionCredentials {

    private LoginCredentials loginCredentials;
    private SessionToken sessionToken;
    private User user;

    public SessionCredentials(LoginCredentials loginCredentials, SessionToken sessionToken, User user) {
        this.loginCredentials = loginCredentials;
        this.sessionToken = sessionToken;
        this.user = user;
    }
}