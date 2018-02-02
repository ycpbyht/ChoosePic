package bclb.upload.album.albumlcc.adapter;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

/**
 *
 * 保存相册列表中item的（imageview的）几个状态
 */
public abstract class DisplayImageViewAdapter<T> {
    public abstract void onDisplayImage(Context context, ImageView imageView, T t);

    public void onItemImageClick(Context context, int index, List<T> list) {

    }
    public void onItemClick(Context context, int index, String path) {

    }
//    被选中／取消选中的点击
    public void onImageCheckL(String path, boolean isChecked) {

    }



}