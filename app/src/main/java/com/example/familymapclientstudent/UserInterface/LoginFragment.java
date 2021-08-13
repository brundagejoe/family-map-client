package com.example.familymapclientstudent.UserInterface;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.familymapclientstudent.Proxies.DataCache;
import com.example.familymapclientstudent.ViewModels.LoginViewModel;
import com.example.familymapclientstudent.R;
import com.example.familymapclientstudent.Proxies.ServerProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;


public class LoginFragment extends Fragment {

    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    private static final String LOGIN_TOAST_KEY = "LoginToastKey";
    private static final String REGISTRATION_TOAST_KEY = "RegistrationToastKey";

    private EditText serverHostField;
    private EditText serverPortField;
    private EditText userNameField;
    private EditText passwordField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private RadioGroup genderButton;

    private Button mSignInButton;
    private Button mRegisterButton;

    private LoginViewModel getViewModel() {
        return new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        super.onCreate(savedInstanceState);

        DataCache.getInstance().clearData();

        serverHostField = view.findViewById(R.id.serverHostField);
        serverPortField = view.findViewById(R.id.serverPortField);
        userNameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);
        firstNameField = view.findViewById(R.id.firstNameField);
        lastNameField = view.findViewById(R.id.lastNameField);
        emailField = view.findViewById(R.id.emailAddressField);
        genderButton = view.findViewById(R.id.genderButtonGroup);

        mSignInButton = view.findViewById(R.id.signInButton);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRequestVariables();
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername(getViewModel().getUserName());
                loginRequest.setPassword(getViewModel().getPassword());

                Handler loginHandler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        Bundle bundle = msg.getData();
                        String loginToast = bundle.getString(LOGIN_TOAST_KEY, "Login Failed.");
                        Toast.makeText(getActivity(),
                                loginToast,
                                Toast.LENGTH_SHORT).show();
                    }
                };

                LoginTask task = new LoginTask(loginHandler, getViewModel().getServerHost(),
                                                getViewModel().getServerPort(),
                                                loginRequest, listener);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);
            }
        });
        mRegisterButton = view.findViewById(R.id.registerButton);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRequestVariables();
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername(getViewModel().getUserName());
                registerRequest.setPassword(getViewModel().getPassword());
                registerRequest.setFirstName(getViewModel().getFirstName());
                registerRequest.setLastName(getViewModel().getLastName());
                registerRequest.setEmail(getViewModel().getEmail());
                registerRequest.setGender(getViewModel().getGender());

                Handler registrationHandler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        Bundle bundle = msg.getData();
                        String registrationToast = bundle.getString(REGISTRATION_TOAST_KEY, "Login Failed.");
                        Toast.makeText(getActivity(),
                                registrationToast,
                                Toast.LENGTH_SHORT).show();
                    }
                };

                RegisterTask task = new RegisterTask(registrationHandler, getViewModel().getServerHost(),
                                                        getViewModel().getServerPort(),
                                                        registerRequest, listener);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);

            }
        });

        serverHostField.addTextChangedListener(loginTextWatcher);
        serverPortField.addTextChangedListener(loginTextWatcher);
        userNameField.addTextChangedListener(loginTextWatcher);
        passwordField.addTextChangedListener(loginTextWatcher);
        firstNameField.addTextChangedListener(loginTextWatcher);
        lastNameField.addTextChangedListener(loginTextWatcher);
        emailField.addTextChangedListener(loginTextWatcher);
        genderButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getViewModel().setGenderSelected(true);
                mRegisterButton.setEnabled(getViewModel().isRegistrationTextEntered() &&
                                            getViewModel().isGenderSelected());
                switch(checkedId) {
                    case R.id.maleButton:
                        getViewModel().setGender('m');
                        break;
                    case R.id.femaleButton:
                        getViewModel().setGender('f');
                        break;
                }
            }
        });

        return view;
    }

    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String serverHost = serverHostField.getText().toString().trim();
            String serverPort = serverPortField.getText().toString().trim();
            String userName = userNameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();

            mSignInButton.setEnabled(!serverHost.isEmpty() &&
                    !serverPort.isEmpty() &&
                    !userName.isEmpty() &&
                    !password.isEmpty());

            getViewModel().setRegistrationTextEntered(!serverHost.isEmpty() &&
                    !serverPort.isEmpty() &&
                    !userName.isEmpty() &&
                    !password.isEmpty() &&
                    !firstName.isEmpty() &&
                    !lastName.isEmpty() &&
                    !email.isEmpty());

            mRegisterButton.setEnabled(getViewModel().isRegistrationTextEntered() &&
                    getViewModel().isGenderSelected());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void SetRequestVariables() {
        getViewModel().setServerHost(serverHostField.getText().toString().trim());
        getViewModel().setServerPort(Integer.parseInt(serverPortField.getText().toString().trim()));
        getViewModel().setUserName(userNameField.getText().toString().trim());
        getViewModel().setPassword(passwordField.getText().toString().trim());
        getViewModel().setFirstName(firstNameField.getText().toString().trim());
        getViewModel().setLastName(lastNameField.getText().toString().trim());
        getViewModel().setEmail(emailField.getText().toString().trim());
    }

    private static class LoginTask implements Runnable {
        private final Handler messageHandler;
        private final String serverHost;
        private final int serverPort;
        private final LoginRequest loginRequest;
        private Listener listener;

        public LoginTask(Handler messageHandler, String serverHost, int serverPort,
                         LoginRequest loginRequest, Listener listener) {
            this.messageHandler = messageHandler;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.loginRequest = loginRequest;
            this.listener = listener;
        }

        @Override
        public void run() {
            DataCache dataCache = DataCache.getInstance();
            ServerProxy server = new ServerProxy();
            LoginResult result = server.login(serverHost, serverPort, loginRequest);

            if (result.getSuccess()) {
                if (listener != null) {
                    listener.notifyDone();
                }
            }
            else {
                sendMessage("Login Failed.");
            }

        }

        private void sendMessage(String toast) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString(LOGIN_TOAST_KEY, toast);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private static class RegisterTask implements Runnable {
        private final Handler messageHandler;
        private final String serverHost;
        private final int serverPort;
        private final RegisterRequest registerRequest;
        private Listener listener;

        public RegisterTask(Handler messageHandler, String serverHost, int serverPort,
                            RegisterRequest registerRequest, Listener listener) {
            this.messageHandler = messageHandler;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.registerRequest = registerRequest;
            this.listener = listener;
        }

        @Override
        public void run() {
            DataCache dataCache = DataCache.getInstance();
            ServerProxy server = new ServerProxy();
            RegisterResult result = server.register(serverHost, serverPort, registerRequest);

            if (result.getSuccess()) {
                if (listener != null) {
                    listener.notifyDone();
                }
            }
            else {
                sendMessage("Registration Failed.");
            }

        }

        private void sendMessage(String toast) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString(REGISTRATION_TOAST_KEY, toast);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
}