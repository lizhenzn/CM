package com.example.cm.friend.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.ChatListAdapter;
import com.example.cm.util.MessageManager;

import static com.example.cm.MainActivity.setToolbarText;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionFragment extends Fragment {
private ListView sessionLV;
private ChatListAdapter chatListAdapter;
private boolean work;

    public SessionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        work=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=View.inflate(getActivity(),R.layout.session,null);
        sessionLV=view.findViewById(R.id.session_lv);
        chatListAdapter=new ChatListAdapter(getActivity());
        sessionLV.setAdapter(chatListAdapter);
        if(MessageManager.isHaveNewMessage()) {
            chatListAdapter.notifyDataSetChanged();
            MessageManager.setHaveNewMessage(false);
        }
        sessionLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userName", MessageManager.getFriendInfoList().get(position).getUserName());
                intent.putExtra("noteName",MessageManager.getFriendInfoList().get(position).getNoteName());
                startActivity(intent);
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        work=true;
        Log.d("test", "onResume: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("test", "run: ");
                while(work) {
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
                    synchronized((Object)work){
                        if(!work)break;
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
        work=false;
        Log.d("test", "onPause: ");
    }

}
