package com.example.cm.share;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.friend.chat.ImageDetail;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.util.MessageManager;
import com.example.cm.util.ServerFunction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import main.PostInfo;
import main.RemarkInfo;

public class PresentShareItem extends AppCompatActivity {
    private List<CommentItem> CommentList;
    private PostInfo postInfo;
    private ArrayList<RemarkInfo> remarks;
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerView;
    private String clothes_up_path;
    private String clothes_down_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_present_share_item);
        CircleImageView headImage = findViewById(R.id.headImage);
        ImageView clothesUp = findViewById(R.id.clothes_up);
        clothesUp.setImageResource(R.drawable.ic_loading);
        ImageView clothesDown = findViewById(R.id.clothes_down);
        clothesDown.setImageResource(R.drawable.ic_loading);
        TextView userName = findViewById(R.id.userName);
        TextView description = findViewById(R.id.description);
        description.setText("");
        //TextView likeNum = findViewById(R.id.like_num);
        ShareItem shareItem=(ShareItem) getIntent().getSerializableExtra("shareItem");
        ServerFunction.getShareManagerForPresent().resetTransferFlags();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerFunction.loadImg(shareItem.getPostInfo());

                while(!ServerFunction.getShareManagerForPresent().transfer_flags[0]){}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //likeNum.setText(shareItem.getPostInfo().getLike_num()+"");
                        //userName.setText(shareItem.getUserName());
                        File clothesUpFile=ServerFunction.getUpImg(shareItem.getPostInfo());
                        File clothesDownFile=ServerFunction.getDownImg(shareItem.getPostInfo());
                        clothes_up_path=clothesUpFile.toString();
                        clothes_down_path=clothesDownFile.toString();
                        clothesUp.setImageURI(Uri.fromFile(clothesUpFile));
                        clothesDown.setImageURI(Uri.fromFile(clothesDownFile));
                        description.setText(shareItem.getDescription());
                    }
                });
            }
        }).start();
        //上衣图片点击事件
        clothesUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PresentShareItem.this, ImageDetail.class);
                intent.putExtra("bitmapPath",clothes_up_path);
                startActivity(intent);
            }
        });
        //下衣图片点击事件
        clothesDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PresentShareItem.this, ImageDetail.class);
                intent.putExtra("bitmapPath",clothes_down_path);
                startActivity(intent);
            }
        });
//        ImageView comment = findViewById(R.id.comment);
//        comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(PresentShareItem.this,Comments.class);
//                intent.putExtra("post",shareItem.getPostInfo());
//                startActivity(intent);
//            }
//        });

//        ImageView giveLike = findViewById(R.id.giveLike);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(ServerFunction.getShareManager().hasLiked(shareItem.getPostInfo().getPost_id(), MessageManager.getSmackUserInfo().getUserName())){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            giveLike.setImageResource(R.drawable.like_click);
//                        }
//                    });
//                }
//            }
//        }).start();
//        giveLike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(ServerFunction.getShareManager().like(shareItem.getPostInfo(), MessageManager.getSmackUserInfo().getUserName()))
//                        {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    giveLike.setImageResource(R.drawable.like_click);
//                                    Toast.makeText(PresentShareItem.this,"点赞成功",Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                        else{
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(PresentShareItem.this,"您已经点过赞了",Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                }).start();
//            }
//        });
        //评论区部分
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new CommentItemDecoration());
        postInfo=shareItem.getPostInfo();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PresentShareItem.this,"评论已发送",Toast.LENGTH_SHORT).show();
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
                for(int i=remarks.size();i>=1;i--){
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
    class CommentItemDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(20,20,20,20);
        }
    }
}
