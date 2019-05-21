package com.surampaksakosoy.ydig.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.adapters.AdapterHome;
import com.surampaksakosoy.ydig.handlers.HandlerServer;
import com.surampaksakosoy.ydig.models.ModelHome;
import com.surampaksakosoy.ydig.utils.VolleyCallback;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private TextView textView;
    private RecyclerView recyclerView;
    private AdapterHome adapterHome;
    private LinearLayoutManager linearLayoutManager;
    private static final String TAG = "FragmentHome";
    private FragmentHomeListener listener;

    public FragmentHome(){
    }

    public interface  FragmentHomeListener{
        void onInputHomeSent(List<String> input);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHome.FragmentHomeListener){
            listener = (FragmentHomeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " harus mengimplementasi FragmentHomeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.home_recyclerview);
        textView = view.findViewById(R.id.home_textview);

        List<String> list = new ArrayList<>();
        list.add("0");
        HandlerServer handlerServer = new HandlerServer(getActivity(),"GET_HOME_DATA");
        synchronized (getActivity()){
            handlerServer.getList(new VolleyCallback() {
                @Override
                public void onFailed(String result) {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailed: " + result);
                }

                @Override
                public void onSuccess(List<ModelHome> result) {
                    tampilankanSuccess(result);
                }
            }, list);
        }


        return view;
    }

    private void tampilankanSuccess(List<ModelHome> result) {
        if (result.isEmpty()){
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            adapterHome = new AdapterHome(result,getActivity());
            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapterHome);
            recyclerView.setVisibility(View.VISIBLE);
        }



    }

    public void updateData(List<ModelHome> newData){
        Log.e(TAG, "updateData: " + newData);
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
