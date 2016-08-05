package com.ithomaslin.caffeinista;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stephentuso.welcome.WelcomeScreenBuilder;
import com.stephentuso.welcome.ui.WelcomeActivity;
import com.stephentuso.welcome.util.WelcomeScreenConfiguration;

public class CustomWelcomeActivity extends WelcomeActivity {

    @Override
    protected WelcomeScreenConfiguration configuration() {
        return new WelcomeScreenBuilder(this)
                .theme(R.style.CustomWelcomeScreenTheme)
                .parallaxPage(R.layout.welcome_main, getString(R.string.welcome_title_page),
                        getString(R.string.welcome_title_description), R.color.primary, 0.2f, 2f)
                .parallaxPage(R.layout.welcome_screen_1, getString(R.string.welcome_one_title),
                        getString(R.string.welcome_one_description), R.color.primary, 0.2f, 2f)
                .parallaxPage(R.layout.welcome_screen_2, getString(R.string.welcome_two_title),
                        getString(R.string.welcome_two_description), R.color.primary, 0.2f, 2f)
                .parallaxPage(R.layout.welcome_screen_3, getString(R.string.welcome_three_title),
                        getString(R.string.welcome_three_description), R.color.primary, 0.2f, 2f)
                .useCustomDoneButton(true)
                .backButtonSkips(false)
                .swipeToDismiss(true)
                .build();
    }
}
