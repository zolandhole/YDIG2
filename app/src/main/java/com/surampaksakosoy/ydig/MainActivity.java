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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.surampaksakosoy.ydig.fragment.FragmentHome;
import com.surampaksakosoy.ydig.handlers.DBHandler;
import com.surampaksakosoy.ydig.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String SUMBER_LOGIN, ID_LOGIN, NAMA, EMAIL;
    private DBHandler dbHandler;
    private GoogleSignInClient googleSignInClient;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
        checkLocalDB();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation, R.string.navigation);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                    new FragmentHome()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private void initView(){
        drawerLayout = findViewById(R.id.main_drawer_layout);
        toolbar =findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.main_navigation);
    }

    private void initListener() {
        dbHandler = new DBHandler(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkLocalDB(){
        ArrayList<HashMap<String, String>> userDB = dbHandler.getUser(1);
        for (Map<String, String> map :  userDB){
            SUMBER_LOGIN = map.get("sumber_login");
            ID_LOGIN = map.get("id_login");
            NAMA = map.get("nama");
            EMAIL = map.get("email");
        }

        if (ID_LOGIN == null){
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
        TextView textViewNama  = view.findViewById(R.id.nav_name);
        TextView textViewEmail = view.findViewById(R.id.nav_email);

        textViewNama.setText(NAMA);
        textViewEmail.setText(EMAIL);

        Uri photo = null;
        if (SUMBER_LOGIN.equals("FACEBOOK")){
            int dimensionPixelSize = getResources()
                    .getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
            photo = ImageRequest.getProfilePictureUri(ID_LOGIN, dimensionPixelSize , dimensionPixelSize );
            Glide.with(this).load(photo).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
        } else if (SUMBER_LOGIN.equals("GOOGLE")){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null){
                photo = account.getPhotoUrl();
                if (photo!=null){
                    Glide.with(this).load(photo).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
                }
            }
        }
        Log.e(TAG, "percantikNavigasiHeader: " + photo);
        savePhoto(photo);
    }

    private void savePhoto(Uri photo) {
        List<String> list = new ArrayList<>();
        list.add(ID_LOGIN);
        list.add(String.valueOf(photo));
        ServerHandler serverHandler = new ServerHandler(this, "MAIN_SAVE_PHOTO");
        synchronized (this){
            serverHandler.sendData(list);
        }
    }

    private void prosesLogout() {
        dbHandler.deleteDB();

        if (SUMBER_LOGIN != null){
            if (SUMBER_LOGIN.equals("GOOGLE")){
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
        synchronized (this){
            serverHandler.sendData(list);
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                        new FragmentHome()).commit();
                break;
            case R.id.nav_news:
                Toast.makeText(this, "Info Kajian", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_streaming:
                Toast.makeText(this, "Radio Streaming", Toast.LENGTH_SHORT).show();
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
        return false;
    }
}
