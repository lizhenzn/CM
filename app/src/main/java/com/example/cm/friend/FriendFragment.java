package com.example.cm.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.ChatListAdapter;
import com.example.cm.util.MessageManager;

import java.util.HashMap;
import java.util.List;


import static com.example.cm.MainActivity.setToolbarText;

public class FriendFragment extends Fragment  {
    private Context context;
    private View view;
    private ListView chatLV;
    private ChatListAdapter chatListAdapter;
    private List<HashMap<String,Object>> list;      //会话列表
    static boolean working;
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




        //initChatList();
        chatListAdapter=new ChatListAdapter(getActivity());
        chatLV.setAdapter(chatListAdapter);
        Log.d("test", "onCreateView: ");
        if(MessageManager.isHaveNewMessage()) {
            chatListAdapter.notifyDataSetChanged();
            MessageManager.setHaveNewMessage(false);
        }



        //点击进入聊天界面
               chatLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("userName",MessageManager.getFriendInfoList().get(position).getUserName());
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




    @Override
    public void onResume() {
        super.onResume();
        working=true;
        Log.d("test", "onResume: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("test", "run: ");
                while(working) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MessageManager.isHaveNewMessage()||ChatActivity.isSend) {
                                chatListAdapter.notifyDataSetChanged();
                                MessageManager.setHaveNewMessage(false); ;
                                ChatActivity.isSend=false;
                            }
                        }
                    });
                    synchronized((Object)FriendFragment.working){
                        if(!working)break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //chatListAdapter.notifyDataSetChanged();
        setToolbarText("会话");
    }

    @Override
    public void onPause() {
        super.onPause();
        working=false;
        Log.d("test", "onPause: ");
    }

}
