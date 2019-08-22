package com.example.cm.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.cm.ConnectService;
import com.example.cm.R;
import com.example.cm.myInfo.LoginActivity;
import com.example.cm.myInfo.MyInfoActivity;


import java.util.ArrayList;
import java.util.List;

import main.CallBackMethods;
import main.TransferManager;
import main.UserInfo;


import static com.example.cm.MainActivity.setToolbarText;

public class ShareFragment extends Fragment {
    private Context context;
    private View view;
    private List<ShareItem> shareItemList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarText("广场");

    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbarText("广场");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=getActivity();
        view=View.inflate(context, R.layout.share,null);
        Button button=(Button)view.findViewById(R.id.share_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        initShareItems();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ShareAdapter adapter = new ShareAdapter(shareItemList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ShareItemDecoration());
        return  view;
    }

    private void initShareItems() {
        for (int i = 0; i <= 9; i++) {
            shareItemList.add(new ShareItem(R.drawable.friend1,"TSaber7",R.drawable.friend1,R.drawable.friend1,"test!!!!!!!!!!!!!!!!!!!!!!!!!!!",R.drawable.givelike,R.drawable.comment));
        }
    }
}

class ShareItemDecoration extends RecyclerView.ItemDecoration{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,30,0,30);
    }
}