package com.illicitintelligence.mythoughts.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.illicitintelligence.mythoughts.R;
import com.illicitintelligence.mythoughts.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {

    private SignUpFragment signUpFragment = new SignUpFragment();

    @BindView(R.id.email_address_edittext)
    public EditText emailEditText;

    @BindView(R.id.password_edittext)
    public EditText passwordEditText;

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

    @OnClick(R.id.signup_textview)
    public void openSignUp(View view) {
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_right_to_left, R.anim.slide_left_to_right, R.anim.slide_right_to_left, R.anim.slide_left_to_right)
                .replace(R.id.signup_frame, signUpFragment)
                .addToBackStack(signUpFragment.getTag())
                .commit();

    }

    private boolean checkLogInUserInput() {
        if (emailEditText.getText().toString().trim().length() == 0
                || passwordEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "Email and Password cannot be empty", Toast.LENGTH_LONG).show();
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
