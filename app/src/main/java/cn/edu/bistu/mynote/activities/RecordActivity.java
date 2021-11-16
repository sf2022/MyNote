package cn.edu.bistu.mynote.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import cn.edu.bistu.mynote.Bean.Note;
import cn.edu.bistu.mynote.R;
import cn.edu.bistu.mynote.Utils.DBUtils;
import cn.edu.bistu.mynote.Utils.ImgUtils;
import cn.edu.bistu.mynote.Utils.mySQLite;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{
    EditText title;//标题
    TextView date;//时间
    TextView author;//作者
    EditText myContent;//内容
    private ImageView img = null;//图片
    private String iname;//图片名
    private mySQLite mSQL;
    private String id;//笔记的id
    Button save;//保存
    Button insertImage;//插入图片

    //图片
    private static final int TAKE_PICTURE = 0;  //拍照
    private static final int CHOOSE_PICTURE = 1;  //从相册中选择照片
    private static final int SCALE = 5;//照片缩小比例
    private String mImageName = null;//图片文件名
    private File mImageFile;//图片文件
    private Uri mImageUri;
    private String mImagePath;//图片文件的绝对路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        //获取各组件或对象的实例
        title = (EditText)findViewById(R.id.rtitle);//标题
        author = (TextView)findViewById(R.id.rauthor);//作者
        date = (TextView)findViewById(R.id.rdate);//记事的时间
        myContent = (EditText)findViewById(R.id.rmyContent);//保存记录的内容
        img = (ImageView)findViewById(R.id.rimg); //保存的图片
        save = (Button) findViewById(R.id.save);//保存的按钮
        insertImage = (Button)findViewById(R.id.insert_image);
        /**
         * 初始化本条日记的数据
         */
        initData();
        //监听
        insertImage.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    /**
     * 初始化本条日记的数据
     */
    public void initData(){
        Intent intent = getIntent();
        if(intent!=null){
            id = intent.getStringExtra("id");
            //id为空,新建一条记录
            if(id==null){
                title.setText(null);
                author.setText(intent.getStringExtra("author"));
                date.setText(intent.getStringExtra("date"));
                myContent.setText(null);
                img.setImageBitmap(null);
            }
            //id不空，修改数据
            else {
                title.setText(intent.getStringExtra("title"));
                author.setText(intent.getStringExtra("author"));
                date.setText(intent.getStringExtra("date"));
                myContent.setText(intent.getStringExtra("myContent"));
                iname =intent.getStringExtra("myImage");
                if(iname!=null){
                    Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir().getAbsolutePath()+"/"+iname);
                    img.setImageBitmap(bitmap);
                }
            }
        }
    }

    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                fsave();
                break;
            case R.id.insert_image:
                //fimage();
                showPicturePicker(RecordActivity.this);
            default:
                break;
        }
    }

    /**
     * 保存新建或修改的日记
     */
    public void fsave(){
        mSQL = new mySQLite(this, DBUtils.DATABASE_NAME,
                null,DBUtils.DATABASE_VERION);
        Note nt=new Note();
        nt.setId(id);
        nt.setTitle(title.getText().toString().trim());
        nt.setAuthor(author.getText().toString());
        nt.setDate(DBUtils.getTime());
        nt.setMyContent(myContent.getText().toString().trim());
        nt.setMyImage(iname);
        //id不空更新日记
        if(id!=null){
            if(mSQL.updateData(nt)){
                showToast("保存成功");
                setResult(RESULT_OK);
                finish();
            }
            else {
                showToast("保存失败");
            }
        }
        //id空,新建日记
        else {
            if (mSQL.insertData(nt)){
                showToast("插入成功");
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    /**
     * 弹出Dialog选择窗口
     */
    public void showPicturePicker(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //设置弹出框标题
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener() {
            //类型码
            int REQUEST_CODE;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //根据点击的拍照或相册控件生成which,进而switch选择
                switch (which) {
                    case TAKE_PICTURE:
                        startCamera();
                        break;
                    case CHOOSE_PICTURE:
                        //发送打开相册程序器请求
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        REQUEST_CODE = CHOOSE_PICTURE;
                        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "image/*");
                        startActivityForResult(openAlbumIntent, REQUEST_CODE);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 启动相机
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startCamera() {
        Intent intent = new Intent();
        //指定动作，启动相机
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //创建文件
        createImageFile();
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //获取uri
        mImageUri = FileProvider.getUriForFile(this, "cn.edu.bistu.mynote.provider",
                mImageFile);
        //将uri加入到额外数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //启动相机并要求返回结果
        startActivityForResult(intent, TAKE_PICTURE);
    }

    /**
     * 创建图片文件
     */
    private void createImageFile(){
        //设置图片文件名（含后缀），以当前时间的毫秒值为名称
        mImageName = Calendar.getInstance().getTimeInMillis() + ".png";
        //创建图片文件
        mImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "Pictures" +"/", mImageName);
        //将图片的绝对路径设置给mImagePath
        mImagePath = mImageFile.getAbsolutePath();
        //按设置好的目录层级创建
        mImageFile.getParentFile().mkdirs();
        //不加这句会报Read-only警告。且无法写入SD
        mImageFile.setWritable(true);
    }

    /**
     * 处理返回数据，保存图片到本地并显示图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照
                case TAKE_PICTURE:
                    //将保存在SD的图片取出并缩小后显示在界面上
                    Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
                    //然后删除该文件
                    ImgUtils.delFile(mImagePath);
                    Bitmap newBitmap = ImgUtils.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上，并保存到本地
                    img.setImageBitmap(newBitmap);
                    String s=String.valueOf(System.currentTimeMillis());
                    ImgUtils.savePhotoToSDCard(newBitmap,getFilesDir().getAbsolutePath(), s);
                    //压缩后的文件名=日期+png
                    iname=s+".png";
                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImgUtils.zoomBitmap(photo,
                                    photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            //将处理过的图片显示在界面上，并保存到本地
                            img.setImageBitmap(smallBitmap);
                            String s1=String.valueOf(System.currentTimeMillis());
                            ImgUtils.savePhotoToSDCard(smallBitmap,getFilesDir().getAbsolutePath(), s1);
                            iname=s1+".png";
                        }
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void showToast(String message){
        Toast.makeText(RecordActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}
