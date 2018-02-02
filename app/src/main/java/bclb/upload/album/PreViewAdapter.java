package bclb.upload.album;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bclb.upload.album.albumlcc.adapter.DisplayImageViewAdapter;
import uk.co.senab.photoview.PhotoView;
import ycpb.upload.myapplication.R;

/**
 * Created by cuiliubi on 18/1/31 星期四.
 */

class PreViewAdapter extends PagerAdapter {
    private List<String> imgList;
    private Context context;

    public PreViewAdapter(List<String> imgList, Context context) {
        this.imgList = imgList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View v = LayoutInflater.from(container.getContext()).inflate(R.layout.photoprelayout, null);
        PhotoView photoView = (PhotoView) v.findViewById(R.id.photo_pre);
        photoView.setScaleLevels(1.0f, 1.25f, 1.5f);//设置缩放系数
        container.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mDisplayAdapter != null) {
            mDisplayAdapter.onDisplayImage(context, photoView, imgList.get(position));
        }
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private DisplayImageViewAdapter<String> mDisplayAdapter;

    public void setOnDisplayImageAdapter(DisplayImageViewAdapter<String> adapter) {
        this.mDisplayAdapter = adapter;
    }
}