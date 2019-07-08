package com.surampaksakosoy.ydig.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.Services.StreamingService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.surampaksakosoy.ydig.R.color.merahmarun;

public class FragmentStreaming extends Fragment{

    private static final String TAG = "FragmentStreaming";
    private FragmentStreamingListener listener;
    private Button btnMainkan;
    private BroadcastReceiver broadcastReceiver;
    private LinearLayout linearLayoutServerDown;
    private RelativeLayout relativeLayoutServerUp;
    private NavigationView navigationView;
    private ProgressBar progressBar;
    private EditText editTextPesan;


    public FragmentStreaming() {
    }

    public interface FragmentStreamingListener {
        void onInputStreamingSent(CharSequence input);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentStreaming.FragmentStreamingListener) {
            listener = (FragmentStreamingListener) context;
        } else {
            throw new RuntimeException(context.toString() + " harus mengimplementasi FragmentStreamingListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_streaming, container, false);
        CharSequence test = "streaming";
        listener.onInputStreamingSent(test);
        btnMainkan = view.findViewById(R.id.streaming_mainkan);
        Button btnCobalagi = view.findViewById(R.id.streaming_cobalagi);
        progressBar = view.findViewById(R.id.streaming_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutServerDown = view.findViewById(R.id.serverdown);
        linearLayoutServerDown.setVisibility(View.GONE);
        relativeLayoutServerUp = view.findViewById(R.id.streaming_serverOn);
        relativeLayoutServerUp.setVisibility(View.GONE);
        navigationView = getActivity().findViewById(R.id.main_navigation);

        editTextPesan = view.findViewById(R.id.streaming_edittext);
        Button btnSend = view.findViewById(R.id.streaming_sendpesan);


        stopStreamingRadio();
        btnMainkan.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                FragmentHome fragmentHome = new FragmentHome();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragmentHome, "TEST").addToBackStack(null).commit();
                navigationView.setCheckedItem(R.id.nav_home);

            }
        });

        btnCobalagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTask().execute();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pesan = editTextPesan.getText().toString();
                Toast.makeText(getActivity().getApplicationContext(), pesan, Toast.LENGTH_SHORT).show();
                kirimKeServer(pesan);
                editTextPesan.setText("");
            }
        });
        new MyTask().execute();
        return view;
    }

    private void kirimKeServer(String pesan) {
        List<String> list = new ArrayList<>();
        list.add(pesan);
    }

    @Override
    public void onResume() {
        super.onResume();
        stopStreamingRadio();
    }

    private void stopStreamingRadio() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            btnMainkan.setText("Mainkan");
                        }
                    });
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("exit"));
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @SuppressLint("SetTextI18n")
    private void mainkan() {
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://122.248.39.157:8000/;");
        bundle.putString("name", "Live On Air Radio Streaming");
        Intent serviceOn = new Intent(getActivity().getApplicationContext(), StreamingService.class);
        serviceOn.putExtras(bundle);
        getActivity().startService(serviceOn);
        btnMainkan.setText("Hentikan");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updateData(CharSequence newData){
        if (newData.equals("streamingRadio")){
            if (getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        btnMainkan.setText("Mainkan");
                    }
                });
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String title ="";
            Document doc;
            try {
                doc = Jsoup.connect("http://122.248.39.157:8000/index.html?sid=1").get();
                Elements test = doc.select("b");
                Log.e(TAG, "doInBackground: " + test);
                if (test.text().contains("Stream is currently down.")){
                    Log.e(TAG, "doInBackground: SERVER DIED");
                    title = "SERVER STREAMING TIDAK DITEMUKAN";
                } else {
                    title = "";
                    mainkan();
                    Log.e(TAG, "doInBackground: SERVER ALIVE");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return title;
        }


        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("")){
                if (getActivity().findViewById(R.id.layout_streaming) != null){
                    RelativeLayout mRoot = getActivity().findViewById(R.id.layout_streaming);
                    Snackbar snackbar = Snackbar.make(mRoot, result, Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), merahmarun));
                    snackbar.show();
                }
                linearLayoutServerDown.setVisibility(View.VISIBLE);
                relativeLayoutServerUp.setVisibility(View.GONE);
            } else {
                linearLayoutServerDown.setVisibility(View.GONE);
                relativeLayoutServerUp.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }
}
