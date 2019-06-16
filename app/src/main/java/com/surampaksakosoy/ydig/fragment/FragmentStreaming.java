package com.surampaksakosoy.ydig.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.surampaksakosoy.ydig.BroadcastReceivers.StreamingReceiver;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.Services.StreamingService;

public class FragmentStreaming extends Fragment{

    private static final String TAG = "FragmentStreaming";
    private FragmentStreamingListener listener;
    private Button btnMainkan;
    private BroadcastReceiver broadcastReceiver;


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
        stopStreamingRadio();
        btnMainkan.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (btnMainkan.getText().equals("Hentikan")){
                    Intent intentExit = new Intent(getActivity().getApplicationContext(), StreamingReceiver.class);
                    intentExit.setAction("exit");
                    getActivity().sendBroadcast(intentExit);
                } else {
                    mainkan();
                }
            }
        });
        return view;
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
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        btnMainkan.setText("Mainkan");
                    }
                });
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
