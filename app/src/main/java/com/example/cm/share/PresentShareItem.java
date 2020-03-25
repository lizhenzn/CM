package com.example.cm.share;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;
import com.example.cm.util.ServerFunction;

import de.hdodenhof.circleimageview.CircleImageView;

public class PresentShareItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_present_share_item);
        CircleImageView headImage = findViewById(R.id.headImage);
        ImageView clothesUp = findViewById(R.id.clothes_up);
        clothesUp.setImageResource(R.drawable.friend1);
        ImageView clothesDown = findViewById(R.id.clothes_down);
        clothesDown.setImageResource(R.drawable.friend1);
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
                        clothesUp.setImageURI(Uri.fromFile(ServerFunction.getUpImg(shareItem.getPostInfo())));
                        clothesDown.setImageURI(Uri.fromFile(ServerFunction.getDownImg(shareItem.getPostInfo())));
                        description.setText(shareItem.getDescription());

                    }
                });
            }
        }).start();

        ImageView comment = findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PresentShareItem.this,Comments.class);
                intent.putExtra("post",shareItem.getPostInfo());
                startActivity(intent);
            }
        });
        ImageView giveLike = findViewById(R.id.giveLike);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(ServerFunction.getShareManager().hasLiked(shareItem.getPostInfo().getPost_id(), MessageManager.getSmackUserInfo().getUserName())){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            giveLike.setImageResource(R.drawable.like_click);
                        }
                    });
                }
            }
        }).start();
        giveLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(ServerFunction.getShareManager().like(shareItem.getPostInfo(), MessageManager.getSmackUserInfo().getUserName()))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    giveLike.setImageResource(R.drawable.like_click);
                                    Toast.makeText(PresentShareItem.this,"点赞成功",Toast.LENGTH_SHORT).show();
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            //点赞数+1
//                                            //likeNum.setText(shareItem.getPostInfo().getLike_num()+1+"");
//                                        }
//                                    });
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PresentShareItem.this,"您已经点过赞了",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
