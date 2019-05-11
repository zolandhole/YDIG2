package com.surampaksakosoy.ydig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.surampaksakosoy.ydig.handlers.DBHandler;
import com.surampaksakosoy.ydig.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String SUMBER_LOGIN, ID_LOGIN, NAMA, EMAIL;
    private DBHandler dbHandler;
    private TextView textView;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListener();
        checkLocalDB();

        textView = findViewById(R.id.textview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesLogout();
            }
        });
    }

    private void initListener() {
        dbHandler = new DBHandler(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
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
}
