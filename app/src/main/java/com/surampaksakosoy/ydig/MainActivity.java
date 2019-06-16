package com.surampaksakosoy.ydig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.networkmanager.Tovuti;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.surampaksakosoy.ydig.fragment.FragmentHome;
import com.surampaksakosoy.ydig.fragment.FragmentPanduan;
import com.surampaksakosoy.ydig.fragment.FragmentStreaming;
import com.surampaksakosoy.ydig.handlers.DBHandler;
import com.surampaksakosoy.ydig.handlers.ServerHandler;
import com.surampaksakosoy.ydig.utils.NoInternetConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        FragmentHome.FragmentHomeListener,
        FragmentPanduan.FragmentPanduanListener,
        FragmentStreaming.FragmentStreamingListener {

    private static final String TAG = "MainActivity";
    private String SUMBER_LOGIN, ID_LOGIN, NAMA, EMAIL;
    private DBHandler dbHandler;
    private GoogleSignInClient googleSignInClient;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private CharSequence activeFragment;
    private boolean backPressExit = false;
    private NoInternetConnection internetConnection;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        checkInternetConnection(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String Test = intent.getStringExtra("streamingRadio");
        if (Test.equals("streamingRadio")){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                    new FragmentStreaming()).commit();
            navigationView.setCheckedItem(R.id.nav_streaming);
            FragmentStreaming fragmentStreaming = new FragmentStreaming();
            fragmentStreaming.updateData(Test);
        }
    }

    @Override
    protected void onStop() {
        Tovuti.from(this).stop();
        super.onStop();
    }

    private void checkInternetConnection(final Bundle savedInstanceState) {
        checkLocalDB();
        String dariNotification = getIntent().getStringExtra("streamingRadio");
        Log.e(TAG, "checkInternetConnection: " + dariNotification);
        if (internetConnection.isNetworkAvailable()) {
            if (dariNotification == null){
                keHomeFragment(savedInstanceState);
            } else {
                keStreamingFragment(savedInstanceState);
            }
        } else {
            if (ID_LOGIN == null){
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.nav_logout).setVisible(false);
                nav_Menu.findItem(R.id.nav_account).setVisible(false);
            }
            kePanduanFragment(savedInstanceState);
        }
    }

    private void noConnection() {
        Snackbar.make(findViewById(R.id.main_drawer_layout), "Tidak terhubung ke Internet", Snackbar.LENGTH_LONG).show();
    }

    private void kePanduanFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                    new FragmentPanduan()).commit();
            navigationView.setCheckedItem(R.id.nav_panduan);
        }
    }

    private void keHomeFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                    new FragmentHome()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void keStreamingFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                    new FragmentStreaming()).commit();
            navigationView.setCheckedItem(R.id.nav_streaming);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (backPressExit) {
                super.onBackPressed();
                finish();
            }

            this.backPressExit = true;
            Toast.makeText(this, "Tekan lagi untuk keluar", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressExit = false;
                }
            }, 2000);
        }
    }

    private void initView() {
        drawerLayout = findViewById(R.id.main_drawer_layout);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.main_navigation);
    }

    private void initListener() {
        internetConnection = new NoInternetConnection(this);
        dbHandler = new DBHandler(this);
        navigationView.setNavigationItemSelectedListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation, R.string.navigation);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void checkLocalDB() {
        ArrayList<HashMap<String, String>> userDB = dbHandler.getUser(1);
        for (Map<String, String> map : userDB) {
            SUMBER_LOGIN = map.get("sumber_login");
            ID_LOGIN = map.get("id_login");
            NAMA = map.get("nama");
            EMAIL = map.get("email");
        }

        if (ID_LOGIN == null && !internetConnection.isNetworkAvailable()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                    new FragmentPanduan()).commit();
        } else if (ID_LOGIN == null && internetConnection.isNetworkAvailable()) {
            prosesLogout();
        } else {
            loginSuccess();
        }
    }

    private void loginSuccess() {
        percantikNavigasiHeader();
    }

    private void percantikNavigasiHeader() {
        final View view = navigationView.getHeaderView(0);
        final CircleImageView imageView = view.findViewById(R.id.nav_image_view);
        TextView textViewNama = view.findViewById(R.id.nav_name);
        TextView textViewEmail = view.findViewById(R.id.nav_email);

        textViewNama.setText(NAMA);
        textViewEmail.setText(EMAIL);

        Uri photo = null;
        if (SUMBER_LOGIN.equals("FACEBOOK")) {
            int dimensionPixelSize = getResources()
                    .getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
            photo = ImageRequest.getProfilePictureUri(ID_LOGIN, dimensionPixelSize, dimensionPixelSize);
            if (photo != null) {
                Glide.with(this).load(photo).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.avatar);
            }
        } else if (SUMBER_LOGIN.equals("GOOGLE")) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                photo = account.getPhotoUrl();
                if (photo != null) {
                    Glide.with(this).load(photo).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.avatar);
                }
            }
        }
        savePhoto(photo);
    }

    private void savePhoto(Uri photo) {
        List<String> list = new ArrayList<>();
        list.add(ID_LOGIN);
        list.add(String.valueOf(photo));
        ServerHandler serverHandler = new ServerHandler(this, "MAIN_SAVE_PHOTO");
        synchronized (this) {
            serverHandler.sendData(list);
        }
    }

    private void prosesLogout() {
        dbHandler.deleteDB();

        if (SUMBER_LOGIN != null) {
            if (SUMBER_LOGIN.equals("GOOGLE")) {
                googleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                keLoginActivity();
                            }
                        });
            } else {
                LoginManager.getInstance().logOut();
                keLoginActivity();
            }
        } else {
            keLoginActivity();
        }
    }

    private void keLoginActivity() {
        ServerHandler serverHandler = new ServerHandler(this, "MAIN_LOGOUT");
        List<String> list = new ArrayList<>();
        list.add(ID_LOGIN);
        synchronized (this) {
            serverHandler.sendData(list);
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                if (internetConnection.isNetworkAvailable() && ID_LOGIN != null) {
                    if (!activeFragment.equals("home")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                                new FragmentHome()).commit();
                    }
                } else if (ID_LOGIN == null && internetConnection.isNetworkAvailable()) {
                    prosesLogout();
                } else {
                    noConnection();
                }
                break;
            case R.id.nav_panduan:
                if (!activeFragment.equals("panduan")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                            new FragmentPanduan()).commit();
                }
                break;
            case R.id.nav_news:
                Toast.makeText(this, "Info Kajian", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_streaming:
                if (!activeFragment.equals("streaming")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                            new FragmentStreaming()).commit();
                }
                break;
            case R.id.nav_chat:
                Toast.makeText(this, "Chatting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_account:
                Toast.makeText(this, "Setting Account", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                prosesLogout();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onInputHomeSent(String input) {
        activeFragment = input;
    }

    @Override
    public void onInputPanduanSent(String input) {
        activeFragment = input;
    }

    @Override
    public void onInputStreamingSent(CharSequence input) {
        activeFragment = input;
    }
}
