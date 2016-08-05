package com.ithomaslin.caffeinista;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.stephentuso.welcome.WelcomeScreenHelper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignInActivity extends AppCompatActivity {

    WelcomeScreenHelper welcomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        welcomeScreen = new WelcomeScreenHelper(this, CustomWelcomeActivity.class);
        welcomeScreen.show(savedInstanceState);

        Button restartWelcomeBtn = (Button) findViewById(R.id.restartWelcomeBtn);
        restartWelcomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcomeScreen.forceShow();
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }
}
