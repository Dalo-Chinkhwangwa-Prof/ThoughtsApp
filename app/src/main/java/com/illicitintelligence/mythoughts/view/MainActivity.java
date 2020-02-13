package com.illicitintelligence.mythoughts.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.illicitintelligence.mythoughts.R;
import com.illicitintelligence.mythoughts.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginDelegator, HomeFragment.HomeController {

    private LoginFragment loginFragment = new LoginFragment();

    private HomeFragment homeFragment = new HomeFragment();

    private Handler handler = new Handler();

    @BindView(R.id.splash_view)
    public ConstraintLayout splashScreen;

    private Animation fadeAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        checkIfUserLoggedIn();
        splashScreen.setOnClickListener(view -> {
            splashScreen.setVisibility(View.GONE);
        });
        fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        splashScreen.startAnimation(fadeAnimation);
        handler.postDelayed(() -> {
            splashScreen.setVisibility(View.GONE);
        }, 2000);



    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void checkIfUserLoggedIn() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            Toast.makeText(this, "User is logged in :)", Toast.LENGTH_LONG).show();
            loadHomeFragment();
        } else { //User is logged in
            Toast.makeText(this, "User needs to be logged in", Toast.LENGTH_LONG).show();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame, loginFragment)
                    .commit();
        }
    }

    @Override
    public void signNewUpUser(User signUpUser) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                signUpUser.getEmailAddress(),
                signUpUser.getPassword()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                Toast.makeText(this, "Please check email for verification link", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(this, task.getException().getLocalizedMessage() + "", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void loginUser(User loginUser) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                loginUser.getEmailAddress(),
                loginUser.getPassword())
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(this, "Login complete", Toast.LENGTH_LONG).show();
                                    removeLoginFragment();
                                    loadHomeFragment();
                                } else {
                                    Toast.makeText(this, "Please verify email", Toast.LENGTH_LONG).show();
                                }
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
                .setCustomAnimations(R.anim.slide_right_to_left, R.anim.slide_left_to_right, R.anim.slide_right_to_left, R.anim.slide_left_to_right)
                .replace(R.id.main_frame, homeFragment)
                .commit();
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this,
                "Signed out!",
                Toast.LENGTH_SHORT).show();

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_right_to_left, R.anim.slide_left_to_right, R.anim.slide_right_to_left, R.anim.slide_left_to_right)
                .replace(R.id.main_frame, loginFragment)
                .commit();
    }
}
