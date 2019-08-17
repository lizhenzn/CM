package com.example.cm.share;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cm.R;

import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private List<ShareItem> ShareItemList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ClothesUp;
        ImageView ClothesDown;
        TextView Description;
        ImageButton GiveLike;
        ImageButton Comment;

        public ViewHolder(View view) {
            super(view);
            ClothesUp = view.findViewById(R.id.clothes_up);
            ClothesDown = view.findViewById(R.id.clothes_down);
            Description = view.findViewById(R.id.description);
            GiveLike = view.findViewById(R.id.giveLike);
            Comment = view.findViewById(R.id.comment);
        }


}
        public ShareAdapter(List<ShareItem> ShareItemList){
            this.ShareItemList=ShareItemList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ShareItem shareItem = ShareItemList.get(position);
        viewHolder.ClothesUp.setImageResource(shareItem.getIdClothesUp());
        viewHolder.ClothesDown.setImageResource(shareItem.getIdClothesDown());
        viewHolder.Description.setText(shareItem.getDescription());
        viewHolder.GiveLike.setImageResource(shareItem.getIdGiveLike());
        viewHolder.Comment.setImageResource(shareItem.getIdComment());
    }

    @Override
    public int getItemCount() {
        return ShareItemList.size();
    }


}
