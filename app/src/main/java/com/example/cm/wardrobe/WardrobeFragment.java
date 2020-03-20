package com.example.cm.wardrobe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.util.ActionSheetDialog;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.ClothesEstimater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.cm.MainActivity.setToolbarText;

public class WardrobeFragment extends Fragment  {
    private Context context;
    private View view;
    //private List<Integer> photoList1,photoList2; //展示的上衣、裤子和上面展示的的详情  R.drawable.cm资源为Integer类型
    //private static List<Integer> integerList;    //
    //private static List<List<Integer>> detailList1,detailList2;   //每个衣服对应的前后左右各个图片
    public static List<Bitmap> photoList1,photoList2; //展示的上衣、裤子和上面展示的的详情  R.drawable.cm资源为Integer类型
    public static List<Bitmap> integerList;    //选中的某个衣服各个图片
    public static List<List<Bitmap>> detailList1,detailList2;   //每个衣服对应的前后左右各个图片
    public static List<String> upFileName,downFileName;
    private RecyclerView wardrobeR1,wardrobeR2;
    private WardrobeAdapter wardrobeAdapter1,wardrobeAdapter2;
    private LinearLayout layout_up,layout_down,layout_up_control,layout_down_control;
    private ImageView upAdd,downAdd;
    private static ViewPager viewPager;
    private  static  WardrobeVPAdapter wardrobeVPAdapter;
    private boolean upClothes,downClothes;
    private static final int ALBUM_UP=1,ALBUM_DOWN=2,CAMERA_UP=3,CAMERA_DOWN=4;
    private static File BASE_DIR=null;
    //1:滑动上衣 2：滑动裤子
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO 在加载这个模块就从服务器获取图片
        setToolbarText("衣柜");
        initData();//initData
        context=getActivity();
        upClothes=true;downClothes=false;
        wardrobeVPAdapter=new WardrobeVPAdapter(context,integerList);
        wardrobeAdapter1=new WardrobeAdapter(context,photoList1,1);
        wardrobeAdapter2=new WardrobeAdapter(context,photoList2,2);
    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbarText("衣柜");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case AlbumUtil.REQUEST_CAMERA:

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=View.inflate(context, R.layout.wardrobe,null);
        layout_up=view.findViewById(R.id.wardrobeUpLayout);
        if(!upClothes)layout_up.setVisibility(View.GONE);
        layout_down=view.findViewById(R.id.wardrobeDownLayout);
        if(!downClothes)layout_down.setVisibility(View.GONE);
        layout_up_control=view.findViewById(R.id.wardrobeUpControl);//控制上选单伸缩
        layout_down_control=view.findViewById(R.id.wardrobeDownControl);//控制下选单伸缩
        viewPager=(ViewPager)view.findViewById(R.id.wardrobeVP);
        viewPager.setPageTransformer(true,new Transform());
        //viewPager.setAdapter(wardrobeVPAdapter);
        wardrobeR1=(RecyclerView)view.findViewById(R.id.wardrobeR1);
        wardrobeR2=(RecyclerView)view.findViewById(R.id.wardrobeR2);
        upAdd=view.findViewById(R.id.wardrobeUpAdd);
        downAdd=view.findViewById(R.id.wardrobeDownAdd);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(context);
        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        wardrobeR1.setLayoutManager(linearLayoutManager1);
        wardrobeR2.setLayoutManager(linearLayoutManager2);
        wardrobeR1.setAdapter(wardrobeAdapter1);
        wardrobeR2.setAdapter(wardrobeAdapter2);
        layout_down_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downClothes)
                {layout_down.setVisibility(View.GONE);downClothes=false;}
                else
                {layout_down.setVisibility(View.VISIBLE);downClothes=true;}
            }
        });
        layout_up_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_up.setVisibility(View.GONE);
                if(upClothes)
                {layout_up.setVisibility(View.GONE);upClothes=false;}
                else
                {layout_up.setVisibility(View.VISIBLE);upClothes=true;}
            }
        });
        upAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ActionSheetDialog(getContext())
                        .builder()
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(true)
                        .setTitle("添加上衣")
                        .addSheetItem("从相册选择", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                if (AlbumUtil.checkStorage(getContext())) {
                                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                                    intent.setType("image/*");
                                    startActivityForResult(intent,WardrobeFragment.ALBUM_UP);
                                } else {
                                    AlbumUtil.requestStorage(getContext());
                                    //Toast.makeText(ChatActivity.this,"You denied the permission",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addSheetItem("点击拍照", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Log.d("addTest", "点击拍照——上");
                                if(!AlbumUtil.checkCamera(getContext()))
                                    AlbumUtil.requestCamera(getContext());
                                if(!AlbumUtil.checkCamera(getContext())){
                                    Toast.makeText(getContext(),
                                            "拒绝相机权限，拍照失败",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//用来打开相机的Intent
                                if(takePhotoIntent.resolveActivity(getActivity().getPackageManager())!=null){//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                                    File picFile=creatImageFile(BASE_DIR,1);
                                    upFileName.add(picFile.getName());
                                    Uri imageUri=FileProvider.getUriForFile(getContext(),
                                            "com.example.cm",
                                            picFile);
                                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                    startActivityForResult(takePhotoIntent,WardrobeFragment.CAMERA_UP);//启动相机
                                }else{
                                    Toast.makeText(getContext(),
                                            "没有相机，无法完成操作",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).show();
            }
        });
        downAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(getContext())
                        .builder()
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(true)
                        .setTitle("添加下衣")
                        .addSheetItem("从相册选择", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                if (AlbumUtil.checkStorage(getContext())) {
                                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                                    intent.setType("image/*");
                                    startActivityForResult(intent,WardrobeFragment.ALBUM_DOWN);
                                } else {
                                    AlbumUtil.requestStorage(getContext());
                                    //Toast.makeText(ChatActivity.this,"You denied the permission",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addSheetItem("点击拍照", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                if(!AlbumUtil.checkCamera(getContext()))
                                    AlbumUtil.requestCamera(getContext());
                                if(!AlbumUtil.checkCamera(getContext())){
                                    Toast.makeText(getContext(),
                                            "拒绝相机权限，拍照失败",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//用来打开相机的Intent
                                if(takePhotoIntent.resolveActivity(getActivity().getPackageManager())!=null){//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                                    File picFile=creatImageFile(BASE_DIR,2);
                                    downFileName.add(picFile.getName());
                                    Uri imageUri=FileProvider.getUriForFile(getContext(),
                                            "com.example.cm",
                                            picFile);
                                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                    startActivityForResult(takePhotoIntent,WardrobeFragment.CAMERA_DOWN);//启动相机
                                }else{
                                    Toast.makeText(getContext(),"没有相机，无法完成操作",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
            }
        });
        return  view;
    }
    /**
     * 判断sdcard是否被挂载
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
    /**
     *  此处负责处理相机和图库的调用返回问题
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!AlbumUtil.checkStorage(getContext()))AlbumUtil.requestStorage(getContext());
        if(!AlbumUtil.checkStorage(getContext())){
            Toast.makeText(getContext(),"拒绝权限，无法加入衣柜",Toast.LENGTH_LONG).show();
        }
        File baseDir=null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            baseDir=getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            Log.e("wardrobe", "saveBitmap failure : sdcard not mounted");
            return;
        }
        switch (requestCode){
            case WardrobeFragment.ALBUM_UP:{
                String absoluteRoad=AlbumUtil.getImageAbsolutePath(data,getContext());
                Log.d("addTest", "从相册选择_上:path="+absoluteRoad);
                if(absoluteRoad!=null) {
                    Bitmap bitmap = ClothesEstimater.getScaleBitmap(absoluteRoad);
                    WardrobeFragment.photoList1.add(bitmap);
                    wardrobeAdapter1.notifyDataSetChanged();
                    upFileName.add(saveBitmap(bitmap,baseDir,1));
                }
           }break;
            case WardrobeFragment.ALBUM_DOWN:{
                String absoluteRoad=AlbumUtil.getImageAbsolutePath(data,getContext());
                Log.d("addTest", "从相册选择_下:path="+absoluteRoad);
                if(absoluteRoad!=null) {
                    Bitmap bitmap = ClothesEstimater.getScaleBitmap(absoluteRoad);
                    WardrobeFragment.photoList2.add(bitmap);
                    wardrobeAdapter2.notifyDataSetChanged();
                    downFileName.add(saveBitmap(bitmap,baseDir,2));
                }
           }break;
            case WardrobeFragment.CAMERA_UP:{
                Log.d("addTest", "点击拍照——上");
                if(resultCode==RESULT_OK){
                    /*缩略图信息是储存在返回的intent中的Bundle中的，
                     * 对应Bundle中的键为data，因此从Intent中取出
                     * Bundle再根据data取出来Bitmap即可*/
                    File pic=new File(BASE_DIR,"upClothes"+
                            File.separatorChar+
                            upFileName.get(upFileName.size()-1));
                    Log.d("wardrobe", "onActivityResult: "+upFileName.get(upFileName.size()-1));
                    Bitmap bitmap =ClothesEstimater.getScaleBitmap(pic.getAbsolutePath());
                    if(bitmap!=null) {
                        WardrobeFragment.photoList1.add(bitmap);
                        wardrobeAdapter1.notifyDataSetChanged();
                    }
                }

            }break;
            case WardrobeFragment.CAMERA_DOWN:{
                Log.d("addTest", "点击拍照——下");
                if(resultCode==RESULT_OK){
                    File pic=new File(BASE_DIR,"downClothes"+
                            File.separatorChar+
                            downFileName.get(downFileName.size()-1));
                    Log.d("wardrobe", "onActivityResult: "+downFileName.get(downFileName.size()-1));
                    Bitmap bitmap =ClothesEstimater.getScaleBitmap(pic.getAbsolutePath());
                    if(bitmap!=null) {
                        WardrobeFragment.photoList2.add(bitmap);
                        wardrobeAdapter2.notifyDataSetChanged();
                    }
                }
            }break;
        }
    }

    public void initData(){
        photoList1=new ArrayList<>();
        photoList2=new ArrayList<>();
        integerList=new ArrayList<>();
        detailList1=new ArrayList<>();
        detailList2=new ArrayList<>();
        upFileName=new ArrayList<>();
        downFileName=new ArrayList<>();
        @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(R.drawable.cm);
        Bitmap mBitmap = BitmapFactory.decodeStream(is);
        @SuppressLint("ResourceType") InputStream is1 = getResources().openRawResource(R.drawable.unlogin);
        Bitmap mBitmap1 = BitmapFactory.decodeStream(is1);
        @SuppressLint("ResourceType") InputStream is2 = getResources().openRawResource(R.drawable.match1);
        Bitmap mBitmap2 = BitmapFactory.decodeStream(is2);
        @SuppressLint("ResourceType") InputStream is3 = getResources().openRawResource(R.drawable.wardrobe1);
        Bitmap mBitmap3 = BitmapFactory.decodeStream(is3);
//        for(int i=0;i<66;i++) {
//
//            photoList1.add(mBitmap);
//            photoList2.add(mBitmap1);
//        }
        if(!AlbumUtil.checkStorage(getContext()))AlbumUtil.requestStorage(getContext());
        if(!AlbumUtil.checkStorage(getContext())){
            Toast.makeText(getContext(),"拒绝权限，无法加载衣柜内容",Toast.LENGTH_LONG).show();
        }

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            BASE_DIR=getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            BASE_DIR=getActivity().getFilesDir();
            if(BASE_DIR==null)Log.e("wardrobe",
                    "load wardrobe failure : get storage dir failed");
        }
        File picDir=new File(BASE_DIR,"upClothes");
        if(!picDir.exists())picDir.mkdirs();
        Log.d("wardrobe", "initData: "+picDir.getAbsolutePath());
        for(String fname:picDir.list()){
            Bitmap bitmap=ClothesEstimater.getScaleBitmap(picDir.getAbsolutePath()
                    +File.separator
                    +fname);
            photoList1.add(bitmap);
            upFileName.add(fname);
        }
        picDir=new File(BASE_DIR,"downClothes");
        if(!picDir.exists())picDir.mkdirs();
        for(String fname:picDir.list()){
            Bitmap bitmap=ClothesEstimater.getScaleBitmap(picDir.getAbsolutePath()
                    +File.separator
                    +fname);
            photoList2.add(bitmap);
            downFileName.add(fname);
        }
        for(int i=0;i<photoList1.size()+10;i++){
            detailList1.add(new ArrayList<Bitmap>(Arrays.asList(mBitmap,mBitmap1,mBitmap2,mBitmap3)));
        }
        for(int i=0;i<photoList2.size()+10;i++){
            detailList2.add(new ArrayList<Bitmap>(Arrays.asList(mBitmap3,mBitmap2,mBitmap1,mBitmap)));
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
        private List<Bitmap> photoList;
        private  Context context;
        int type;
        public WardrobeAdapter(Context context,List<Bitmap> photoList,int type){
            this.context=context;
            this.photoList=photoList;
            this.type=type;
        }
        public static interface OnItemClickListener {
            void onItemClick(View view);
            void onItemLongClick(View view);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ViewHolder viewHolder=null;
            View view= LayoutInflater.from(context).inflate(R.layout.wardrobe_recycler,viewGroup,false);
            viewHolder=new ViewHolder(view);
            ViewHolder finalViewHolder = viewHolder;
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position= finalViewHolder.getAdapterPosition();

                    //if(MainActivity.isChoose_flag()){
                        if(type==1){  //上衣
                            MainActivity.setClothes_up(position);
                            Log.d("RecycleList:", "onClick: "+upFileName.get(position));
                        }else{    //下衣
                            MainActivity.setClothes_down(position);
                            Log.d("RecycleList:", "onClick: "+downFileName.get(position));
                        }
                   // }

                }
            });
            viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new ActionSheetDialog(context).
                            builder()
                            .setCancelable(true)
                            .setCanceledOnTouchOutside(true)
                            .addSheetItem("删除",
                                    ActionSheetDialog.SheetItemColor.Blue
                                    , new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            MainActivity.setClothes_down(-1);
                                            MainActivity.setClothes_up(-1);
                                            int pos=finalViewHolder.getAdapterPosition();
                                            if(type==1){
                                                photoList1.remove(pos);
                                                File f=new File(BASE_DIR,
                                                        "upClothes"+
                                                                File.separatorChar+
                                                                upFileName.get(pos));
                                                if (f.exists())f.delete();
                                                upFileName.remove(pos);
                                                notifyDataSetChanged();
                                            }else if(type==2){
                                                photoList2.remove(pos);
                                                File f=new File(BASE_DIR,
                                                        "downClothes"+
                                                                File.separatorChar+
                                                                downFileName.get(pos));
                                                if (f.exists())f.delete();
                                                downFileName.remove(pos);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    }).show();
                    return false;
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            viewHolder.imageView.setImageBitmap(photoList.get(position));
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

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
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
        private List<Bitmap> integerList;        //选中图片的各个方向照
        private Context context;
        public WardrobeVPAdapter(Context context,List<Bitmap> integerList){
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
            imageView.setImageBitmap(integerList.get(position));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }
    }
    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     * @param type 为1表示上衣，为2表示下衣
     */
    public static String saveBitmap(Bitmap bitmap,File baseDir,int type) {
        File filePic=creatImageFile(baseDir,type);
        try {
            Log.d("wardrobe", "saveBitmap: savePath"+filePic.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            return null;
        }
        Log.i("tag", "saveBitmap success: " + filePic.getParentFile().getPath());
        return filePic.getName();
    }
    public static File creatImageFile(File baseDir,int type){
        String savePath=null;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        if(type==1)savePath="upClothes/";
        else if(type==2)savePath="downClothes/";
        else {
            Log.d("wardrobe", "saveBitmap: error_type");return null;}
        savePath+=format.format(new Date())+".jpg";
        File filePic= new File(baseDir,savePath);
        if (!filePic.exists()) {
            filePic.getParentFile().mkdirs();
            try {
                filePic.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePic;
    }
}
