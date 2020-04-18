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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;
import com.example.cm.util.ServerFunction;

import java.util.ArrayList;
import java.util.List;

import main.PostInfo;

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
    private boolean isRefreshing=true;

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
        if(!Connect.isLogined){
            Toast.makeText(context,"您尚未登陆",Toast.LENGTH_LONG).show();
            ImageView imageView=view.findViewById(R.id.invisible);
            imageView.setVisibility(View.VISIBLE);
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setEnabled(false);
            return view;
        }
        serverFunction=new ServerFunction(context.getCacheDir());
        init();
        recyclerView.addItemDecoration(new ShareItemDecoration());
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        //刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isRefreshing){
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                if(!Connect.isLogined){
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context,"您尚未登陆",Toast.LENGTH_LONG).show();
                    return;
                }
                isRefreshing=true;
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
        Log.d(TAG, "getData: countLeft="+countLeft+",count="+count);
        for (int i = 0; i < count; i++) {
            shareItemList.add(new ShareItem(R.drawable.ic_loading, "用户名",R.drawable.ic_loading,
                    R.drawable.ic_loading,"",R.drawable.givelike,R.drawable.comment));
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
                    serverFunction.loadPostList();
                    for (int i = 0; i < count; i++) {
                        while(!serverFunction.getShareManager().transfer_flags[i]){ }
                        PostInfo post=serverFunction.getPost();
                        String userName=null;
                        userName=serverFunction.getUserName();
                        while(userName==null){ }
                        shareItemList.set(shareItemList.size()-count+i,new ShareItem(post, VCardManager.getUserImage(userName),userName,serverFunction.getSmallUpImg(),
                                serverFunction.getSmallDownImg(),serverFunction.getDescription(),R.drawable.givelike,R.drawable.comment));
                        isRefreshing=false;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                shareAdapter.notifyDataSetChanged();
                            }
                        });
                        if(!serverFunction.nextPost()){
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