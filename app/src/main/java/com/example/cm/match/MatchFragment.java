package com.example.cm.match;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.util.ClothesEstimater;
import com.example.cm.util.Connect;
import com.example.cm.wardrobe.WardrobeFragment;

import static com.example.cm.MainActivity.setToolbarText;

public class MatchFragment extends Fragment {
    private Context context;
    private View view;
    private ImageView clothes_up;
    private ImageView clothes_down;
    private ImageView clothes_up_model;
    private ImageView clothes_down_model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarText("搭配");
    }
    @Override
    public void onResume() {
        //返回该碎片时显示已选取衣物
        super.onResume();
        setToolbarText("搭配");
        Log.d(TAG, "onResume: "+MainActivity.getClothes_up());
        if(MainActivity.getClothes_up()!=-1){
            clothes_up = view.findViewById(R.id.clothes_up);
            //设置上衣
            clothes_up.setImageBitmap(WardrobeFragment.photoList1.get(MainActivity.getClothes_up()));
            clothes_up_model.setImageBitmap(WardrobeFragment.photoList1.get(MainActivity.getClothes_up()));
        }
        Log.d(TAG, "onResume: "+MainActivity.getClothes_down());
        if(MainActivity.getClothes_down()!=-1){
            clothes_down = view.findViewById(R.id.clothes_down);
            //设置下衣
            clothes_down.setImageBitmap(WardrobeFragment.photoList2.get(MainActivity.getClothes_down()));
            clothes_down_model.setImageBitmap(WardrobeFragment.photoList2.get(MainActivity.getClothes_down()));
        }
    }
    private static final String TAG = "TEST";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateView: create match fragment");
        context = getActivity();
        view = View.inflate(context, R.layout.match, null);
        clothes_up = view.findViewById(R.id.clothes_up);
        clothes_down = view.findViewById(R.id.clothes_down);
        clothes_up_model = view.findViewById(R.id.clothes_up_model);
        clothes_down_model = view.findViewById(R.id.clothes_down_model);
        //选择上衣点击事件
        clothes_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setChoose_flag(true);
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.tab_main_content,new WardrobeFragment()).addToBackStack(null).commit();
                MainActivity.setFragmentTabHostVisibility(false);
            }
        });
        //选择下衣点击事件
        clothes_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setChoose_flag(true);
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.tab_main_content,new WardrobeFragment()).addToBackStack(null).commit();
                MainActivity.setFragmentTabHostVisibility(false);
            }
        });
        //智能搭配入口
        ImageView match=view.findViewById(R.id.smart_match);
        match.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //智能搭配功能
                int up=-1;
                int down=-1;
                int clothes_up_count=WardrobeFragment.photoList1.size();
                int clothes_down_count=WardrobeFragment.photoList2.size();
                if(clothes_up_count==0||clothes_down_count==0){
                    Toast.makeText(getContext(), "缺少搭配的衣物", Toast.LENGTH_LONG).show();
                    return;
                }
                up=(int)(Math.random()*(clothes_up_count-1));
                down=(int)(Math.random()*(clothes_down_count-1));
                int i=up;
                int j=down;
                ClothesEstimater estimater=new ClothesEstimater(getContext());
                do{
                    do {
                        int upSeason=WardrobeFragment.upSeason.get(i);
                        int downSeason=WardrobeFragment.downSeason.get(j);
                        if(upSeason!=WardrobeFragment.SEASON_DEFAULT&&downSeason!=WardrobeFragment.SEASON_DEFAULT){
                            if(upSeason!=downSeason){
                                j = (j + 1) % clothes_down_count;
                                continue;
                            }
                        }
                        int result = estimater.estimateClothes(WardrobeFragment.photoList1.get(i), WardrobeFragment.photoList2.get(j));
                        if (result == 1) {
                            Toast.makeText(getContext(), "已给出智能搭配", Toast.LENGTH_LONG).show();
                            MainActivity.setClothes_up(i);
                            MainActivity.setClothes_down(j);
                            clothes_up.setImageBitmap(WardrobeFragment.photoList1.get(i));
                            clothes_up_model.setImageBitmap(WardrobeFragment.photoList1.get(i));
                            clothes_down.setImageBitmap(WardrobeFragment.photoList2.get(j));
                            clothes_down_model.setImageBitmap(WardrobeFragment.photoList2.get(j));
                            return;
                        } else {
                            j = (j + 1) % clothes_down_count;
                        }
                    }while(j!=down);
                    i = (i + 1) % clothes_up_count;
                }while(i!=up);
                Toast.makeText(getContext(), "未能找出合适的搭配", Toast.LENGTH_LONG).show();
            }
        });
        //发送分享入口
        ImageView send_share=view.findViewById(R.id.send_share);
        send_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Connect.isLogined){
                    Toast.makeText(context,"您未登录",Toast.LENGTH_LONG).show();
                    return;
                }
                if(MainActivity.getClothes_up()==-1||MainActivity.getClothes_down()==-1){
                    Toast.makeText(context,"搭配不完整",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent=new Intent(getActivity(), SendShare.class);
                startActivity(intent);
            }
        });
        return view;
    }}