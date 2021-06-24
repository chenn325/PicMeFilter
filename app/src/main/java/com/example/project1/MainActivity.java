package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import jp.co.cyberagent.android.gpuimage.*;

public class MainActivity extends AppCompatActivity {


    private static GPUImageFilter filter;

    //饱和度、亮度等参数指数
    private static int count;
    private final static int PHOTO = 2;
    /**
     * 获取过滤器
     * @param GPUFlag
     * @return 滤镜类型
     **/
    private GPUImage gpuImage;
    private Bitmap finalBitmap = null;
    private Bitmap newBitmap = finalBitmap;
    //显示处理结果
    private ImageView resultIv;
    private ImageView imageView1;
    private Button bt1,bt2,bt3,bt4,bt5,bt6,btOpen,btSave;
    private TextView text1;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt1 = (Button)findViewById(R.id.bt1);
        bt2 = (Button)findViewById(R.id.bt2);
        bt3 = (Button)findViewById(R.id.bt3);
        bt4 = (Button)findViewById(R.id.bt4);
        bt5 = (Button)findViewById(R.id.bt5);
        bt6 = (Button)findViewById(R.id.bt6);
        btOpen = (Button)findViewById(R.id.btOpen);
        btSave = (Button)findViewById(R.id.btSave);
        text1 = (TextView)findViewById(R.id.text1);
        resultIv = (ImageView)findViewById(R.id.resultIv);
        // imageView1.setImageResource(R.drawable.cat);


        bt1.setOnClickListener(listener);
        bt2.setOnClickListener(listener);
        bt3.setOnClickListener(listener);
        bt4.setOnClickListener(listener);
        bt5.setOnClickListener(listener);
        bt6.setOnClickListener(listener);
        btOpen.setOnClickListener(listener);
        btSave.setOnClickListener(listener);

        MainActivity.context = getApplicationContext();
    }

    private Button.OnClickListener listener=(v)->{

        resultIv = (ImageView) findViewById(R.id.resultIv);

        switch(v.getId()){
            case R.id.btOpen:
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PHOTO);
                break;
            case R.id.btSave:
                saveImage(newBitmap);

                break;
        }
        Bitmap bitmap = finalBitmap;
        gpuImage = new GPUImage(this);
        gpuImage.setImage(bitmap);

        if(bitmap != null){
            switch(v.getId()){
                case R.id.bt1:
                    gpuImage.setFilter(getFilter(1));;
                    changeImage(bitmap);
                    break;
                case R.id.bt2:
                    gpuImage.setFilter(getFilter(2));;
                    changeImage(bitmap);
                    break;
                case R.id.bt3:
                    gpuImage.setFilter(getFilter(3));;
                    changeImage(bitmap);
                    break;
                case R.id.bt4:
                    gpuImage.setFilter(getFilter(4));;
                    changeImage(bitmap);
                    break;
                case R.id.bt5:
                    gpuImage.setFilter(getFilter(5));;
                    changeImage(bitmap);
                    break;
                case R.id.bt6:
                    gpuImage.setFilter(getFilter(6));;
                    changeImage(bitmap);
                    break;
            }

        }
    };

    private void changeImage(Bitmap bitmap){
        bitmap = gpuImage.getBitmapWithFilterApplied();
        newBitmap = bitmap;
        //显示处理后的图片
        resultIv.setImageBitmap(newBitmap);
    }

    public static void saveImage(Bitmap bm){

        //String path = Environment.getExternalStorageDirectory().toString();
        String path = "/sdcard/DCIM";
        File appDir = new File(path+"/PicMe");
        if(!appDir.exists()){
            appDir.mkdir();
        }
        //debug.setText(appDir.getPath());
        String fileName = System.currentTimeMillis()+".jpg";
        File file = new File(appDir, fileName);
        try{
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast toast = Toast.makeText(context, "已儲存", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }catch(FileNotFoundException e){
            Toast toast = Toast.makeText(context, "FileNotFound", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            e.printStackTrace();
        }catch(IOException e){
            Toast toast = Toast.makeText(context, "IOException", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            e.printStackTrace();
        }

        //檔案插入到系統圖庫
        try{
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        }catch(FileNotFoundException e){
            Toast toast = Toast.makeText(context, "MediaStoreFileNotFound", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            e.printStackTrace();
        }

        //儲存圖片後傳送廣播通知更新資料庫
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("File://"+path+"/PicMe")));

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO && resultCode == RESULT_OK && null != data) {
//获取返回的数据，这里是android自定义的Uri地址
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//获取选择照片的数据视图
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
//从数据视图中获取已选择图片的路径
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
//将图片显示到界面上
            ImageView imageView = (ImageView) findViewById(R.id.resultIv);
            finalBitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    public static GPUImageFilter getFilter(int GPUFlag){
        switch (GPUFlag){
            case 1:
                filter = new GPUImageGrayscaleFilter();
                break;
            case 2:
                filter = new GPUImageAddBlendFilter();
                break;
            case 3:
                filter = new GPUImageAlphaBlendFilter();
                break;
            case 4:
                filter = new GPUImageBulgeDistortionFilter();
                break;
            case 5:
                filter = new GPUImageCGAColorspaceFilter();
                break;
            case 6:
                filter = new GPUImageSaturationFilter(count);
                break;
        }
        return filter;
    }

}