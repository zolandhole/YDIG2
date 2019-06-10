package com.surampaksakosoy.ydig.fragment;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.Services.StreamingService;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FragmentStreaming extends Fragment{

    private FragmentStreamingListener listener;
    private Button btnMainkan;
//    private String LINK = "http://live.radiosunnah.net/;";
//    private String NAMA = "Radio Streaming YDIG";

    public FragmentStreaming() {
    }

    public interface FragmentStreamingListener {
        void onInputStreamingSent(String input);
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
        listener.onInputStreamingSent("streaming");
        btnMainkan = view.findViewById(R.id.streaming_mainkan);
        btnMainkan.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (btnMainkan.getText().equals("Hentikan")){
                    Toast.makeText(getActivity().getApplicationContext(), "Hentikan radio", Toast.LENGTH_SHORT).show();
                    btnMainkan.setText("Mainkan");

                    StreamingService streamingService = new StreamingService();
                    streamingService.hideNotification();
                } else {
                    mainkan();
                }
            }
        });
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void mainkan() {
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://live.radiosunnah.net/;");
        bundle.putString("name", "Radio Cirebon");
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
}
