package com.example.cm.match;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.SendShare;
import com.example.cm.util.ClothesEstimater;
import com.example.cm.wardrobe.WardrobeFragment;

import static com.example.cm.MainActivity.getClothes_down;
import static com.example.cm.MainActivity.getClothes_up;
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
        //Log.d(TAG, "onResume: resume match");
        super.onResume();
        setToolbarText("搭配");
        Log.d(TAG, "onResume: "+MainActivity.getClothes_up());
        MainActivity mainActivity=(MainActivity)getActivity();
        if(MainActivity.getClothes_up()!=-1){
            ImageView clothes_up = view.findViewById(R.id.clothes_up);
            //.setImage
            clothes_up.setImageBitmap(WardrobeFragment.photoList1.get(MainActivity.getClothes_up()));
        }
        Log.d(TAG, "onResume: "+MainActivity.getClothes_down());
        if(MainActivity.getClothes_down()!=-1){
            ImageView clothes_down = view.findViewById(R.id.clothes_down);
            //.setImage
            clothes_down.setImageBitmap(WardrobeFragment.photoList2.get(MainActivity.getClothes_down()));
        }
    }

    private static final String TAG = "TEST";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateView: create match fragment");
        context = getActivity();
        view = View.inflate(context, R.layout.match, null);
        ImageView clothes_up = view.findViewById(R.id.clothes_up);
        ImageView clothes_down = view.findViewById(R.id.clothes_down);

        //上衣点击事件
        clothes_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setChoose_flag(true);
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.tab_main_content,new WardrobeFragment()).addToBackStack(null).commit();
                MainActivity.setFragmentTabHostVisibility(false);
            }
        });
        //下衣点击事件
        clothes_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setChoose_flag(true);
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.tab_main_content,new WardrobeFragment()).addToBackStack(null).commit();
            }
        });
        //智能搭配入口
        ImageButton match=view.findViewById(R.id.smart_match);
        match.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //智能搭配功能
                if(getClothes_up()!=-1&&getClothes_down()!=-1){
                    ClothesEstimater estimater=new ClothesEstimater(getContext());
                    int result=estimater.estimateClothes(WardrobeFragment.photoList1.get(getClothes_up()),
                            WardrobeFragment.photoList2.get(getClothes_down()));
                    switch(result){
                        case 1:
                            Toast.makeText(getContext(),"Good",Toast.LENGTH_LONG).show();break;
                        case 0:
                            Toast.makeText(getContext(),"Bad",Toast.LENGTH_LONG).show();break;
                        case -1:
                            Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();break;
                    }
                }
            }
        });
        //发送分享入口
        FloatingActionButton send_share=view.findViewById(R.id.send_share);
        send_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SendShare.class);
                startActivity(intent);
            }
        });
        return view;
    }}