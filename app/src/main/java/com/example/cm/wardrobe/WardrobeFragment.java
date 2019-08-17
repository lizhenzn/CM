package com.example.cm.wardrobe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cm.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.cm.MainActivity.setToolbarText;

public class WardrobeFragment extends Fragment  {
    private Context context;
    private View view;
    private List<Integer> photoList1,photoList2; //展示的上衣、裤子和上面展示的的详情
    private static List<Integer> integerList;
    private static List<List<Integer>> detailList1,detailList2;
    private RecyclerView wardrobeR1,wardrobeR2;
    private WardrobeAdapter wardrobeAdapter1,wardrobeAdapter2;
    private static ViewPager viewPager;
    private  static  WardrobeVPAdapter wardrobeVPAdapter;
    //1:滑动上衣 2：滑动裤子
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarText("衣柜");

    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbarText("衣柜");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=getActivity();
        initData();
        wardrobeVPAdapter=new WardrobeVPAdapter(context,integerList);
        wardrobeAdapter1=new WardrobeAdapter(context,photoList1,1);
        wardrobeAdapter2=new WardrobeAdapter(context,photoList2,2);
        view=View.inflate(context, R.layout.wardrobe,null);
        viewPager=(ViewPager)view.findViewById(R.id.wardrobeVP);
        viewPager.setPageTransformer(true,new Transform());
        //viewPager.setAdapter(wardrobeVPAdapter);
        wardrobeR1=(RecyclerView)view.findViewById(R.id.wardrobeR1);
        wardrobeR2=(RecyclerView)view.findViewById(R.id.wardrobeR2);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(context);
        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        wardrobeR1.setLayoutManager(linearLayoutManager1);
        wardrobeR2.setLayoutManager(linearLayoutManager2);
        wardrobeR1.setAdapter(wardrobeAdapter1);
        wardrobeR2.setAdapter(wardrobeAdapter2);


        return  view;
    }

    public void initData(){
        photoList1=new ArrayList<>();
        photoList2=new ArrayList<>();
        integerList=new ArrayList<>();
        detailList1=new ArrayList<>();
        detailList2=new ArrayList<>();
        for(int i=0;i<66;i++) {
            photoList1.add(R.drawable.cm);
            photoList2.add(R.drawable.friend);
        }
        for(int i=0;i<photoList1.size();i++){

                detailList1.add(new ArrayList<Integer>(Arrays.asList(R.drawable.wardrobe, R.drawable.ic_launcher_background, R.drawable.wardrobe, R.drawable.cm)));

        }
        for(int i=0;i<photoList1.size();i++){

            detailList2.add(new ArrayList<Integer>(Arrays.asList(R.drawable.friend, R.drawable.friend1, R.drawable.match, R.drawable.cm)));

        }

    }



    class Transform implements ViewPager.PageTransformer{
        public  float MIN_ALPHA = 0.5f;
        public  float MIN_SCALE = 0.8f;
        @Override
        public void transformPage(@NonNull View view, float position)  {
            if (position < -1 || position > 1) {
                viewPager.setAlpha(MIN_ALPHA);
                viewPager.setScaleX(MIN_SCALE);
                viewPager.setScaleY(MIN_SCALE);
                Log.i("info", "缩放：position < -1 || position > 1");
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                if (position < 0) {
                    float scaleX = 1 + 0.2f * position;
                    Log.i("info", "缩放：position < 0");
                    viewPager.setScaleX(scaleX);
                    viewPager.setScaleY(scaleX);
                } else {
                    float scaleX = 1 - 0.2f * position;
                    viewPager.setScaleX(scaleX);
                    viewPager.setScaleY(scaleX);
                    Log.i("info", "缩放：position <= 1 >=0");
                }
                viewPager.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }
        }


    }

    public static class WardrobeAdapter extends RecyclerView.Adapter<WardrobeAdapter.ViewHolder> {
        private List<Integer> photoList;
        private  Context context;
        int type;
        public WardrobeAdapter(Context context,List<Integer> photoList,int type){
            this.context=context;
            this.photoList=photoList;
            this.type=type;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ViewHolder viewHolder=null;
            View view= LayoutInflater.from(context).inflate(R.layout.wardrobe_recycler,viewGroup,false);
            viewHolder=new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            viewHolder.imageView.setImageResource(photoList.get(position));
            if(type==1){
                integerList=detailList1.get(position);
            }else if(type==2){
                integerList=detailList2.get(position);
            }
            wardrobeVPAdapter=new WardrobeVPAdapter(context,integerList);
            viewPager.setAdapter(wardrobeVPAdapter);


        }

        @Override
        public int getItemCount() {
            return photoList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.wardrobeR_IV);
            }
        }
    }

    public static class WardrobeVPAdapter extends PagerAdapter {
        private List<View> viewList;
        private List<Integer> integerList;        //选中图片的各个方向照
        private Context context;
        public WardrobeVPAdapter(Context context,List<Integer> integerList){
            this.context=context;
            this.integerList=integerList;
        }
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view==o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
             View view= LayoutInflater.from(context).inflate(R.layout.wardrobe_vp,container,false);
            ImageView imageView=(ImageView)view.findViewById(R.id.wardrobeVP_IV);
            imageView.setImageResource(integerList.get(position));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }
    }
}
