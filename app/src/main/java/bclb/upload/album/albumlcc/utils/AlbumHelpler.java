package bclb.upload.album.albumlcc.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bclb.upload.album.albumlcc.bean.AlbumModel;
import bclb.upload.album.albumlcc.bean.DisplayInfoBean;

/**
 * Created by cuiliubi on 2017/9/26.
 */

public class AlbumHelpler {
    private ContentResolver mResolver;
    private Context context;
    public static String ALBUM_NAME = "最近照片";
    public static String VIDEO_NAME = "所有视频";
    private long MIN_PIC_SIZE = 1024 * 20;//图片最小尺寸
    private long MAX_VIDEO_SIZE = 15 * 1024 * 1024;//视频的最大尺寸
    private String MAX_VIDEO_TIME = "10000";//视频最大时长

    private List<AlbumModel> albumsList = new ArrayList<>();    //照片的集合
    public AlbumHelpler(Context context) {
        this.context = context;
        mResolver = context.getContentResolver();
    }

    /**
     * 获得所有的照片，以及相册
     *
     * @return
     */
    public List<String> getPics() {
        List<String> picList = new ArrayList<>(); //照片的集合
        HashMap<String, AlbumModel> temporary = new HashMap<>();
//      return albumsList;
        //照片的集合 picList
        Uri mTable = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //照片的限制大小
        String imgSize = String.valueOf(MIN_PIC_SIZE);
        //照片选取的列名  代表所有图片文件的列  地址 尺寸 时间
        String[] mColumns = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_ADDED};
        //照片的筛选条件  多用途种类 控制尺寸
        String mSelection = "(" + MediaStore.Images.ImageColumns.MIME_TYPE + "=? or " + MediaStore.Images.ImageColumns.MIME_TYPE + "=? or " + MediaStore.Images.ImageColumns.MIME_TYPE + "=? " + ") AND " + MediaStore.Images.ImageColumns.SIZE + ">?";

        //筛选条件值，与？相对应 三种图片类型，可以考虑减少
        String[] mSelectArgs = {"image/jpeg", "image/png", "image/webp", imgSize};
        //根据ContentResolver进行图片的添加
        Cursor mCursor = mResolver.query(mTable, mColumns, mSelection, mSelectArgs, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");
        if (null == mCursor || mCursor.getCount() == 0) {
            return picList;
        }
        if (null != mCursor && mCursor.getCount() != 0) {
            mCursor.moveToFirst();
//            初始化一个相册的model
            AlbumModel album1 = new AlbumModel(ALBUM_NAME, 0, mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
            albumsList.add(0, album1);


//            while (mCursor.moveToNext()) {
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {

                String albumPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                album1.increaseCount();
                if (temporary.containsKey(albumPath)) {
//                    所在的相册相同就增加一个
                    AlbumModel albumBean = temporary.get(albumPath);
                    albumBean.increaseCount();
                } else {
//                    否则增加一个相册
                    AlbumModel albumBean = new AlbumModel(albumPath, 1, mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                    albumsList.add(albumBean);
                    temporary.put(albumPath, albumBean);
                }

                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                picList.add(path);
            }
        }
        if (!mCursor.isClosed()) {
            mCursor.close();
            mCursor = null;
        }
        return picList;
    }

    /**
     * 获得图片和视频的集合
     *
     * @return
     */
    public List<DisplayInfoBean> getPicVideos() {
        //照片的集合
        List<DisplayInfoBean> picVideoList = new ArrayList<>();
        Uri mTable = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //照片的限制大小
        String imgSize = String.valueOf(MIN_PIC_SIZE);
        //照片选取的列名
        String[] mColumns = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DATE_ADDED};
        //照片的筛选条件
        String mSelection = "(" + MediaStore.Images.ImageColumns.MIME_TYPE + "=?or " + MediaStore.Images.ImageColumns.MIME_TYPE + "=?or " + MediaStore.Images.ImageColumns.MIME_TYPE + "=?" + ")AND " + MediaStore.Images.ImageColumns.SIZE + ">?";
        //筛选条件值，与？相对应
        String[] mSelectArgs = {"image/jpeg", "image/png", "image/webp", imgSize};
        //视频的筛选条件
        String videoSize = String.valueOf(MAX_VIDEO_SIZE);
        String mSelect = MediaStore.Video.Media.MIME_TYPE + "=? AND " + MediaStore.Video.Media.DURATION + "<? AND " + MediaStore.Video.Media.SIZE + "<?";
        String[] mSelectVideoArgs = {"video/mp4", MAX_VIDEO_TIME, videoSize};
        Cursor mCursor = mResolver.query(mTable, mColumns, mSelection, mSelectArgs, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");
        if (mCursor == null || mCursor.getCount() == 0) {
            return picVideoList;
        }
        if (mCursor != null && mCursor.getCount() != 0) {
            while (mCursor.moveToNext()) {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                String addTime = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
                picVideoList.add(new DisplayInfoBean(path, addTime));
            }
        }
        Cursor cursor = mResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, mSelect, mSelectVideoArgs, MediaStore.Video.Media.DATE_ADDED + " DESC");
        if (mCursor == null || mCursor.getCount() == 0) {
            return picVideoList;
        }
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                String addTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
                picVideoList.add(new DisplayInfoBean(path, addTime));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            cursor = null;
        }

        if (!mCursor.isClosed()) {
            mCursor.close();
            mCursor = null;
        }
        return picVideoList;
    }

