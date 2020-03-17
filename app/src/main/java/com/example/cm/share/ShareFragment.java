package com.example.cm.share;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cm.R;
import com.example.cm.util.ClothesEstimater;
import com.example.cm.util.Connect;
import com.example.cm.util.ServerFunction;

import java.util.ArrayList;
import java.util.List;

import main.PostInfo;
import main.ShareManager;

import static com.example.cm.MainActivity.setToolbarText;

public class ShareFragment extends Fragment {
    private static final String TAG = "ShareFragment";
    private Context context;
    private View view;
    private List<ShareItem> shareItemList ;
    private RecyclerView recyclerView;
    private ShareAdapter shareAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ServerFunction serverFunction;
    private Object ThreadManager=new Object();
    private int countLeft=-1;
    private int count=-1;


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
        serverFunction=new ServerFunction(context.getCacheDir());
        Button button=(Button)view.findViewById(R.id.share_btn);
        init();
        recyclerView.addItemDecoration(new ShareItemDecoration());
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        //刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                        init();
                        shareAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
            }
        });
        return  view;
    }
    private void init() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        shareItemList=new ArrayList<>();
        Log.d(TAG, "init:create new ShareItemList");
        // 获取数据
        getData(true);
        shareAdapter = new ShareAdapter(shareItemList,getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(shareAdapter);

        // 设置加载更多监听
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                shareAdapter.setLoadState(shareAdapter.LOADING);
                Log.d(TAG, "onLoadMore: loadState="+shareAdapter.getLoadState());
               if (countLeft>0) {
                   getData(false);
                   shareAdapter.setLoadState(shareAdapter.LOADING_COMPLETE);
                } else {
                    // 显示加载到底的提示
                    shareAdapter.setLoadState(shareAdapter.LOADING_END);
                }
            }
        });
    }
    private void getData(boolean refresh)  {
        if(refresh){
            countLeft=-1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    countLeft=serverFunction.getCount();
                }
            }).start();
            while(countLeft==-1){
                Log.d(TAG, "getData: is deadLoop");
            }
            Log.d(TAG, "init: countLeft="+countLeft);
            count=0;
        }
        if(countLeft>=10){
            count=10;
        }
        else{
            count=countLeft;
        }
        for (int i = 0; i < count; i++) {
            shareItemList.add(new ShareItem(R.drawable.friend1, Connect.smackUserInfo.getUserName(),R.drawable.friend1,
                    R.drawable.friend1,"",R.drawable.givelike,R.drawable.comment));
        }
        countLeft-=count;
        serverFunction.getShareManager().resetTransferFlags();
        Log.d(TAG, "getData: ready to enter thread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (ThreadManager){
                    if(refresh)
                        serverFunction.refresh();
                    //Log.d(TAG, "run: enter thread");
                    serverFunction.loadPostList();
                    //Log.d(TAG, "postlist: "+serverFunction.nextPost());
                    for (int i = 0; i < count; i++) {
                        //Log.d(TAG, "run: "+serverFunction.getCurrentPostPosition());
                        Log.d(TAG, "run: "+i);
                        while(!serverFunction.getShareManager().transfer_flags[i]){}
                        Log.d(TAG, "run: quite while");
                        PostInfo post=serverFunction.getPost();
                        String userName=null;
                        userName=serverFunction.getUserName();
                        while(userName==null){}
                        Log.d(TAG, "run: username="+userName);
                        shareItemList.set(shareItemList.size()-count+i,new ShareItem(post,Connect.getUserImage(userName),userName,serverFunction.getSmallUpImg(),
                                serverFunction.getSmallDownImg(),serverFunction.getDescription(),R.drawable.givelike,R.drawable.comment));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                shareAdapter.notifyDataSetChanged();
                            }
                        });
                        if(!serverFunction.nextPost()){
                            //Log.d(TAG, "run: "+serverFunction.getCurrentPostPosition());
                            break;
                        }
                    }
                }

            }
        }).start();
    }
}

class ShareItemDecoration extends RecyclerView.ItemDecoration{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,30,0,30);
    }
}