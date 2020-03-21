package com.example.cm.share;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cm.R;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;
import com.example.cm.util.ServerFunction;

import java.util.ArrayList;
import java.util.List;

import main.PostInfo;
import main.RemarkInfo;

public class Comments extends AppCompatActivity {
    private static final String TAG = "Comments";
    private List<CommentItem> CommentList;
    private PostInfo postInfo;
    private ArrayList<RemarkInfo> remarks;
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new CommentItemDecoration());
        postInfo=(PostInfo) getIntent().getSerializableExtra("post");
        initComments();
        EditText editText=findViewById(R.id.input);
        Button button = findViewById(R.id.sendText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServerFunction.sendRemark(postInfo, MessageManager.getSmackUserInfo().getUserName(),content,"2019-08-23 14:55:50");
                        Log.d(TAG, "run: execute sendRemark,posi_id="+postInfo.getPost_id()+",userName="+MessageManager.getSmackUserInfo().getUserName()+",content="+content);
                        ServerFunction.sendRemark(postInfo, MessageManager.getSmackUserInfo().getUserName(),content,"2019-08-23 14:55:50");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initComments();
                            }
                        });
                    }
                }).start();
                editText.setText("");
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initComments();
                commentAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initComments() {



        CommentList=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                remarks=ServerFunction.getRemark(postInfo);
                for(int i=1;i<=remarks.size();i++){
                    CommentList.add(new CommentItem(VCardManager.getUserImage(remarks.get(i-1).getUsername()),i+"F",remarks.get(i-1).getUsername(),remarks.get(i-1).getContent()));
                }
                commentAdapter=new CommentAdapter(CommentList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(commentAdapter);
                    }
                });

            }
        }).start();

    }

}
class CommentItemDecoration extends RecyclerView.ItemDecoration{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(20,20,20,20);
    }
}
