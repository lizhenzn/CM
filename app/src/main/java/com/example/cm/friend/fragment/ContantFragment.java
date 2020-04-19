package com.example.cm.friend.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.cm.R;
import com.example.cm.friend.FriendInfoActivity;
import com.example.cm.friend.SideBar;
import com.example.cm.friend.SortAdapter;
import com.example.cm.util.MessageManager;

import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ContantFragment extends Fragment {
    private ListView contantLV;
    private SideBar sideBar;
    private SortAdapter sortAdapter;
    private boolean work;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getActivity(),R.layout.contant,null);
        contantLV=view.findViewById(R.id.contant_lv);
        sideBar=view.findViewById(R.id.contant_sidebar);
        contantLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), FriendInfoActivity.class);
                String userName= MessageManager.getContantFriendInfoList().get(position).getUserName();
                intent.putExtra("userName",userName);
                intent.putExtra("position",position);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_from_left);
            }
        });
        if(sideBar==null){
            Log.d("", "initView:sideBar为空222233333 ");
        }else {
            Log.d("", "initView:contantFriendlist长度为： "+MessageManager.getContantFriendInfoList().size());
            sideBar.setOnStrSelectCallBack((index, selectStr) -> {
                for (int i = 0; i < MessageManager.getContantFriendInfoList().size(); i++) {
                    if (selectStr.equalsIgnoreCase(MessageManager.getContantFriendInfoList().get(i).getFirstLetter())) {
                        contantLV.setSelection(i); // 选择到首字母出现的位置
                        return;
                    }
                }
            });
        }
        initData();
        return view;
    }
    private void initData() {
        work=true;
        Collections.sort(MessageManager.getContantFriendInfoList()); // 对list进行排序，需要让User实现Comparable接口重写compareTo方法
        sortAdapter = new SortAdapter(getContext(), MessageManager.getContantFriendInfoList());
        contantLV.setAdapter(sortAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        work=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(work) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MessageManager.isContantListChanged()) {
                                Collections.sort(MessageManager.getContantFriendInfoList());
                                sortAdapter.notifyDataSetChanged();
                                MessageManager.setContantListChanged(false); ;
                            }
                        }
                    });
                    synchronized((Object)work){
                        if(!work)break;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        work=false;
        Log.d("test", "onPause: ");
    }

}
