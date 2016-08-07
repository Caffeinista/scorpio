package com.ithomaslin.caffeinista;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";
    private static final long PROFILE_IDENTIFIER = 0;
    private static final long HOME_IDENTIFIER = 2;
    private static final long HISTORY_IDENTIFIER = 2;
    private static final long LOGOUT_IDENTIFIER = 9;
    private static final long SETTINGS_IDENTIFIER = 10;

    private AccountHeader mDrawerHeader = null;
    private Drawer mDrawer = null;
    private IProfile mProfile;

    private String mUsername;
    private String mPhotoUrl;
    private Uri mPhotoUri;
    private String mUserEmail;
    private SharedPreferences mSharedPreferences;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;

    // Google API Client
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            mUserEmail = mFirebaseUser.getEmail();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                mPhotoUri = mFirebaseUser.getPhotoUrl();
            }

            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {
                    Glide.clear(imageView);
                }

                @Override
                public Drawable placeholder(Context ctx, String tag) {
                    if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                        return DrawerUIUtils.getPlaceHolder(ctx);
                    } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                        return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                    } else if ("customUrlItem".equals(tag)) {
                        return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                    }
                    return super.placeholder(ctx, tag);
                }
            });

            mProfile = new ProfileDrawerItem().withName(mUsername).withEmail(mUserEmail).withIcon(mPhotoUri).withIdentifier(PROFILE_IDENTIFIER);
            buildHeader(false, savedInstanceState);

            mDrawer = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withActionBarDrawerToggle(true)
                    .withAccountHeader(mDrawerHeader)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName("Home")
                                    .withIcon(FontAwesome.Icon.faw_home)
                                    .withIdentifier(HOME_IDENTIFIER),
                            new PrimaryDrawerItem().withName("History")
                                    .withIcon(FontAwesome.Icon.faw_history)
                                    .withIdentifier(HISTORY_IDENTIFIER)
                    )
                    .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                        @Override
                        public boolean onNavigationClickListener(View clickedView) {
                            return false;
                        }
                    })
                    .addStickyDrawerItems(
                            new SecondaryDrawerItem().withName("Settings")
                                    .withIcon(FontAwesome.Icon.faw_cog)
                                    .withIdentifier(SETTINGS_IDENTIFIER),
                            new SecondaryDrawerItem().withName("Log Out")
                                    .withIcon(FontAwesome.Icon.faw_sign_out)
                                    .withIdentifier(LOGOUT_IDENTIFIER)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position,
                                                   IDrawerItem drawerItem) {
                            if (drawerItem != null) {
                                switch ((int) drawerItem.getIdentifier()) {
                                    case (int) PROFILE_IDENTIFIER:
                                        Toast.makeText(MainActivity.this, mUsername, Toast.LENGTH_LONG).show();
                                        return false;
                                    case (int) HISTORY_IDENTIFIER:
                                        Toast.makeText(MainActivity.this, "History", Toast.LENGTH_LONG).show();
                                        return false;
                                    case (int) SETTINGS_IDENTIFIER:
                                        Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_LONG).show();
                                        return false;
                                    case (int) LOGOUT_IDENTIFIER:
                                        mFirebaseAuth.signOut();
                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                        mFirebaseUser = null;
                                        mUsername = ANONYMOUS;
                                        mUserEmail = null;
                                        mPhotoUrl = null;
                                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                        return false;
                                    default:
                                        return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .withShowDrawerOnFirstLaunch(true)
                    .build();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        
    }

    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        mDrawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(compact)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(mProfile)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = mDrawer.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = mDrawerHeader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_account:
                closeDrawer();
                return true;
            case R.id.nav_history:
                closeDrawer();
                return true;
            case R.id.nav_settings:
                closeDrawer();
                return true;
            case R.id.nav_signout:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeDrawer() {
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
