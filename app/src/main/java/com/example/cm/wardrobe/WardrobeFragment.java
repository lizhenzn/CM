package com.example.cm.wardrobe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.friend.chat.ImageDetail;
import com.example.cm.util.ActionSheetDialog;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.ClothesEstimater;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    //public static List<Bitmap> integerList;    //选中的某个衣服各个图片
    //public static List<List<Bitmap>> detailList1,detailList2;   //每个衣服对应的前后左右各个图片
    public static List<String> upFileName,downFileName;
    public static List<Integer> upSeason,downSeason;
    private RecyclerView wardrobeR1,wardrobeR2;
    private WardrobeAdapter wardrobeAdapter1,wardrobeAdapter2;
    private ImageView upAdd,downAdd;
    //private static ViewPager viewPager;
    //private  static  WardrobeVPAdapter wardrobeVPAdapter;
    private static final int ALBUM_UP=1,ALBUM_DOWN=2,CAMERA_UP=3,CAMERA_DOWN=4;
    public static final int  TYPE_UP=5,TYPE_DOWN=6;
    public static final int SEASON_SPRING=7,SEASON_SUMMER=8,SEASON_FALL=9,SEASON_WINTER=10,SEASON_DEFAULT=11;
    private static File BASE_DIR=null;
    private String prevUsername;
    //1:滑动上衣 2：滑动裤子
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO 在加载这个模块就从服务器获取图片
        setToolbarText("衣柜");
        initData();
