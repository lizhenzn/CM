package com.example.cm.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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


import com.example.cm.ConnectService;
import com.example.cm.R;
import com.example.cm.myInfo.LoginActivity;
import com.example.cm.myInfo.MyInfoActivity;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import main.CallBackMethods;
import main.TransferManager;
import main.UserInfo;


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
                /*UserInfo user=new UserInfo();
                user.setUserName("lizhen");
                user.setEmail("229010ad655");
                user.setPassWord("123456");
                TransferManager transferManager=new TransferManager();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        transferManager.doRegister(user, new CallBackMethods() {
                            @Override
                            public void onSuccess() {
                                Log.d("doRegister","666");
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onFailed() {

                            }

                            @Override
                            public void onBadLink() {

                            }
                        });
                    }
                }).start();*/
                //ConnectService connectService=new ConnectService();
               // connectService.getConnection();
                //if(connectService.login("lizhen","zn521128"))
                    //Log.d("loginOpenfile","Success");
               //boolean b= connectService.login("admin","zn521128");
                 Log.d("login","loginOpenfile");
                Log.d("login000001", "onClick: ");
               // ConnectService.getConnection();
                //ConnectService.login("lizhen","zn521128");
            }
        });


        init();
        recyclerView.addItemDecoration(new ShareItemDecoration());

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                init();
                                shareAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
        return  view;
    }
    private void init() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        shareItemList=new ArrayList<>();
        // 模拟获取数据
        getData();
        shareAdapter = new ShareAdapter(shareItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(shareAdapter);

        // 设置加载更多监听
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                shareAdapter.setLoadState(shareAdapter.LOADING);
                Log.d(TAG, "onLoadMore: loadState="+shareAdapter.getLoadState());
               if (shareItemList.size() < 52) {
                    // 模拟获取网络数据，延时1s
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               Thread.sleep(2000);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                           getActivity().runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   getData();
                                   shareAdapter.setLoadState(shareAdapter.LOADING_COMPLETE);
                               }
                           });
                       }
                   }).start();
                } else {
                    // 显示加载到底的提示
                    shareAdapter.setLoadState(shareAdapter.LOADING_END);
                }
            }
        });
    }
    private void getData() {
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