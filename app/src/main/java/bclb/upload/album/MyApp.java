package bclb.upload.album;

import android.app.Application;
import android.util.DisplayMetrics;

import java.util.ArrayList;

import bclb.upload.album.model.PicCompress;

/**
 * Created by cuiliubi on 17/12/18 星期四.
 */

public class MyApp extends Application {
    protected static MyApp mInstance;
    private static String path;//设置一个保存拍摄照片的地址，偷懒，直接用app 来保存了
    private ArrayList<PicCompress> piclist; //选中相册的地址 一样是偷懒。。。实际可以用sp或者干脆就rxbus 进行回调也是个选择
    private DisplayMetrics displayMetrics = null;

    public static MyApp getApp() {
        if (mInstance != null && mInstance instanceof MyApp) {
            return (MyApp) mInstance;
        } else {
            mInstance = new MyApp();
            mInstance.onCreate();
            return (MyApp) mInstance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        piclist = new ArrayList<>();


    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        MyApp.path = path;
    }

    public ArrayList<PicCompress> getPiclist() {
        return piclist;
    }

    public void setPiclist(ArrayList<PicCompress> piclist) {
        this.piclist = piclist;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }
    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

}
