package com.illicitintelligence.mythoughts.view;

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

public class SignUpFragment extends Fragment {

    @BindView(R.id.signup_email_address_edittext)
    public EditText signupEmailEditText;

    @BindView(R.id.signup_password_edittext)
    public EditText signupPasswordEditText;

    @BindView(R.id.verify_signup_password_edittext)
    public EditText verifyPasswordEditText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.signup_button)
    public void signUpUser(View view) {
        if (checkSignUpUserInput()) {
            String emailAddress = signupEmailEditText.getText().toString().trim();
            String password = signupPasswordEditText.getText().toString().trim();
            User newUser = new User(emailAddress, password);
            ((LoginFragment.LoginDelegator) getActivity()).signNewUpUser(newUser);
            getParentFragment().getChildFragmentManager().popBackStack();
        }
    }

    @OnClick(R.id.signup_back_imageview)
    public void onCloseSignUp(View view) {
        getParentFragment().getChildFragmentManager().popBackStack();
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
}
