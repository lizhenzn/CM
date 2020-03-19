package com.example.cm;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.cm.wardrobe.WardrobeFragment;

public class SendShare extends AppCompatActivity {
    private static final String TAG = "SendShare";
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_share);
        view = View.inflate(this, R.layout.match, null);
        ImageView clothes_up = view.findViewById(R.id.clothes_up);
        Log.d(TAG, "onCreate: clothes_up and clothes_down="+MainActivity.getClothes_up()+","+MainActivity.getClothes_down());
        if(MainActivity.getClothes_up()!=-1){
            clothes_up.setImageBitmap(WardrobeFragment.photoList1.get(MainActivity.getClothes_up()));
        }
        ImageView clothes_down = view.findViewById(R.id.clothes_down);
        if(MainActivity.getClothes_down()!=-1){
            clothes_down.setImageBitmap(WardrobeFragment.photoList1.get(MainActivity.getClothes_down()));
        }
    }
}
