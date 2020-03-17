package com.example.cm.share;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cm.R;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<CommentItem> allComments;
    public CommentAdapter(List<CommentItem> allComments) {
        this.allComments = allComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CommentItem comment = allComments.get(i);
        viewHolder.headImage.setImageBitmap(comment.getHeadImage());
        viewHolder.number.setText(comment.getNumber());
        viewHolder.userName.setText(comment.getUserName());
        viewHolder.commentText.setText(comment.getCommentText());
    }

    @Override
    public int getItemCount() {
        return allComments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView headImage;
        TextView number;
        TextView userName;
        TextView commentText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage=itemView.findViewById(R.id.headImage);
            number=itemView.findViewById(R.id.number);
            userName = itemView.findViewById(R.id.userName);
            commentText = itemView.findViewById(R.id.commentText);
        }
    }
}
