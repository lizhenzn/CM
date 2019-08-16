package com.example.cm.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.myInfo.MyInfoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.cm.MainActivity.setToolbarText;

public class FriendFragment extends Fragment  {
    private Context context;
    private View view;
    private ListView chatLV;
    private ChatListAdapter chatListAdapter;
    private List<HashMap<String,Object>> list;      //会话列表

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.friend);
        setToolbarText("会话");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

           FragmentManager fragmentManager;

    {
        //调用子FragmentManager替换Fragment
        FriendBtnFragment friendBtnFragment=new FriendBtnFragment();
        //ChatLvFragment chatLvFragment=new ChatLvFragment();
        fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.btnFrame, friendBtnFragment);
        ft.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
                }
                View view=View.inflate(getActivity(),R.layout.friend,null);
                chatLV=(ListView)view.findViewById(R.id.chat_lv);



        //DisplayMetrics displayMetrics=new DisplayMetrics();
        //getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        initChatList();
        chatListAdapter=new ChatListAdapter(list,getActivity());
        chatLV.setAdapter(chatListAdapter);
        //点击进入聊天界面
               chatLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(getActivity(),MyInfoActivity.class);
                        startActivity(intent);
                        Toast.makeText(getContext(),"点击  "+position,Toast.LENGTH_SHORT).show();
                        Log.d("friend", "onItemClick: ");
                    }
                });
        //长按显示置顶删除等
        chatLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            String []choose={"置顶","删除"};
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setSingleChoiceItems(choose, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {




                    }
                });
                builder.setCancelable(true);
                builder.setIcon(R.drawable.button);
                builder.show();
                Toast.makeText(getContext(),"点击  "+position,Toast.LENGTH_SHORT).show();
                Log.d("friend", "onItemClick: ");
                return true;
            }
        });
         return view;
    }
    //初始化会话界面列表
    public void initChatList(){
        HashMap<String,Object> map=new HashMap<String, Object>();
        list=new ArrayList<>();
        map.put("headImage",R.drawable.ic_launcher_foreground);
        map.put("name","张三");
        map.put("message","Hello,World!");
        map.put("time","2019.6.6 19:10");
        list.add(map);
        map=new HashMap<>();
        map.put("headImage",R.drawable.ic_launcher_foreground);
        map.put("name","李四");
        map.put("message","Hello,World!");
        map.put("time","2019.5.6 19:10");
        list.add(map);
        for(int i=0;i<36;i++) {
            map = new HashMap<>();
            map.put("headImage", R.drawable.ic_launcher_foreground);
            map.put("name", "王五"+i);
            map.put("message", "Hello,World!");
            map.put("time", "2019.6.6 19:10");
            list.add(map);
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        setToolbarText("会话");
    }

}
