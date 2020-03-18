package com.example.cm.match;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.wardrobe.WardrobeFragment;

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
        Log.d(TAG, "onResume: "+MainActivity.getClothesUp());
        MainActivity mainActivity=(MainActivity)getActivity();
        if(MainActivity.getClothesUp()!=-1){
            ImageView clothes_up = view.findViewById(R.id.clothes_up);
            //.setImage
            clothes_up.setImageBitmap(WardrobeFragment.photoList1.get(MainActivity.getClothesUp()));
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
        clothes_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setChoose_flag(true);
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.tab_main_content,new WardrobeFragment()).addToBackStack(null).commit();
                //Log.d(TAG, "onClick: replace!");
                //MainActivity mainActivity=(MainActivity)getActivity();
                //while(mainActivity.getClothesUp()==-1){}
            }
        });
        ImageView clothes_down = view.findViewById(R.id.clothes_down);
        clothes_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setChoose_flag(true);
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.tab_main_content,new WardrobeFragment()).addToBackStack(null).commit();
                //Log.d(TAG, "onClick: replace!");
                //MainActivity mainActivity=(MainActivity)getActivity();
                //while(mainActivity.getClothesUp()==-1){}
            }
        });
        ImageButton match=view.findViewById(R.id.smart_match);
        match.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //智能搭配功能
            }
        });
        return view;
    }}
//        Button button = view.findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for(int i=1;i<=12;i++){
//
//                            ServerFunction