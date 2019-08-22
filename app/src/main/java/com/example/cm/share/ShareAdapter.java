package com.example.cm.share;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ShareItem> ShareItemList;
    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position+1==getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    private static final String TAG = "ShareAdapter";

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView HeadImage;
        TextView UserName;
        ImageView ClothesUp;
        ImageView ClothesDown;
        TextView Description;
        ImageButton GiveLike;
        ImageButton Comment;

        public RecyclerViewHolder(View view) {
            super(view);
            HeadImage = view.findViewById(R.id.headImage);
            UserName = view.findViewById(R.id.userName);
            ClothesUp = view.findViewById(R.id.clothes_up);
            ClothesDown = view.findViewById(R.id.clothes_down);
            Description = view.findViewById(R.id.description);
            GiveLike = view.findViewById(R.id.giveLike);
            Comment = view.findViewById(R.id.comment);
        }


    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        TextView tvLoading;

        FootViewHolder(View itemView) {
            super(itemView);
            tvLoading = (TextView) itemView.findViewById(R.id.foot_tip);
        }
    }

    public ShareAdapter(List<ShareItem> ShareItemList) {
        this.ShareItemList = ShareItemList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item, parent, false);
            RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), PresentShareItem.class));
                }
            });
            viewHolder.Comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Comments.class);
                    v.getContext().startActivity(intent);
                }
            });
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            Log.d(TAG, "onCreateViewHolder: create foot_tip");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_foot_item, parent, false);
            FootViewHolder footViewHolder = new FootViewHolder(v);
            return footViewHolder;
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            ShareItem shareItem = ShareItemList.get(position);
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;
            if(shareItem.isBlankItemFlag()){
                recyclerViewHolder.ClothesUp.setImageResource(R.drawable.friend1);
                recyclerViewHolder.ClothesDown.setImageResource(R.drawable.friend1);
            }
            if(!shareItem.isBlankItemFlag()){
                recyclerViewHolder.ClothesUp.setImageURI(Uri.fromFile(shareItem.getClothesUp()));
                recyclerViewHolder.ClothesDown.setImageURI(Uri.fromFile(shareItem.getClothesDown()));
            }
            recyclerViewHolder.HeadImage.setImageResource(shareItem.getIdHeadImage());
            recyclerViewHolder.UserName.setText(shareItem.getUserName());
            recyclerViewHolder.Description.setText(shareItem.getDescription());
            recyclerViewHolder.GiveLike.setImageResource(shareItem.getIdGiveLike());
            recyclerViewHolder.Comment.setImageResource(shareItem.getIdComment());
        } else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            switch (loadState) {

                case LOADING: // 正在加载

//                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setText("正在加载" );
                    Log.d(TAG, "onBindViewHolder: LOADING ");
//                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
//                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);Log.d(TAG, "onBindViewHolder:LOADING_COMPLETE ");
//                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
//                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    Log.d(TAG, "onBindViewHolder: LOADING_END");
//                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    }
    @Override
    public int getItemCount () {
        return ShareItemList.size()+1 ;
    }
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    public void changeItem() {

    }
    public int getLoadState(){
        return this.loadState;
    }
}
