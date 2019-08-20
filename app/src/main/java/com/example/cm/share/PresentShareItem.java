package com.example.cm.share;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cm.R;

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
        userName.setText("TSaber7");
        ImageButton comment = findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PresentShareItem.this,Comments.class));
            }
        });
    }
}
