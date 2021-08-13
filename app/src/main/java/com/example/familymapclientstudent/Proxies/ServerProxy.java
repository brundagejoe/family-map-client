package com.example.familymapclientstudent.Proxies;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
//import com.google.gson.Gson;

import Model.AuthToken;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.EventResult;
import results.LoginResult;
import results.PersonResult;
import results.RegisterResult;

public class ServerProxy {

    public String Post(String urlString, String requestString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.addRequestProperty("Accept", "text/html");

        connection.connect();

        try(OutputStream requestBody = connection.getOutputStream()) {
            writeString(requestString, requestBody);
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String responseBodyString = readString(responseBody);
            return responseBodyString;

        }
        else {
            //Server returned http error
            return null;
        }
    }

    public String Get(String urlString, String authToken) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");

        connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", authToken);

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String responseBodyString = readString(responseBody);
            return responseBodyString;
        } else {
            //Server returned an http error
            return null;
        }
    }

    public RegisterResult register(String serverHost, int serverPort, RegisterRequest r) {
        DataCache.getInstance().clearData();//will clear in the logout, so this should be deleted later on

        String urlString = "http://" + serverHost + ":" + serverPort;
        RegisterResult registerResult;
        String requestString = getJSONFromObject(r);
        try {
            String responseString = Post(urlString + "/user/register", requestString);
            if (responseString != null) {
                registerResult = getObjectFromJSON(responseString, RegisterResult.class);
                storeAuthToken(registerResult.getAuthtoken(), registerResult.getUsername());
                addData(urlString, registerResult.getAuthtoken(), registerResult.getPersonID());
                return registerResult;
            }
            else {
                registerResult = new RegisterResult();
                registerResult.setSuccess(false);
                return registerResult;
            }
        } catch (IOException e) {
            e.printStackTrace();
            registerResult = new RegisterResult();
            registerResult.setSuccess(false);
            return registerResult;
        }
    }

    public LoginResult login(String serverHost, int serverPort, LoginRequest r) {
        DataCache.getInstance().clearData();//will be cleared in the logout, so this should be deleted later on

        String urlString = "http://" + serverHost + ":" + serverPort;
        LoginResult loginResult;
        String requestString = getJSONFromObject(r);
        try {
            String responseString = Post(urlString + "/user/login", requestString);
            if (responseString != null) {
                loginResult = getObjectFromJSON(responseString, LoginResult.class);
                storeAuthToken(loginResult.getAuthtoken(), loginResult.getUsername());
                addData(urlString, loginResult.getAuthtoken(), loginResult.getPersonID());
                return loginResult;
            }
            else {
                loginResult = new LoginResult();
                loginResult.setSuccess(false);
                return loginResult;
            }
        } catch (IOException e) {
            e.printStackTrace();
            loginResult = new LoginResult();
            loginResult.setSuccess(false);
            return loginResult;
        }
    }

    private void addPeople(String urlString, String authToken, String personIDofUser) throws IOException {
        String personResultString = Get(urlString + "/person", authToken);
        PersonResult personResult = getObjectFromJSON(personResultString, PersonResult.class);

        if (personResult.getSuccess()) {
            DataCache dataCache = DataCache.getInstance();

            dataCache.addPeople(personResult.getData(), personIDofUser);
        }
    }

    private void addEvents(String urlString, String authToken) throws IOException {
        String eventResultString = Get(urlString + "/event", authToken);
        EventResult eventResult = getObjectFromJSON(eventResultString, EventResult.class);

        if (eventResult.getSuccess()) {
            DataCache dataCache = DataCache.getInstance();
            dataCache.addEvents(eventResult.getData());
        }
    }

    private void addData(String urlString, String authToken, String personIDofUser) throws IOException {
        addPeople(urlString, authToken, personIDofUser);
        addEvents(urlString, authToken);
    }

    private void storeAuthToken(String authToken, String username) {
        DataCache dataCache = DataCache.getInstance();

        AuthToken AuthtokenModel = new AuthToken();
        AuthtokenModel.setAuthtoken(authToken);
        AuthtokenModel.setUsername(username);
        dataCache.setAuthToken(AuthtokenModel);
    }

    protected String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    protected void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }

    protected static <T> T getObjectFromJSON(String data, Class<T> tClass) {
        Gson gson = new Gson();
        T t = gson.fromJson(data, tClass);
        return t;
    }

    protected String getJSONFromObject(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
}
