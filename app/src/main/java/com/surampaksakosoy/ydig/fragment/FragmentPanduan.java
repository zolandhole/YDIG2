package com.surampaksakosoy.ydig.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.surampaksakosoy.ydig.R;

public class FragmentPanduan extends Fragment {

    private TextView textView;
    private FragmentPanduanListener listener;

    public FragmentPanduan(){
    }

    public interface FragmentPanduanListener {
        void onInputPanduanSent(String input);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentPanduan.FragmentPanduanListener){
            listener = (FragmentPanduanListener) context;
        } else {
            throw new RuntimeException(context.toString() + "harus mengimplementasi FragmentPanduanListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panduan, container, false);
        textView = view.findViewById(R.id.panduan_textview);
        textView.setText(R.string.arab_here);
        listener.onInputPanduanSent("panduan");
        return view;
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
