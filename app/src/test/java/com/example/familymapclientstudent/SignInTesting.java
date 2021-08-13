package com.example.familymapclientstudent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.familymapclientstudent.Proxies.ServerProxy;

import org.junit.Test;

import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SignInTesting {
    @Test
    public void registerSuccess() {
        //Make sure database is clear
        ServerProxy serverProxy = new ServerProxy();
        RegisterRequest r = new RegisterRequest();
        r.setUsername("test");
        r.setPassword("password");
        r.setFirstName("test");
        r.setLastName("person");
        r.setEmail("testemail@gmail.com");
        r.setGender('m');

        RegisterResult result = serverProxy.register("localhost", 8080, r);
        assertNotNull(result);
        assertEquals("test", result.getUsername());
        assertTrue(result.getSuccess());
    }

    @Test
    public void registerFailure() {
        //Make sure database is clear
        ServerProxy serverProxy = new ServerProxy();
        RegisterRequest r = new RegisterRequest();
        r.setUsername("test");
        r.setPassword("password");
        r.setFirstName("test");
        r.setLastName("person");
        r.setEmail("testemail@gmail.com");
        r.setGender('m');

        RegisterResult result = serverProxy.register("localhost", 8080, r);
        assertNotNull(result);
        assertFalse(result.getSuccess());
    }

    @Test
    public void loginSuccess() {
        ServerProxy serverProxy = new ServerProxy();
        LoginRequest r = new LoginRequest();
        r.setUsername("test");
        r.setPassword("password");

        LoginResult result = serverProxy.login("localhost", 8080, r);
        assertNotNull(result);
        assertEquals("test", result.getUsername());
        assertTrue(result.getSuccess());
    }

    @Test
    public void loginFailed() {
        ServerProxy serverProxy = new ServerProxy();
        LoginRequest r = new LoginRequest();
        r.setUsername("test");
        r.setPassword("incorrectPassword");

        LoginResult result = serverProxy.login("localhost", 8080, r);
        assertNotNull(result);
        assertFalse(result.getSuccess());
    }
}