    /**
     * 获得所有图片的相册
     *
     * 把所有的注释放开依然可用，
     * 现在是把相册以及图片的获取都放到了上面的方法，节省一次循环操作
     * @return
     */
    public List<AlbumModel> getAlbums() {
        //照片的集合 albumsList
//        List<AlbumModel> albumsList = new ArrayList<>();
//        HashMap<String, AlbumModel> temporary = new HashMap<>();
//        //照片大小的最小值
//        String imgSize = String.valueOf(MIN_PIC_SIZE);
//        Uri mTable = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        //照片选取的列名
//        String[] mColumns = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_ADDED};
//        String mSelection = "(" + MediaStore.Images.ImageColumns.MIME_TYPE + "=? or " + MediaStore.Images.ImageColumns.MIME_TYPE + "=? or " + MediaStore.Images.ImageColumns.MIME_TYPE + "=? " + ") AND " + MediaStore.Images.ImageColumns.SIZE + ">?";
//        String[] mSelectArgs = {"image/jpeg", "image/png", "image/webp", imgSize};
//        //照片的相册获得
//        Cursor mCursor = mResolver.query(mTable, mColumns, mSelection, mSelectArgs, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");
//        if (null == mCursor || mCursor.getCount() == 0) {
//            return albumsList;
//        }
//        if (mCursor != null && mCursor.getCount() != 0) {
//            mCursor.moveToFirst();
////            初始化一个相册的model
//            AlbumModel album1 = new AlbumModel(ALBUM_NAME, 0, mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
//            albumsList.add(0, album1);
//            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
////            while (mCursor.moveToNext()) {
////                相册的地址
//                String albumPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
//                album1.increaseCount();
//                if (temporary.containsKey(albumPath)) {
////                    所在的相册相同就增加一个
//                    AlbumModel albumBean = temporary.get(albumPath);
//                    albumBean.increaseCount();
//                } else {
////                    否则增加一个相册
//                    AlbumModel albumBean = new AlbumModel(albumPath, 1, mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
//                    albumsList.add(albumBean);
//                    temporary.put(albumPath, albumBean);
//                }
//            }
//        }
//        if (!mCursor.isClosed()) {
//            mCursor.close();
//            mCursor = null;
//        }
        return albumsList;

    }

    /**
     * 获得视频相册
     */
    public List<AlbumModel> getVideoAlbums() {
        //照片的集合
        List<AlbumModel> albumsList = new ArrayList<>();
        String videoSize = String.valueOf(MAX_VIDEO_SIZE);
        String mSelect = MediaStore.Video.Media.MIME_TYPE + "=? AND " + MediaStore.Video.Media.DURATION + "<=? AND " + MediaStore.Video.Media.SIZE + "<=?";
        String[] mSelectVideoArgs = {"video/mp4", MAX_VIDEO_TIME, videoSize};
        //视频的相册获得
        Cursor cursor = mResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, mSelect, mSelectVideoArgs, MediaStore.Video.Media.DATE_ADDED + " DESC");
        if (null == cursor || cursor.getCount() == 0) {
            return albumsList;
        }
        cursor.moveToFirst();
        AlbumModel VideoAlbum = new AlbumModel(VIDEO_NAME, 0, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
        albumsList.add(VideoAlbum);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                VideoAlbum.increaseCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            cursor = null;
        }

        return albumsList;

    }

    /**
     * 获得相册中的照片
     *
     * @param albumName
     * @return
     */
    public List<String> getAlbumPic(String albumName) {
        List<String> pics = new ArrayList<>();
        Uri mTable = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //照片的限制大小
        String imgSize = String.valueOf(MIN_PIC_SIZE);
        //照片选取的列名
        String[] mColumns = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DATE_ADDED};

        String mSelection = "(" + MediaStore.Images.ImageColumns.MIME_TYPE + "=? or " + MediaStore.Images.ImageColumns.MIME_TYPE + "=? or " + MediaStore.Images.ImageColumns.MIME_TYPE + "=? " + ")AND  " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=? AND " + MediaStore.Images.ImageColumns.SIZE + ">?";
        String[] selectArgs = {"image/jpeg", "image/png", "image/webp", albumName, imgSize};
        Cursor mCursor = mResolver.query(mTable, mColumns, mSelection, selectArgs, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");
        if (mCursor == null || mCursor.getCount() == 0) {
            return pics;
        }
        while (mCursor.moveToNext()) {
            pics.add(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
        }
        if (!mCursor.isClosed()) {
            mCursor.close();
            mCursor = null;
        }
        return pics;
    }

    /**
     * 获得所有的视频
     */
    public List<DisplayInfoBean> getVideos() {
        List<DisplayInfoBean> videoLists = new ArrayList<>();
        String videoSize = String.valueOf(MAX_VIDEO_SIZE);
        String mSelect = MediaStore.Video.Media.MIME_TYPE + "=? AND " + MediaStore.Video.Media.DURATION + "<=? AND " + MediaStore.Video.Media.SIZE + "<=?";
        String[] mSelectArgs = {"video/mp4", MAX_VIDEO_TIME, videoSize};
        Cursor cursor = mResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, mSelect, mSelectArgs, MediaStore.Video.Media.DATE_ADDED + " DESC");
        if (null == cursor || cursor.getCount() == 0) {
            return videoLists;
        }
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                String addTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                videoLists.add(new DisplayInfoBean(path, addTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            cursor = null;
        }
        return videoLists;
    }
}
