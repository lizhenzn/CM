package com.example.cm.match;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cm.R;
import com.example.cm.MainActivity;
import com.example.cm.util.ServerFunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import main.CallBackMethods;
import main.ImgManager;
import main.TransferManager;
import main.UserInfo;

import static com.example.cm.MainActivity.setToolbarText;

public class MatchFragment extends Fragment {
    private Context context;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarText("搭配");

    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbarText("搭配");
    }

    private static final String TAG = "TEST";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=getActivity();
        view=View.inflate(context, R.layout.match,null);

//        Button button = view.findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for(int i=1;i<=12;i++){
//
//                            ServerFunction.sendPost("test","第"+i+"条分享",2,"/sdcard/test/"+"saber"+i+"_up.jpg",
//                                    "/sdcard/test/"+"saber"+i+"_down.jpg","TSaber7",0);
//                        }
//                    }
//                }).start();
//            }
//        });
        return  view;
    }
}
