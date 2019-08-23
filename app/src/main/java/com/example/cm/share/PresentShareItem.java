package com.example.cm.share;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.util.ServerFunction;

import de.hdodenhof.circleimageview.CircleImageView;

public class PresentShareItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_share_item);
        CircleImageView headImage = findViewById(R.id.headImage);
        headImage.setImageResource(R.drawable.friend1);
        ImageView clothesUp = findViewById(R.id.clothes_up);
        clothesUp.setImageResource(R.drawable.friend1);
        ImageView clothesDown = findViewById(R.id.clothes_down);
        clothesDown.setImageResource(R.drawable.friend1);
        TextView userName = findViewById(R.id.userName);
        userName.setTextSize(30);
        TextView description = findViewById(R.id.description);
        description.setText("");
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

                        userName.setText(shareItem.getUserName());
                        clothesUp.setImageURI(Uri.fromFile(ServerFunction.getUpImg(shareItem.getPostInfo())));
                        clothesDown.setImageURI(Uri.fromFile(ServerFunction.getDownImg(shareItem.getPostInfo())));
                        description.setText(shareItem.getDescription());

                    }
                });
            }
        }).start();

        ImageButton comment = findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PresentShareItem.this,Comments.class);
                intent.putExtra("post",shareItem.getPostInfo());
                startActivity(intent);
            }
        });
    }
}
