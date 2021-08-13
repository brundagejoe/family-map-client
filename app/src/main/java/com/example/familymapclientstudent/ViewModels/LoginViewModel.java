package com.example.familymapclientstudent.ViewModels;

import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private String serverHost;
    private int serverPort;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Character gender;

    private boolean genderSelected;
    private boolean registrationTextEntered;

    public LoginViewModel() {
        registrationTextEntered = false;
        genderSelected = false;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public boolean isRegistrationTextEntered() {
        return registrationTextEntered;
    }

    public void setRegistrationTextEntered(boolean registrationTextEntered) {
        this.registrationTextEntered = registrationTextEntered;
    }

    public boolean isGenderSelected() {
        return genderSelected;
    }

    public void setGenderSelected(boolean genderSelected) {
        this.genderSelected = genderSelected;
    }
}
