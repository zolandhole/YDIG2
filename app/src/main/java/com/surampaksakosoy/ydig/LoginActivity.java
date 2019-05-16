package com.surampaksakosoy.ydig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.surampaksakosoy.ydig.handlers.DBHandler;
import com.surampaksakosoy.ydig.handlers.ServerHandler;
import com.surampaksakosoy.ydig.models.ModelUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    private SignInButton signInGoogle;
    private GoogleApiClient googleApiClient;
    private static final int REQ_GOOLE = 9001;

    private CallbackManager callbackManager;
    private LoginButton signiInFacebook;
    private Button button_facebook, button_google;

    private FrameLayout FrameLayoutFacebook, FrameLayoutGoogle;

    private DBHandler dbHandler;
    private String SUMBER_LOGIN;
    private String ID_LOGIN;
    private String NAMA;
    private String EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();
        anotherOnClick();
        loginWithFacebook();
    }

    private void anotherOnClick(){
        button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signiInFacebook.performClick();
            }
        });
        button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void loginWithFacebook() {

        signiInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request =GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    ID_LOGIN = object.getString("id");
                                    NAMA = object.getString("name");
                                    EMAIL = object.getString("email");
                                    SUMBER_LOGIN = "FACEBOOK";
                                    storeToLocalDB();
                                } catch (JSONException e) {
                                    Log.e(TAG, "onCompleted: "+ e);
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters= new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,link,location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Anda Membatalkan login dengan Facebook", Toast.LENGTH_SHORT).show();
                updateUI();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: "+ error);
                updateUI();
            }
        });
    }

    private void initListener() {
        signInGoogle.setOnClickListener(this);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();


        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.surampaksakosoy.ydig", PackageManager.GET_SIGNATURES
            );
            for (Signature signature: info.signatures){
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e){
            Log.e(TAG, "loginDenganFacebook: " + e);
        } catch (NoSuchAlgorithmException e){
            Log.e(TAG, "loginDenganFacebook: " + e);
        }

        callbackManager = CallbackManager.Factory.create();
        signiInFacebook.setReadPermissions(Arrays.asList("email","public_profile"));
    }

    private void initView() {
        signInGoogle = findViewById(R.id.signInGoogle);
        signiInFacebook = findViewById(R.id.signInFacebook);

        button_facebook = findViewById(R.id.button_facebook);
        button_google = findViewById(R.id.button_google);

        FrameLayoutFacebook = findViewById(R.id.FrameLayoutFacebook);
        FrameLayoutGoogle = findViewById(R.id.FrameLayoutGoogle);
    }

    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_GOOLE);
    }

    private void handleResult(GoogleSignInResult result){
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            ID_LOGIN = account.getId();
            NAMA = account.getDisplayName();
            EMAIL = account.getEmail();
            SUMBER_LOGIN = "GOOGLE";
            storeToLocalDB();
        } else {
            updateUI();
        }
    }

    private void updateUI(){
        FrameLayoutGoogle.setVisibility(View.VISIBLE);
        FrameLayoutFacebook.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_GOOLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        } else{
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signInGoogle) {
            signIn();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
    }

    private void storeToLocalDB(){
        dbHandler = new DBHandler(this);
        dbHandler.addUser(new ModelUser(1, SUMBER_LOGIN, ID_LOGIN, NAMA, EMAIL));
        dbHandler.close();
        checkLocalDB();
    }

    private void checkLocalDB(){
        ArrayList<HashMap<String, String>> userDB = dbHandler.getUser(1);
        for (Map<String, String> map : userDB){
            ID_LOGIN = map.get("id_login");
        }
        if (ID_LOGIN != null){
            storeToServer();
        } else {
            Toast.makeText(this, "Gagal Menyimpan ke Database", Toast.LENGTH_SHORT).show();
        }
    }

    public void keMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void storeToServer(){
        String status ="1";
        List<String> list = new ArrayList<>();
        list.add(SUMBER_LOGIN);
        list.add(ID_LOGIN);
        list.add(NAMA);
        list.add(EMAIL);
        list.add(status);
        ServerHandler serverHandler = new ServerHandler(this, "LOGIN_DATA");
        synchronized (this){
            serverHandler.sendData(list);
        }
    }
}