//        if(Connect.isLogined){
//            String str=MessageManager.getSharedPreferences().getString("userName",null);
//            if(str!=null) {
//                initData(str);prevUsername=str;
//            }else{
//                initData();
//                prevUsername = null;
//            }
//        }else {
//            initData();//initData
//            prevUsername = null;
//        }
        context=getActivity();
        //wardrobeVPAdapter=new WardrobeVPAdapter(context,integerList);
        wardrobeAdapter1=new WardrobeAdapter(context,photoList1,1);
        wardrobeAdapter2=new WardrobeAdapter(context,photoList2,2);
        wardrobeAdapter1.notifyDataSetChanged();
        wardrobeAdapter2.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbarText("衣柜");
        Log.d("wardrobe", "onResume: wardrobe");
        if(Connect.isLogined){
            String str=MessageManager.getSharedPreferences().getString("userName",null);
            Log.d("wardrobe", "onCreateView: username:"+str);
            if(str!=null&&(prevUsername==null||!prevUsername.equals(str))){
                initData(str);prevUsername=str;
            }
        }else if(prevUsername!=null){
            Log.d("wardrobe", "enter reset");
            initData();prevUsername=null;
        }
        wardrobeAdapter2.notifyDataSetChanged();
        wardrobeAdapter1.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case AlbumUtil.REQUEST_CAMERA:
                Log.d("wardrobe", "onRequestPermissionsResult: camera get");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.setFragmentTabHostVisibility(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=View.inflate(context, R.layout.wardrobe,null);
        //viewPager=(ViewPager)view.findViewById(R.id.wardrobeVP);
        //viewPager.setPageTransformer(true,new Transform());
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
                                else {
                                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//用来打开相机的Intent
                                    if (takePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                                        File picFile = creatImageFile(BASE_DIR, 1);
                                        upFileName.add(picFile.getName());
                                        upSeason.add(SEASON_DEFAULT);
                                        Uri imageUri = FileProvider.getUriForFile(getContext(),
                                                "com.example.cm",
                                                picFile);
                                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        startActivityForResult(takePhotoIntent, WardrobeFragment.CAMERA_UP);//启动相机
                                    } else {
                                        Toast.makeText(getContext(),
                                                "没有相机，无法完成操作",
                                                Toast.LENGTH_SHORT).show();
                                    }
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
                                else {
                                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//用来打开相机的Intent
                                    if (takePhotoIntent.
                                            resolveActivity(getActivity().getPackageManager()) != null) {//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                                        File picFile = creatImageFile(BASE_DIR, 2);
                                        downFileName.add(picFile.getName());
                                        downSeason.add(SEASON_DEFAULT);
                                        Uri imageUri = FileProvider.getUriForFile(getContext(),
                                                "com.example.cm",
                                                picFile);
                                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        startActivityForResult(takePhotoIntent, WardrobeFragment.CAMERA_DOWN);//启动相机
                                    } else {
                                        Toast.makeText(getContext(),
                                                "没有相机，无法完成操作",
                                                Toast.LENGTH_SHORT).show();
                                    }
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
        if(!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) Log.d("wardrobe", "hasSdcard: false");
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
            return;
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
                    wardrobeAdapter1.photoList.add(bitmap);
                    wardrobeAdapter1.notifyDataSetChanged();
                    upFileName.add(saveBitmap(bitmap,BASE_DIR,1));
                    Log.d("wardrobe", "onActivityResult: "+BASE_DIR);
                    upSeason.add(SEASON_DEFAULT);
                }
           }break;
            case WardrobeFragment.ALBUM_DOWN:{
                String absoluteRoad=AlbumUtil.getImageAbsolutePath(data,getContext());
                Log.d("addTest", "从相册选择_下:path="+absoluteRoad);
                if(absoluteRoad!=null) {
                    Bitmap bitmap = ClothesEstimater.getScaleBitmap(absoluteRoad);
                    wardrobeAdapter2.photoList.add(bitmap);
                    wardrobeAdapter2.notifyDataSetChanged();
                    downFileName.add(saveBitmap(bitmap,BASE_DIR,2));
                    downSeason.add(SEASON_DEFAULT);
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
                    Log.d("wardrobe", "onActivityResult: "+
                            upFileName.get(upFileName.size()-1));
                    Log.d("wardrobe", "onActivityResult: "+pic.getAbsolutePath());
                    Bitmap bitmap =ClothesEstimater.getScaleBitmap(pic.getAbsolutePath());
                    if(bitmap!=null) {
                        bitmap2File(bitmap,pic);
                        wardrobeAdapter1.photoList.add(bitmap);
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
                    Log.d("wardrobe", "onActivityResult: "+
                            downFileName.get(downFileName.size()-1));
                    Bitmap bitmap =ClothesEstimater.getScaleBitmap(pic.getAbsolutePath());
                    if(bitmap!=null){
                        bitmap2File(bitmap,pic);
                        wardrobeAdapter2.photoList.add(bitmap);
                        wardrobeAdapter2.notifyDataSetChanged();
                    }
                }
            }break;
        }
    }
    public static void initData(){
        initData("default");
    }
    public static void initData(String username){
        if(!MainActivity.isChoose_flag()) {
            MainActivity.setClothes_up(-1);
            MainActivity.setClothes_down(-1);
        }
        if(photoList2==null&&photoList1==null){
            photoList1=new ArrayList<>();
            photoList2=new ArrayList<>();
        }
        photoList1.clear();
        photoList2.clear();
//        integerList=new ArrayList<>();
//        detailList1=new ArrayList<>();
//        detailList2=new ArrayList<>();
        upFileName=new ArrayList<>();
        downFileName=new ArrayList<>();
        upSeason=new ArrayList<>();
        downSeason=new ArrayList<>();
//        @SuppressLint("ResourceType") InputStream is = MainActivity.getInstance().
//                getResources().openRawResource(R.drawable.cm);
//        Bitmap mBitmap = BitmapFactory.decodeStream(is);
//        @SuppressLint("ResourceType") InputStream is1 = MainActivity.getInstance().
//                getResources().openRawResource(R.drawable.cm);
//        Bitmap mBitmap1 = BitmapFactory.decodeStream(is1);
//        @SuppressLint("ResourceType") InputStream is2 = MainActivity.getInstance().
//                getResources().openRawResource(R.drawable.cm);
//        Bitmap mBitmap2 = BitmapFactory.decodeStream(is2);
//        @SuppressLint("ResourceType") InputStream is3 = MainActivity.getInstance().
//                getResources().openRawResource(R.drawable.cm);
//        Bitmap mBitmap3 = BitmapFactory.decodeStream(is3);
//        for(int i=0;i<66;i++) {
//
//            photoList1.add(mBitmap);
//            photoList2.add(mBitmap1);
//        }
        if(!AlbumUtil.checkStorage(MainActivity.getInstance()))
            AlbumUtil.requestStorage(MainActivity.getInstance());
        if(!AlbumUtil.checkStorage(MainActivity.getInstance())){
            Toast.makeText(MainActivity.getInstance(),"拒绝权限，无法加载衣柜内容",Toast.LENGTH_LONG).show();
            return;
        }

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            BASE_DIR=MainActivity.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            BASE_DIR = MainActivity.getInstance().getFilesDir();
            if (BASE_DIR == null) {
                Log.e("wardrobe",
                        "load wardrobe failure : get storage dir failed");
                return;
            }
        }
        BASE_DIR=new File(BASE_DIR,username);
        Log.d("wardrobe", "BASE_DIR: "+BASE_DIR);
        if(!BASE_DIR.exists())BASE_DIR.mkdirs();
        File picDir=new File(BASE_DIR,"upClothes");
        if(!picDir.exists()) {
            picDir.mkdirs();
            new File(picDir,"spring").mkdir();
            new File(picDir,"summer").mkdir();
            new File(picDir,"fall").mkdir();
            new File(picDir,"winter").mkdir();
        }
        Log.d("wardrobe", "initData: "+picDir.getAbsolutePath());
        for(File fname:picDir.listFiles()){
            if(fname.isFile()) {
                Log.d("wardrobe", "initData: "+fname.getAbsolutePath());
                Bitmap bitmap = ClothesEstimater.getScaleBitmap(fname.getAbsolutePath());
                Log.d("wardrobe", "initData: added"+photoList1.add(bitmap));

                upFileName.add(fname.getName());
                upSeason.add(SEASON_DEFAULT);
            }
            else if(fname.isDirectory()){
                int season=SEASON_DEFAULT;
                if(fname.getName().equals("spring"))season=SEASON_SPRING;
                else if(fname.getName().equals("summer"))season=SEASON_SUMMER;
                else if(fname.getName().equals("fall"))season=SEASON_FALL;
                else if(fname.getName().equals("winter"))season=SEASON_WINTER;
                for(File inner:fname.listFiles()){
                    Bitmap bitmap = ClothesEstimater.getScaleBitmap(inner.getAbsolutePath());
                    photoList1.add(bitmap);
                    upFileName.add(fname.getName()+File.separatorChar+inner.getName());
                    upSeason.add(season);
                }
            }
        }
        picDir=new File(BASE_DIR,"downClothes");
        if(!picDir.exists()) {
            picDir.mkdirs();
            new File(picDir,"spring").mkdir();
            new File(picDir,"summer").mkdir();
            new File(picDir,"fall").mkdir();
            new File(picDir,"winter").mkdir();
        }
        for(File fname:picDir.listFiles()){
            if(fname.isFile()) {
                Log.d("wardrobe", "initData: "+fname.getAbsolutePath());
                Bitmap bitmap = ClothesEstimater.getScaleBitmap(fname.getAbsolutePath());
                photoList2.add(bitmap);
                downFileName.add(fname.getName());
                downSeason.add(SEASON_DEFAULT);
            }
            else if(fname.isDirectory()){
                int season=SEASON_DEFAULT;
                if(fname.getName().equals("spring"))season=SEASON_SPRING;
                else if(fname.getName().equals("summer"))season=SEASON_SUMMER;
                else if(fname.getName().equals("fall"))season=SEASON_FALL;
                else if(fname.getName().equals("winter"))season=SEASON_WINTER;
                for(File inner:fname.listFiles()){
                    Bitmap bitmap = ClothesEstimater.getScaleBitmap(inner.getAbsolutePath());
                    photoList2.add(bitmap);
                    downFileName.add(fname.getName()+File.separatorChar+inner.getName());
                    downSeason.add(season);
                }
            }
        }

