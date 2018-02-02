package bclb.upload.album.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import bclb.upload.album.albumlcc.bean.DisplayInfoBean;
import bclb.upload.album.albumlcc.bean.Photobean;

/**
 * Created by cuiliubi on 18/1/12 星期四.
 * 解决点击选中，需要刷新数据时候的全局刷新导致的recycleview闪
 */

public class DiffPhotoPickCallback extends DiffUtil.Callback {
    protected ArrayList<Photobean> newPic, oldPic;
    protected List<DisplayInfoBean> newVideo, oldVideo;
    private int isType = 0; // 0图片，1 视频，暂时没有加入视频，貌似视频也不需要，因为点击以后就回传了，先放着吧暂时

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
//        棒极了的操作，自己也在边学边做，参考自
//        http://blog.csdn.net/zxt0601/article/details/52562770
        Photobean oldBean = oldPic.get(oldItemPosition);
        Photobean newBean = newPic.get(newItemPosition);

        //这里就不用比较核心字段了,一定相等
        Bundle payload = new Bundle();
        if (oldBean.getSelect() != newBean.getSelect()) {
            payload.putInt("SELECT", newBean.getSelect());
        }
        if (!oldBean.getPath().equals(newBean.getPath())) {
            payload.putString("PATH", newBean.getPath());
        }

        if (payload.size() == 0)//如果没有变化 就传空
            return null;
        return payload;//
    }

    public DiffPhotoPickCallback(ArrayList<Photobean> newPic, ArrayList<Photobean> oldPic, int isType) {
        this.newPic = newPic;
        this.oldPic = oldPic;
        this.isType = isType;
    }

    public DiffPhotoPickCallback(List<DisplayInfoBean> newVideo, List<DisplayInfoBean> oldVideo, int isType) {
        this.newVideo = newVideo;
        this.oldVideo = oldVideo;
        this.isType = isType;
    }

    @Override
    public int getOldListSize() {
        if (isType == 0) return oldPic != null ? oldPic.size() : 0;
        else if (isType == 1) return oldVideo != null ? oldVideo.size() : 0;
        return 0;
    }

    @Override
    public int getNewListSize() {
        if (isType == 0) return newPic != null ? newPic.size() : 0;
        else if (isType == 1) return newVideo != null ? newVideo.size() : 0;
        return 0;
    }

    //用来判断两个对象是否同一个item
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (isType == 0)//地址相同就可以认为是同一个item了
            return newPic.get(newItemPosition).getPath().equals(oldPic.get(oldItemPosition).getPath());
        else return newVideo.get(newItemPosition) == newVideo.get(oldItemPosition);

    }

    //用来判断两个item是否包含相同的数据
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (isType == 0)
            return newPic.get(newItemPosition).getSelect() == oldPic.get(oldItemPosition).getSelect() && newPic.get(newItemPosition).getPath().equals(oldPic.get(oldItemPosition).getPath());
        else
            return newVideo.get(newItemPosition).getAddTime().equals(oldVideo.get(oldItemPosition).getAddTime()) && oldVideo.get(newItemPosition).getPath().equals(newVideo.get(oldItemPosition).getPath());

    }
}
