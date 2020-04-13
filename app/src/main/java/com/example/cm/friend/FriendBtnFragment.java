package com.example.cm.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;


public class FriendBtnFragment extends Fragment {
private Button friendBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.friend_fragment,container,false);
        friendBtn=(Button)view.findViewById(R.id.friend_btn);
        friendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),ContantActivity.class);
                startActivity(intent);
                ((Activity)MainActivity.getInstance()).overridePendingTransition(R.anim.bottom_in,R.anim.bottom_silent);

            }
        });
        return view;
    }
}
