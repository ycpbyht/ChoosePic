package bclb.upload.album;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bclb.upload.album.albumlcc.adapter.AlbumListAdapter;
import bclb.upload.album.albumlcc.adapter.DisplayImageViewAdapter;
import bclb.upload.album.albumlcc.adapter.PhotoVideoAdapter;
import bclb.upload.album.albumlcc.bean.AlbumModel;
import bclb.upload.album.albumlcc.bean.DisplayInfoBean;
import bclb.upload.album.albumlcc.utils.AlbumHelpler;
import bclb.upload.album.albumlcc.utils.PhotoSelectorHelper;
import bclb.upload.album.albumlcc.utils.PictureUtil;
import bclb.upload.album.model.PicCompress;
import bclb.upload.album.utils.RandomString;
import ycpb.upload.myapplication.R;


public class PhotoPickActivity extends AppCompatActivity implements PhotoSelectorHelper.OnLoadAlbumListener,
        PhotoSelectorHelper.OnLoadPhotoListener, PopupWindow.OnDismissListener {
    private PhotoSelectorHelper mHelper;
    private RecyclerView mRecycleView;
    private PhotoVideoAdapter photoVideoAdapter;
    private TextView mCountText;
    public static final String MAX_PICK_COUNT = "max_pick_count";
    public static final String IS_SHOW_CAMERA = "isShowCamera";
    public static final String SELECT_PHOTO_LIST = "select_photo_list";
    private static final int TO_PICK_ALBUM = 1;
    private static final int TO_PRIVIEW_PHOTO = 2;
    private static final int TO_TAKE_PHOTO = 3;
    private boolean isShowCamera;
    private int maxPickCount;
    private String mLastAlbumName;
    //状态栏
    private RelativeLayout headview;
    private ImageView down;
    private TextView album;
    private TextView titleText;
    //相册选择
    private PopupWindow popupWindow;
    private AlbumListAdapter albumListAdapter;
    private TextView mCountText0;
    private ArrayList<String> name;
    private int mShowPicCount = 0;

    private ArrayList<String> mPicList;
    private ArrayList<DisplayInfoBean> videoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick);
        mPicList = new ArrayList<>();
        videoList = new ArrayList<>();

        //图片选择的最大数量，默认是9张
        mShowPicCount = getIntent().getIntExtra("picCount", 9);


        /**
         * 从发布动态进入
         * 是否要添加集合的第一项占位（为了视频位置（添加视频或者显示已经添加的视频））
         * select 两个值 1 pic 点击照片进入   2 video 点击视频进入
         */

        isShowCamera = getIntent().getBooleanExtra("isShowCamera", false);

        if (mShowPicCount == 0) {
            maxPickCount = 9;
        } else {
            maxPickCount = mShowPicCount;
        }
        //相册列表的名字
        name = new ArrayList<>();

        mLastAlbumName = AlbumHelpler.ALBUM_NAME;

        mCountText = (TextView) this.findViewById(R.id.tv_to_confirm);
        mCountText0 = (TextView) this.findViewById(R.id.tv_to_confirm0);
        mCountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDone();
            }
        });
        //recycler
        mRecycleView = (RecyclerView) this.findViewById(R.id.mp_galley_gridView);
        GridLayoutManager mManagerDrawable = new GridLayoutManager(this, 3);
        mRecycleView.setLayoutManager(mManagerDrawable);
        mRecycleView.setAdapter(photoVideoAdapter =
                new PhotoVideoAdapter(this, isShowCamera, maxPickCount));

        //对标题栏的初始化
        headview = (RelativeLayout) findViewById(R.id.photo_pick);
        album = (TextView) findViewById(R.id.pic_head_view_commit);
        down = (ImageView) findViewById(R.id.pic_head_view_down);
        album.setText(getString(R.string.photo_pick_album));
        titleText = (TextView) findViewById(R.id.pic_head_view_title);
        titleText.setText(R.string.photo_pick_near);
        ImageView backImg = (ImageView) findViewById(R.id.pic_head_view_back);
        TextView backText = (TextView) findViewById(R.id.pic_head_view_back_t);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        //返回的选中的图片
        ArrayList<String> list = getIntent().getStringArrayListExtra(SELECT_PHOTO_LIST);
        if (list == null) {
            list = new ArrayList<>();
        }

        if (list != null) {
            photoVideoAdapter.setmSelectedImage(list);
        }
        //视频还是图片的显示逻辑
        mHelper = new PhotoSelectorHelper(this);
        mHelper.getAllPics(this);
        //相册选择
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });

        if (maxPickCount >= 1) {
            mCountText.setVisibility(View.VISIBLE);
            mCountText0.setVisibility(View.VISIBLE);
        } else {
            mCountText.setVisibility(View.GONE);
            mCountText0.setVisibility(View.GONE);
        }

        photoVideoAdapter.setOnDisplayImageAdapter(new DisplayImageViewAdapter<String>() {

            @Override
            public void onItemClick(Context context, int index, String path) {
                //由地址是否为空来判断，空的话就
                if (TextUtils.isEmpty(path)) {
                    //从发视频进入

                    //如果为第一个则跳到照相机，反之添加照片
                    if (Build.BRAND.equals("samsung")) {
                        //三星手机跳转系统相机
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        MyApp.setPath(Environment.getExternalStorageDirectory() + "/greatchef/"
                                + RandomString.randomString(8) + ".jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MyApp.getPath())));
                        startActivityForResult(intent, TO_TAKE_PHOTO);
                    } else {
                        Intent in = new Intent(PhotoPickActivity.this, YcCameraActivity.class);
                        startActivityForResult(in, TO_TAKE_PHOTO);
                    }

                } else {
                    if (path.endsWith(".mp4")) {
                        //视频＝＝因为只拿出来了mp4格式的视频
//                        Intent intent = new Intent(PhotoPickActivity.this, UploadPreVideoActivity.class);
                        Intent intent = new Intent(PhotoPickActivity.this, YcCameraActivity.class);
                        intent.putExtra("isShowDelect", false);
                        intent.putExtra("foodvideo", path);
                        intent.putExtra("islocal", true);
                        intent.putExtra("imgpass", "");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(PhotoPickActivity.this, PhotoPreviewActivity.class);
                        Log.d("========>", index + "   ");
                        intent.putExtra(PhotoPreviewActivity.PHOTO_PATH_IN_ALBUM, index);
                        intent.putExtra("isShowCamera", isShowCamera);
                        intent.putExtra(MAX_PICK_COUNT, maxPickCount);
                        intent.putExtra("album_name", mLastAlbumName);
                        intent.putStringArrayListExtra("select", (ArrayList<String>) photoVideoAdapter.getmSelectedImage());
                        startActivityForResult(intent, TO_PRIVIEW_PHOTO);
                    }
                }
            }

            @Override
            public void onDisplayImage(Context context, ImageView imageView, String s) {

            }

            @Override
            public void onItemImageClick(Context context, int index, List<String> list) {

            }

            @Override
            public void onImageCheckL(String path, boolean isChecked) {
                ///需要处理

//                photoVideoAdapter.notifyDataSetChanged();
                updateCountView();
            }
        });
        updateCountView();
    }


    /**
     * 显示相册列表
     */
    private void showPop() {
        //视频还是图片的显示逻辑

        mHelper.getAlbumList(this);
        down.setBackgroundResource(R.mipmap.photo_havepic_down);
        ListView mListView;
        View view = LayoutInflater.from(this).inflate(R.layout.activity_album_pick, null);
        mListView = (ListView) view.findViewById(R.id.lv_show_album);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        albumListAdapter = new AlbumListAdapter(this, name);
        mListView.setAdapter(albumListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //相册的名字
                mLastAlbumName = albumListAdapter.getItem(position).getName();
                if (name.size() > 0) {
                    name.set(0, mLastAlbumName);
                } else {
                    name.add(mLastAlbumName);
                }
                titleText.setText(mLastAlbumName);
                if (AlbumHelpler.ALBUM_NAME.equals(mLastAlbumName)) {
                    mHelper.getAllPics(PhotoPickActivity.this);
                } else if (AlbumHelpler.VIDEO_NAME.equals(mLastAlbumName)) {
                    mHelper.getAlbumVideoList((PhotoSelectorHelper.OnLoadPhotoListener) PhotoPickActivity.this);
                } else {
                    mHelper.getAlbumPhotoList(mLastAlbumName, (PhotoSelectorHelper.OnLoadPhotoListener) PhotoPickActivity.this);
                }
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });

        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setOnDismissListener(this);
        }
        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(headview);
        } else {
            popupWindow.dismiss();
        }
    }


    /**
     * 图片加载完成
     *
     * @param photos
     */
    @Override
    public void onPhotoLoaded(List<String> photos) {
        //顺序不能错，否则在photoVideoAdapter.notifyDataSetChanged(photos); 会对photos进行操作，假如一个站位放置拍摄
        mPicList.clear();
        mPicList.addAll(photos);

        photoVideoAdapter.notifyDataSetChanged(photos);

    }

    @Override
    public void onVideoLoaded(List<DisplayInfoBean> videos) {
        photoVideoAdapter.notifyVideoDtChanged(videos);
    }

    /**
     * 相册加载完成
     *
     * @param albums
     */
    @Override
    public void onAlbumLoaded(List<AlbumModel> albums) {
        if (null != albums && albums.size() != 0) {
            albumListAdapter.notifyDataSetChanged(albums);
        } else {
            albumListAdapter.notifyDataSetChanged(new ArrayList<AlbumModel>());
            Toast.makeText(this, getString(R.string.photo_empty), Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            case TO_PRIVIEW_PHOTO:
                updateCountView();

                ///刷新adaoter是得刷新的，不过有一种比较特殊的情况，在相册页面，按home 拍摄照片一张，由于在预览页面
                //重新 mHelper.getAllPics 了数据，就导致预览页面数据比较新，照片多了一张。这时候两种解决方式，
                // 1 预览页面使用当前页面的数据，由于数据量可能很大，并不能用intent来传递，所以可以存放数据到第三方
//                 2 预览界面数量不同的时候，在此处返回数据总量，当数两个页面据总量不同时，更新当前页面数据。
//                依然存在的问题1，无法保证按下home键盘拍几张再删除同样的张数导致的问题。实在是懒得写了。
//                解决办法：在onRsume时候，更新相册数据。坏处是，照片数据量巨大的时候可能比较慢
//                依然存在的问题2，上述情况下删除了选中的照片，没有在更新中删除选中列表，暂时因为赶进度，会在下一版本进行优化
//               （思路  onresume时，或者此时（就是刷新数据的时候），遍历当前选中的列表，查询是否存在于相册列表中）
//                3 采用popwindow，在当前页面进行数据操作，优势明显，少一个页面，少一次查询，但是问题点在于会有比较多的数据问题，比如在预览的时候进行选中操作数据刷新的问题
                ArrayList<String> liiiiiist=data.getStringArrayListExtra("select");
                photoVideoAdapter.setmSelectedImage(liiiiiist);

                if (isShowCamera) {
                    if ((mPicList.size() - 1) != data.getIntExtra("count", 0)) {
                        mHelper.getAllPics(this);
                    } else {
                        photoVideoAdapter.notifyDataSetChanged(mPicList);
//                        photoVideoAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (mPicList.size() != data.getIntExtra("count", 0)) {
                        mHelper.getAllPics(this);
                    } else {
                        photoVideoAdapter.notifyDataSetChanged(mPicList);
//                        photoVideoAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case TO_TAKE_PHOTO:
//                三星手机去使用系统相机拍摄，因为角度当时没控制住，是偏转90度的，后来太懒惰了也就没改。。。。
                if (Build.BRAND.equals("samsung")) {
                    File file = new File(MyApp.getPath());
                    PictureUtil.notifyGallery(this, MyApp.getPath());
                    photoVideoAdapter.getmSelectedImage().add(MyApp.getPath());
                    selectDone();
                } else {
                    String url = data.getStringExtra("imgp");
                    PictureUtil.notifyGallery(this, url);
                    photoVideoAdapter.getmSelectedImage().add(url);
                    selectDone();
                }
                break;

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 处理逻辑为压缩了的不在压缩，没压缩的压缩完替换之前存在集合中的数据
     */
    //图库选择中选择的一些图片
    ArrayList<String> list = new ArrayList<>();
    //MyApp中保存的，之前最后一次选中后的数据

    private void selectDone() {
        PicCompress video;
        if (MyApp.getApp().getPiclist() == null || MyApp.getApp().getPiclist().size() == 0) {
            video = new PicCompress();
        } else {
            video = MyApp.getApp().getPiclist().get(0);
        }
        MyApp.getApp().getPiclist().clear();

        if (photoVideoAdapter.getmSelectedImage() != null) {
            for (int i = 0; i < photoVideoAdapter.getmSelectedImage().size(); i++) {
                MyApp.getApp().getPiclist().add(
                        new PicCompress(photoVideoAdapter.getmSelectedImage().get(i), "", false));
                list.add(photoVideoAdapter.getmSelectedImage().get(i));
            }
//            if (isShowCamera) {
//                MyApp.getApp().getPiclist().add(0, video);
//            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra(SELECT_PHOTO_LIST, list);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void selectVideo(String path) {
        Intent intent = new Intent();
        PicCompress compress = new PicCompress();
        compress.setPicadress(path);
        if (MyApp.getApp().getPiclist() != null && MyApp.getApp().getPiclist().size() != 0) {
            MyApp.getApp().getPiclist().remove(0);
        }
        MyApp.getApp().getPiclist().add(0, compress);
        intent.putExtra("videoPath", path);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateCountView() {
        if (photoVideoAdapter.getmSelectedImage().size() == 0) {
            mCountText.setEnabled(false);
            mCountText0.setEnabled(false);

        } else {
            mCountText.setEnabled(true);
            mCountText0.setEnabled(true);

        }
        mCountText.setText(getString(R.string.head_welldone) + "(" + photoVideoAdapter.getmSelectedImage().size() + "/" + maxPickCount + ")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        photoVideoAdapter.getmSelectedImage().clear();
    }

    @Override
    public void onDismiss() {
        down.setBackgroundResource(R.mipmap.photo_havepic_up);
    }

}
