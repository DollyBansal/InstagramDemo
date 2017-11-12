package com.dolly.instagramdemo.interfaces;

// MainActivity extends this interface. The reason for that is that onCodeReceived is
// called after Authentication is done (in class AuthenticationDialog). However, the class
// AuthenticationDialog is initialized in MainActivity itself. If we don't want this interface,
// then, we would need to pass a pointer of MainActivity to AuthenticationDialog, which is not
// as clean, because in that case, all of MainActivity methods will be exposed to
// AuthenticationDialog. Therefore, I prefer to have a clean interface with one method.
public interface AuthenticationListener {
    // Once the authentication code is received/generated, this method fetch the user photos.
    void onCodeReceived();
}
