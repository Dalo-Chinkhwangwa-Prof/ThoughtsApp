package com.illicitintelligence.mythoughts.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.illicitintelligence.mythoughts.R;
import com.illicitintelligence.mythoughts.model.User;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginDelegator {


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private LoginFragment loginFragment = new LoginFragment();

    private HomeFragment homeFragment = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        checkIfUserLoggedIn();



    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void checkIfUserLoggedIn() {

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null ) { //user not logged in
            Toast.makeText(this, "User needs to be logged in", Toast.LENGTH_LONG).show();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame, loginFragment)
                    .commit();

        } else { //User is logged in
            Toast.makeText(this, "User is logged in :)", Toast.LENGTH_LONG).show();
            loadHomeFragment();
        }

    }

    @Override
    public void signNewUpUser(User signUpUser) {
        firebaseAuth.createUserWithEmailAndPassword(
                signUpUser.getEmailAddress(),
                signUpUser.getPassword()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User created successfully", Toast.LENGTH_LONG).show();
                loadHomeFragment();
                removeLoginFragment();
            } else
                Toast.makeText(this, task.getException().getLocalizedMessage() + "", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void loginUser(User loginUser) {
        firebaseAuth.signInWithEmailAndPassword(
                loginUser.getEmailAddress(),
                loginUser.getPassword())
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Login complete", Toast.LENGTH_LONG).show();
                                removeLoginFragment();
                                loadHomeFragment();
                            } else
                                Toast.makeText(this, task.getException().getLocalizedMessage() + "",
                                        Toast.LENGTH_LONG).show();
                        }
                );

    }

    private void removeLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(loginFragment)
                .commit();
    }

    private void loadHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, homeFragment)
                .commit();
    }
}
