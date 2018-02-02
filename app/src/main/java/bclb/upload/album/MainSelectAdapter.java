package bclb.upload.album;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import bclb.upload.album.model.PicCompress;
import ycpb.upload.myapplication.R;

/**
 * Created by cuiliubi on 17/3/30.
 */

public class MainSelectAdapter extends PagerAdapter {
    private List<String> imgList;
    private ArrayList<PicCompress> list;
    private PopupWindow popupWindow;

    public MainSelectAdapter(ArrayList<PicCompress> pa) {
        this.list = pa;
    }

    public MainSelectAdapter(List<String> imgList, PopupWindow popupWindow) {
        this.imgList = imgList;
        this.popupWindow = popupWindow;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (imgList != null)
            return imgList.size();
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View v = LayoutInflater.from(container.getContext()).inflate(R.layout.photoprelayout, null);
        final PhotoView photoView = (PhotoView) v.findViewById(R.id.photo_pre);
        photoView.setScaleLevels(1.0f, 1.25f, 1.5f);
        Glide.with(v.getContext()).load(imgList.get(position)).into(photoView);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }

            @Override
            public void onOutsidePhotoTap() {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        container.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
         container.removeView((View) object);
    }
}