//        for(int i=0;i<photoList1.size()+10;i++){
//            detailList1.add(new ArrayList<Bitmap>(Arrays.asList(mBitmap,mBitmap1,mBitmap2,mBitmap3)));
//        }
//        for(int i=0;i<photoList2.size()+10;i++){
//            detailList2.add(new ArrayList<Bitmap>(Arrays.asList(mBitmap3,mBitmap2,mBitmap1,mBitmap)));
//
//        }

    }


//    class Transform implements ViewPager.PageTransformer{
//        public  float MIN_ALPHA = 0.5f;
//        public  float MIN_SCALE = 0.8f;
//        @Override
//        public void transformPage(@NonNull View view, float position)  {
//            if (position < -1 || position > 1) {
//                viewPager.setAlpha(MIN_ALPHA);
//                viewPager.setScaleX(MIN_SCALE);
//                viewPager.setScaleY(MIN_SCALE);
//                Log.i("info", "缩放：position < -1 || position > 1");
//            } else if (position <= 1) { // [-1,1]
//                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
//                if (position < 0) {
//                    float scaleX = 1 + 0.2f * position;
//                    Log.i("info", "缩放：position < 0");
//                    viewPager.setScaleX(scaleX);
//                    viewPager.setScaleY(scaleX);
//                } else {
//                    float scaleX = 1 - 0.2f * position;
//                    viewPager.setScaleX(scaleX);
//                    viewPager.setScaleY(scaleX);
//                    Log.i("info", "缩放：position <= 1 >=0");
//                }
//                viewPager.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
//            }
//        }
//
//
//    }

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
            View view= LayoutInflater.from(context).inflate(R.layout.wardrobe_recycler,
                    viewGroup,
                    false);
            viewHolder=new ViewHolder(view);
            ViewHolder finalViewHolder = viewHolder;
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position= finalViewHolder.getAdapterPosition();
                    if(MainActivity.isChoose_flag()){
                        if(type==1){  //上衣
                            MainActivity.setClothes_up(position);
                        }else{    //下衣
                            MainActivity.setClothes_down(position);
                        }
                    }else{
                        String path=BASE_DIR.getAbsolutePath();
                        if(type==1){
                            path+=File.separatorChar+"upClothes"+
                                    File.separatorChar+upFileName.get(position);
                        }else if(type==2) {
                            path += File.separatorChar + "downClothes" +
                                    File.separatorChar + downFileName.get(position);
                        }
                        Intent intent=new Intent(context, ImageDetail.class);
                        intent.putExtra("bitmapPath",path);
                        context.startActivity(intent);
                    }

                }
            });
            viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(MainActivity.isChoose_flag()){
                        int pos = finalViewHolder.getAdapterPosition();
                        String path=BASE_DIR.getAbsolutePath();
                        if(type==1){
                            path+=File.separatorChar+"upClothes"+
                                    File.separatorChar+upFileName.get(pos);
                        }else if(type==2) {
                            path += File.separatorChar + "downClothes" +
                                    File.separatorChar + downFileName.get(pos);
                        }
                        Intent intent=new Intent(context, ImageDetail.class);
                        intent.putExtra("bitmapPath",path);
                        context.startActivity(intent);
                    }else {
                        new ActionSheetDialog(context).
                                builder()
                                .setCancelable(true)
                                .setCanceledOnTouchOutside(true)
                                .addSheetItem("标为春季服装",
                                        ActionSheetDialog.SheetItemColor.Blue
                                        , new ActionSheetDialog.OnSheetItemClickListener() {
                                            @Override
                                            public void onClick(int which) {
                                                int pos = finalViewHolder.getAdapterPosition();
                                                File prev = null, target = null;
                                                if (type == 1) {
                                                    prev = new File(BASE_DIR, "upClothes"
                                                            + File.separatorChar + upFileName.get(pos));
                                                    target = new File(BASE_DIR, "upClothes");
                                                } else if (type == 2) {
                                                    prev = new File(BASE_DIR, "downClothes"
                                                            + File.separatorChar + downFileName.get(pos));
                                                    target = new File(BASE_DIR, "downClothes");
                                                }
                                                if (prev != null && target != null) {
                                                    target = new File(target, "spring"
                                                            + File.separatorChar
                                                            + prev.getName());
                                                    if (prev.renameTo(target) == false)
                                                        Log.d("wardrobe", "onClick: movefailed");
                                                    else if (type == 1) {
                                                        upSeason.set(pos, SEASON_SPRING);
                                                        upFileName.set(pos, "spring" + File.separatorChar + target.getName());
                                                    } else if (type == 2) {
                                                        downSeason.set(pos, SEASON_SPRING);
                                                        downFileName.set(pos, "spring" + File.separatorChar + target.getName());
                                                    }
                                                }
                                            }
                                        })
                                .addSheetItem("标为夏季服装",
                                        ActionSheetDialog.SheetItemColor.Blue
                                        , new ActionSheetDialog.OnSheetItemClickListener() {
                                            @Override
                                            public void onClick(int which) {
                                                int pos = finalViewHolder.getAdapterPosition();
                                                File prev = null, target = null;
                                                if (type == 1) {
                                                    prev = new File(BASE_DIR, "upClothes"
                                                            + File.separatorChar + upFileName.get(pos));
                                                    target = new File(BASE_DIR, "upClothes");
                                                } else if (type == 2) {
                                                    prev = new File(BASE_DIR, "downClothes"
                                                            + File.separatorChar + downFileName.get(pos));
                                                    target = new File(BASE_DIR, "downClothes");
                                                }
                                                if (prev != null && target != null) {
                                                    target = new File(target, "summer"
                                                            + File.separatorChar
                                                            + prev.getName());
                                                    if (prev.renameTo(target) == false)
                                                        Log.d("wardrobe", "onClick: movefailed");
                                                    else if (type == 1) {
                                                        upSeason.set(pos, SEASON_SUMMER);
                                                        upFileName.set(pos, "summer" + File.separatorChar + target.getName());
                                                    } else if (type == 2) {
                                                        downSeason.set(pos, SEASON_SUMMER);
                                                        downFileName.set(pos, "summer" + File.separatorChar + target.getName());
                                                    }
                                                }
                                            }
                                        })
                                .addSheetItem("标为秋季服装",
                                        ActionSheetDialog.SheetItemColor.Blue
                                        , new ActionSheetDialog.OnSheetItemClickListener() {
                                            @Override
                                            public void onClick(int which) {
                                                int pos = finalViewHolder.getAdapterPosition();
                                                File prev = null, target = null;
                                                if (type == 1) {
                                                    prev = new File(BASE_DIR, "upClothes"
                                                            + File.separatorChar + upFileName.get(pos));
                                                    target = new File(BASE_DIR, "upClothes");
                                                } else if (type == 2) {
                                                    prev = new File(BASE_DIR, "downClothes"
                                                            + File.separatorChar + downFileName.get(pos));
                                                    target = new File(BASE_DIR, "downClothes");
                                                }
                                                if (prev != null && target != null) {
                                                    target = new File(target, "fall"
                                                            + File.separatorChar
                                                            + prev.getName());
                                                    if (prev.renameTo(target) == false)
                                                        Log.d("wardrobe", "onClick: movefailed");
                                                    else if (type == 1) {
                                                        upSeason.set(pos, SEASON_FALL);
                                                        upFileName.set(pos, "fall" + File.separatorChar + target.getName());
                                                    } else if (type == 2) {
                                                        downSeason.set(pos, SEASON_FALL);
                                                        downFileName.set(pos, "fall" + File.separatorChar + target.getName());
                                                    }
                                                }
                                            }
                                        })
                                .addSheetItem("标为冬季服装",
                                        ActionSheetDialog.SheetItemColor.Blue
                                        , new ActionSheetDialog.OnSheetItemClickListener() {
                                            @Override
                                            public void onClick(int which) {
                                                int pos = finalViewHolder.getAdapterPosition();
                                                File prev = null, target = null;
                                                if (type == 1) {
                                                    prev = new File(BASE_DIR, "upClothes"
                                                            + File.separatorChar + upFileName.get(pos));
                                                    target = new File(BASE_DIR, "upClothes");
                                                } else if (type == 2) {
                                                    prev = new File(BASE_DIR, "downClothes"
                                                            + File.separatorChar + downFileName.get(pos));
                                                    target = new File(BASE_DIR, "downClothes");
                                                }
                                                if (prev != null && target != null) {
                                                    target = new File(target, "winter"
                                                            + File.separatorChar
                                                            + prev.getName());
                                                    if (prev.renameTo(target) == false)
                                                        Log.d("wardrobe", "onClick: movefailed");
                                                    else if (type == 1) {
                                                        upSeason.set(pos, SEASON_WINTER);
                                                        upFileName.set(pos, "winter" + File.separatorChar + target.getName());

                                                    } else if (type == 2) {
                                                        downSeason.set(pos, SEASON_WINTER);
                                                        downFileName.set(pos, "winter" + File.separatorChar + target.getName());
                                                    }
                                                }
                                            }
                                        })
                                .addSheetItem("删除",
                                        ActionSheetDialog.SheetItemColor.Red
                                        , new ActionSheetDialog.OnSheetItemClickListener() {
                                            @Override
                                            public void onClick(int which) {
                                                MainActivity.setClothes_down(-1);
                                                MainActivity.setClothes_up(-1);
                                                int pos = finalViewHolder.getAdapterPosition();
                                                if (type == 1) {
                                                    photoList1.remove(pos);
                                                    File f = new File(BASE_DIR,
                                                            "upClothes" +
                                                                    File.separatorChar +
                                                                    upFileName.get(pos));
                                                    if (f.exists()) {
                                                        f.delete();
                                                        upFileName.remove(pos);
                                                        upSeason.remove(pos);
                                                    }
                                                    notifyDataSetChanged();
                                                } else if (type == 2) {
                                                    photoList2.remove(pos);
                                                    File f = new File(BASE_DIR,
                                                            "downClothes" +
                                                                    File.separatorChar +
                                                                    downFileName.get(pos));
                                                    if (f.exists()) {
                                                        f.delete();
                                                        downFileName.remove(pos);
                                                        downSeason.remove(pos);
                                                    }
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        }).show();
                    }
                    return false;
                     }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            viewHolder.imageView.setImageBitmap(photoList.get(position));
//            if(type==1){
//                integerList=detailList1.get(position);
//            }else if(type==2){
//                integerList=detailList2.get(position);
//            }
            //wardrobeVPAdapter=new WardrobeVPAdapter(context,integerList);
            //viewPager.setAdapter(wardrobeVPAdapter);
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

//    public static class WardrobeVPAdapter extends PagerAdapter {
//        private List<View> viewList;
//        private List<Bitmap> integerList;        //选中图片的各个方向照
//        private Context context;
//        public WardrobeVPAdapter(Context context,List<Bitmap> integerList){
//            this.context=context;
//            this.integerList=integerList;
//        }
//        @Override
//        public int getCount() {
//            return 4;
//        }
//
//        @Override
//        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
//            return view==o;
//        }
//
//        @NonNull
//        @Override
//        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//             View view= LayoutInflater.from(context).inflate(R.layout.wardrobe_vp,container,false);
//            ImageView imageView=(ImageView)view.findViewById(R.id.wardrobeVP_IV);
//            imageView.setImageBitmap(integerList.get(position));
//            container.addView(view);
//            return view;
//        }
//
//        @Override
//        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            container.removeView((View)object);
//        }
//    }
    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     * @param type 为1表示上衣，为2表示下衣
     */
    public static String saveBitmap(Bitmap bitmap,File baseDir,int type) {
        File filePic=creatImageFile(baseDir,type);
        bitmap2File(bitmap,filePic);
        Log.i("tag", "saveBitmap success: " + filePic.getParentFile().getPath());
        return filePic.getName();
    }

    /**
     * 抽离实现，负责将一个给定的bitmap保存进给定文件里
     * @param bitmap 给定的bitmap
     * @param filePic 给定的文件
     * @return 成功与否，成功为true，失败为false
     */
    public static boolean bitmap2File(Bitmap bitmap,File filePic){
        try {
            Log.d("wardrobe", "saveBitmap: savePath"+filePic.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            return false;
        }
        return true;
    }
    public static File creatImageFile(File baseDir,int type){
        String savePath=null;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
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
    /**
     * 给定偏移量和类型取得绝对路径
     * @param pos 偏移量
     * @param type 对应静态常量TYPE_UP、TYPE_DOWN
     * @return 绝对路径,出错则返回null
     */
    public static String getImgRealPath(int pos,int type,Context context){
        File baseDir=BASE_DIR;
        File imgDir=null;
//        if(hasSdcard())baseDir=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        else baseDir=context.getFilesDir();
        baseDir=BASE_DIR;
        if(type==TYPE_UP) {
            imgDir = new File(baseDir, "upClothes"+
                    File.separatorChar+
                    upFileName.get(pos));
        }
        else if(type==TYPE_DOWN) {
            imgDir = new File(baseDir, "downClothes"+
                    File.separatorChar+
                            downFileName.get(pos));
        }
        if(imgDir!=null){
            Log.d("wardrobe", "getImgRealPath: "+imgDir.getAbsolutePath());
            return imgDir.getAbsolutePath();
        }
        else return null;
    }
}
