package cn.edu.bistu.mynote.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;


/**
 * Tools for handler picture
 *
 *
 */
public final class ImgUtils {

    /**
     * 缩小图片
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * 将图片保存到内部存储
     * @param photoBitmap
     * @param photoName
     * @param path
     */
    public static void savePhotoToSDCard(Bitmap photoBitmap,String path,String photoName){
        if (true) {
            File dir = new File(path);
            //目录不存在则新建
            if (!dir.exists()){
                dir.mkdirs();
            }
            File photoFile = new File(path , photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            }
            catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            }
            catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            }
            finally{
                try {
                    fileOutputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 删除文件
     * @param filePath 指定的路径
     */
    public static void delFile(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            file.delete();
        }
        catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
        }
    }
}