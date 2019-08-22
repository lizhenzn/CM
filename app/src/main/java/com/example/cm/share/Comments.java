package com.example.cm.share;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.cm.R;

import java.util.ArrayList;
import java.util.List;

public class Comments extends AppCompatActivity {
    private List<CommentItem> CommentList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initComments();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CommentAdapter(CommentList));
        recyclerView.addItemDecoration(new CommentItemDecoration());
    }

    private void initComments() {
        for (int i = 0; i <= 9; i++) {
            CommentList.add(new CommentItem(R.drawable.friend1, i+1+"F","TSaber7", "testtesttestesttest!!!!!!!!!!!!!!!!!!!!!!!!!!"));
        }
    }

}
class CommentItemDecoration extends RecyclerView.ItemDecoration{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(20,20,20,20);
    }
}
