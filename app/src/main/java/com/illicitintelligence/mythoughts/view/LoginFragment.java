package com.illicitintelligence.mythoughts.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.illicitintelligence.mythoughts.R;
import com.illicitintelligence.mythoughts.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {


    @BindView(R.id.email_address_edittext)
    public EditText emailEditText;

    @BindView(R.id.password_edittext)
    public EditText passwordEditText;

    @BindView(R.id.signup_email_address_edittext)
    public EditText signupEmailEditText;

    @BindView(R.id.signup_password_edittext)
    public EditText signupPasswordEditText;

    @BindView(R.id.verify_signup_password_edittext)
    public EditText verifyPasswordEditText;

    @BindView(R.id.signup_layout)
    public ConstraintLayout signUpLayout;

    private LoginDelegator loginDelegator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View loginView = inflater.inflate(R.layout.login_fragment_layout, container, false);
        return loginView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.login_button)
    public void loginUser(View view) {
        if (checkLogInUserInput()) {
            String emailAddress = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            loginDelegator.loginUser(new User(emailAddress, password));
        }
    }


    @OnClick(R.id.signup_button)
    public void signUpUser(View view) {
        if (checkSignUpUserInput()) {
            String emailAddress = signupEmailEditText.getText().toString().trim();
            String password = signupPasswordEditText.getText().toString().trim();
            User newUser = new User(emailAddress, password);
            loginDelegator.signNewUpUser(newUser);
        }
    }

    @OnClick(R.id.signup_textview)
    public void openSignUp(View view) {
        signUpLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.signup_back_imageview)
    public void onCloseSignUp(View view) {
        signUpLayout.setVisibility(View.GONE);
    }

    private boolean checkLogInUserInput() {
        if (emailEditText.getText().toString().trim().length() == 0
                || passwordEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "Email and Password cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

    private boolean checkSignUpUserInput() {
        if (signupEmailEditText.getText().toString().trim().length() == 0 ||
                signupPasswordEditText.getText().toString().trim().length() == 0 ||
                verifyPasswordEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (!signupPasswordEditText.getText().toString().trim().equals(verifyPasswordEditText.getText().toString().trim())) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        } else if (signupPasswordEditText.getText().toString().length() < 8) {
            Toast.makeText(getContext(), "Password characters less than 8", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loginDelegator = (MainActivity) context;
    }

    interface LoginDelegator {
        void signNewUpUser(User signUpUser);

        void loginUser(User loginUser);
    }


}
