package com.example.cm.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

import com.example.cm.friend.FriendInfoActivity;
import com.example.cm.friend.chat.ImageDetail;
import com.example.cm.util.MessageManager;
import com.example.cm.util.ServerFunction;

public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ShareItem> ShareItemList;
    private Context context;
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
        ImageView GiveLike;
        TextView LikeNum;
        int like_num;//保存当前项点赞人数，便于点赞+1

        public RecyclerViewHolder(View view) {
            super(view);
            HeadImage = view.findViewById(R.id.headImage);
            UserName = view.findViewById(R.id.userName);
            ClothesUp = view.findViewById(R.id.clothes_up);
            ClothesDown = view.findViewById(R.id.clothes_down);
            Description = view.findViewById(R.id.description);
            GiveLike = view.findViewById(R.id.giveLike);
//            Comment = view.findViewById(R.id.comment);
            LikeNum = view.findViewById(R.id.like_num);
        }


    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        TextView tvLoading;

        FootViewHolder(View itemView) {
            super(itemView);
            tvLoading = (TextView) itemView.findViewById(R.id.foot_tip);
        }
    }

    public ShareAdapter(List<ShareItem> ShareItemList,Context context) {
        this.ShareItemList = ShareItemList;
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item, parent, false);
            RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);
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
        //普通广场消息项
        if (viewHolder instanceof RecyclerViewHolder) {
            ShareItem shareItem = ShareItemList.get(position);
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;
            //加载中空白项
            if(shareItem.isBlankItemFlag()){
                recyclerViewHolder.ClothesUp.setImageResource(R.drawable.ic_loading);
                recyclerViewHolder.ClothesDown.setImageResource(R.drawable.ic_loading);
            }
            //已加载消息项
            else{
                recyclerViewHolder.like_num=ShareItemList.get(position).getPostInfo().getLike_num();
                recyclerViewHolder.LikeNum.setText(recyclerViewHolder.like_num+"人觉得很赞");
                recyclerViewHolder.ClothesUp.setImageURI(Uri.fromFile(shareItem.getClothesUp()));
                recyclerViewHolder.ClothesDown.setImageURI(Uri.fromFile(shareItem.getClothesDown()));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(ServerFunction.getShareManager().hasLiked(shareItem.getPostInfo().getPost_id(), MessageManager.getSmackUserInfo().getUserName())){
                            ((MainActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerViewHolder.GiveLike.setImageResource(R.drawable.like_click);
                                }
                            });
                        }
                        else{
                            ((MainActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerViewHolder.GiveLike.setImageResource(R.drawable.like);
                                }
                            });
                        }
                    }
                }).start();
            }
            //广场单个消息项中的点击事件
            recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ShareItemList.get(position).isBlankItemFlag())
                        return;
                    Intent toPresentShareItem=new Intent(v.getContext(),PresentShareItem.class);
                    ShareItemList.get(position).removeHeadImage();
                    toPresentShareItem.putExtra("shareItem",ShareItemList.get(position));
                    v.getContext().startActivity(toPresentShareItem);
                }
            });
//            //上衣图片点击事件
//            recyclerViewHolder.ClothesUp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent=new Intent(context, ImageDetail.class);
//                    intent.putExtra("bitmapPath",shareItem.getClothesUp().toString());
//                    context.startActivity(intent);
//                }
//            });
//            //下衣图片点击事件
//            recyclerViewHolder.ClothesDown.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent=new Intent(context, ImageDetail.class);
//                    intent.putExtra("bitmapPath",shareItem.getClothesDown().toString());
//                    context.startActivity(intent);
//                }
//            });
            //点赞按钮点击事件
            recyclerViewHolder.GiveLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (ServerFunction.getShareManager().like(shareItem.getPostInfo(), MessageManager.getSmackUserInfo().getUserName())) {
                                ((MainActivity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerViewHolder.LikeNum.setText(recyclerViewHolder.like_num+1+"人觉得很赞");
                                        recyclerViewHolder.GiveLike.setImageResource(R.drawable.like_click);
                                        Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                ((MainActivity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "您已经点过赞了", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            });
            recyclerViewHolder.HeadImage.setImageBitmap(shareItem.getHeadImage());
            //头像点击事件
            recyclerViewHolder.HeadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, FriendInfoActivity.class);
                    intent.putExtra("userName",shareItem.getUserName());
                    context.startActivity(intent);
                }
            });
            recyclerViewHolder.UserName.setText(shareItem.getUserName());
            recyclerViewHolder.Description.setText("\u3000"+shareItem.getDescription());
        }
        //加载到底的末尾子项
        else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            switch (loadState) {

                case LOADING: // 正在加载
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setText("正在加载" );
                    Log.d(TAG, "onBindViewHolder: LOADING ");
                    break;
                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);Log.d(TAG, "onBindViewHolder:LOADING_COMPLETE ");
                    break;
                case LOADING_END: // 加载到底
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setText("加载到底" );
                    Log.d(TAG, "onBindViewHolder: LOADING_END");
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
