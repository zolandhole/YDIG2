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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.Services.StreamingService;
import com.surampaksakosoy.ydig.adapters.AdapterStreaming;
import com.surampaksakosoy.ydig.handlers.DBHandler;
import com.surampaksakosoy.ydig.handlers.HandlerServer;
import com.surampaksakosoy.ydig.interfaces.VolleyCallback;
import com.surampaksakosoy.ydig.models.ModelStreaming;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.surampaksakosoy.ydig.R.color.merahmarun;

public class FragmentStreaming extends Fragment{

    private static final String TAG = "FragmentStreaming";
    private FragmentStreamingListener listener;
    private Button btnMainkan;
    private LinearLayout linearLayoutServerDown;
    private RelativeLayout relativeLayoutServerUp;
    private NavigationView navigationView;
    private ProgressBar progressBar;
    private EditText editTextPesan;
    private DBHandler dbHandler;
    private String ID_LOGIN;
    private TextView textViewNoComment;
    private AdapterStreaming adapterStreaming;

    private RecyclerView recyclerView;
    private List<ModelStreaming> modelStreamings;
    private LinearLayoutManager linearLayoutManager;

    private BroadcastReceiver localbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> dataPesan = intent.getStringArrayListExtra("DATANOTIF");
            pesanBaru(dataPesan);
        }
    };

    private BroadcastReceiver streamingBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive stopStreamingRadio: " + intent);
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
        textViewNoComment = view.findViewById(R.id.streaming_no_comment);
        textViewNoComment.setVisibility(View.GONE);

        FirebaseMessaging.getInstance().subscribeToTopic("streamingTanya");

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(localbroadcast, new IntentFilter("PESANBARU"));

        getActivity().registerReceiver(streamingBroadcast, new IntentFilter("EXIT"));

        editTextPesan = view.findViewById(R.id.streaming_edittext);
        Button btnSend = view.findViewById(R.id.streaming_sendpesan);

        dbHandler = new DBHandler(getActivity().getApplicationContext());
        checkLocalDB();
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
                if (!pesan.equals("")){
                    kirimKeServer(pesan);
                }
                editTextPesan.setText("");
            }
        });
        new MyTask().execute();

        recyclerView = view.findViewById(R.id.streaming_recyclerview);
        getData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(streamingBroadcast);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(localbroadcast);
    }

    private void pesanBaru(ArrayList<String> dataPesan) {

        ModelStreaming item = (new ModelStreaming(
                Integer.parseInt(dataPesan.get(0)),dataPesan.get(1),dataPesan.get(2),dataPesan.get(3),dataPesan.get(4),dataPesan.get(5)
        ));
        int insertIndex = 0;
        modelStreamings.add(insertIndex, item);
        adapterStreaming.notifyItemInserted(insertIndex);
        linearLayoutManager.scrollToPosition(0);
    }

    private void getData() {
        List<String> list = new ArrayList<>();
        list.add("0");
        list.add("test");
        HandlerServer handlerServer = new HandlerServer(getActivity().getApplicationContext(), "LOAD_COMMENT_DATA");
        synchronized (getActivity().getApplicationContext()){
            handlerServer.getList(new VolleyCallback() {
                @Override
                public void onFailed(String result) {
                    textViewNoComment.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(JSONArray jsonArray) {
                    prosesResult(jsonArray);
                }
            }, list);
        }
    }

    private void prosesResult(JSONArray jsonArray) {
        List<ModelStreaming> list = new ArrayList<>();
            try {
                JSONObject dataServer;
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataServer = jsonArray.getJSONObject(i);
                    JSONObject isiData = dataServer.getJSONObject("data");
                    list.add(new ModelStreaming(
                            Integer.parseInt(dataServer.getString("id")),
                            isiData.getString("pesan"),
                            isiData.getString("tanggal"),
                            isiData.getString("waktu"),
                            isiData.getString("id_login"),
                            isiData.getString("photo")
                    ));
                }
                tampilkanSuccess(list);
            } catch (JSONException e) {
                Log.e(TAG, "prosesResult: " + e);
                e.printStackTrace();
            }
    }

    private void tampilkanSuccess(List<ModelStreaming> result) {
        this.modelStreamings = result;
        Log.e(TAG, "tampilkanSuccess: " + result.size());
        if (result.isEmpty()){
            textViewNoComment.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewNoComment.setVisibility(View.GONE);
            linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            adapterStreaming = new AdapterStreaming(modelStreamings, getActivity().getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapterStreaming);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            linearLayoutManager.scrollToPosition(0);
        }
    }

    private void kirimKeServer(String pesan) {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        String tanggal = df.format(c);
        String waktu = tf.format(c);

        List<String> list = new ArrayList<>();
        list.add(pesan);
        list.add(tanggal);
        list.add(waktu);
        list.add(ID_LOGIN);

        HandlerServer handlerServer = new HandlerServer(getActivity().getApplicationContext(),"SEND_COMMENT_DATA");
        synchronized (getActivity().getApplicationContext()){
            handlerServer.getList(new VolleyCallback() {
                @Override
                public void onFailed(String result) {
                    Log.e(TAG, "onFailed: " + result);
                }

                @Override
                public void onSuccess(JSONArray jsonArray) {
                    Toast.makeText(getActivity().getApplicationContext(), "Pesan Terkirim", Toast.LENGTH_SHORT).show();
                }
            },list);
        }
    }

    private void checkLocalDB() {
        ArrayList<HashMap<String, String>> userDB = dbHandler.getUser(1);
        for (Map<String, String> map : userDB) {
            ID_LOGIN = map.get("id_login");
        }
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
