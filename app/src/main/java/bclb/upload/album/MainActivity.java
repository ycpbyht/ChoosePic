package bclb.upload.album;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import bclb.upload.album.model.PicCompress;
import bclb.upload.album.utils.Constants;
import ycpb.upload.myapplication.R;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private Boolean ispermission = false;
    private RecyclerView mRecyclerView;
    private TextView cancle;
    private TextView mDynamic;
    private EditText mEditText;
    private PublicGridImageAdapter mAdapter;
    //按钮的显示逻辑（0：默认状态，1：选择图片，2：选择视频）
    /////设置icontype＝0  可以表示图片＋视频的选择
    private int iconType = 1;
    private long setTime = 0;
    public ArrayList<PicCompress> mUploadList = new ArrayList<>();


    /////查看大图的Popwindow
    private TextView number;
    private ViewPager vp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();

        requestPermissions();
        FloatingActionButton with_camera = (FloatingActionButton) findViewById(R.id.with_camera);
        FloatingActionButton withoutCamera = (FloatingActionButton) findViewById(R.id.without_camera);

        with_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
                Snackbar.make(view, "纯粹为了试试这个按钮怎么用。。。", Snackbar.LENGTH_LONG)
                        .setAction("相册&相机", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ispermission) {
                                    Intent in = new Intent(MainActivity.this, PhotoPickActivity.class);
                                    in.putExtra("picCount", 9);
                                    in.putExtra("from", "picAlbum");
                                    in.putExtra("select", "pic");
                                    in.putExtra("isShowCamera", true);
                                    //把已经选中的图片通知相册，因为考虑到后面的压缩，所以使用的是
                                    ArrayList<String> list = new ArrayList<String>();
                                    ArrayList<PicCompress> picComlist = MyApp.getApp().getPiclist();
                                    if (picComlist != null && picComlist.size() > 0) {
                                        for (int i = 0; i < picComlist.size(); i++) {
                                            list.add(picComlist.get(i).getPicadress());
                                        }
                                    }
                                    in.putStringArrayListExtra(PhotoPickActivity.SELECT_PHOTO_LIST, list);
                                    startActivityForResult(in, 200);
                                } else {
                                    Toast.makeText(MainActivity.this, "拒绝权限申请", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }).show();
            }
        });


        withoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
                Snackbar.make(view, "纯粹为了试试这个按钮怎么用。。。", Snackbar.LENGTH_LONG)
                        .setAction("仅相册", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent in = new Intent(MainActivity.this, PhotoPickActivity.class);
                                in.putExtra("picCount", 9);
                                in.putExtra("from", "picAlbum");
                                in.putExtra("select", "pic");
                                in.putExtra("isShowCamera", false);
                                //把已经选中的图片通知相册
                                ArrayList<String> list = new ArrayList<String>();
                                ArrayList<PicCompress> picComlist = MyApp.getApp().getPiclist();
                                if (picComlist != null && picComlist.size() > 0) {
                                    for (int i = 0; i < picComlist.size(); i++) {
                                        list.add(picComlist.get(i).getPicadress());
                                    }
                                }
                                in.putStringArrayListExtra(PhotoPickActivity.SELECT_PHOTO_LIST, list);
                                startActivityForResult(in, 200);
                            }
                        }).show();
            }
        });


    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_public);

        cancle = (TextView) findViewById(R.id.tv_public_back_head);
        mDynamic = (TextView) findViewById(R.id.tv_public_save);
        mEditText = (EditText) findViewById(R.id.edit_publish);


        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));//一行最多5个
        mAdapter = new PublicGridImageAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 200:
                //点击➕去添加照片后
                if (data == null) {
                    /////设置icontype＝0  可以表示图片＋视频的选择
                    iconType = 1;
                    return;
                }

                mUploadList.clear();
//                当然此时可以用  addALL，此处仅仅为了后续进行图片压缩上传之后的判断而已
                if (MyApp.getApp().getPiclist().size() > 0) {
                    for (int i = 0; i < MyApp.getApp().getPiclist().size(); i++) {
                        mUploadList.add(new PicCompress(MyApp.getApp().getPiclist().get(i).getPicadress(), "", false));
                    }
                }


                Log.d("===========>",mUploadList.size()+"   ");
                mAdapter.notifyDataSetChanged();
                break;

            default:
                break;
        }
    }


    /**
     * RxPermission权限申请
     */
    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {

            RxPermissions rxPermissions = new RxPermissions(this);

            rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    .subscribe(new Action1<Permission>() {
                        @Override
                        public void call(Permission permission) {
                            if (permission.name.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                //当ACCESS_FINE_LOCATION权限获取成功时，permission.granted=true
                                Log.i("permissions", Manifest.permission.WRITE_EXTERNAL_STORAGE + "：" + permission.granted);
                                ispermission = true;
                            } else if (permission.name.equals(Manifest.permission.CAMERA)) {
                                //当RECORD_AUDIO 权限获取成功时，permission.granted=true
                                Log.i("permissions", Manifest.permission.CAMERA + "：" + permission.granted);
                                ispermission = true;

                            } else if (permission.name.equals(Manifest.permission.RECORD_AUDIO)) {
                                //当CAMERA权限获取成功时，permission.granted=true
                                Log.i("permissions", Manifest.permission.RECORD_AUDIO + "：" + permission.granted);
                                ispermission = true;

                            } else {
                                ispermission = false;
                            }
                        }
                    });
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        number.setText(position + 1 + "/" + mUploadList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class PublicGridImageAdapter extends RecyclerView.Adapter<PublicGridImageAdapter.MyViewHolder> {

        private LayoutInflater mInflater;
        private PopupWindow popupWindow;
        private MainSelectAdapter pagerAdapter;
        public List<String> bigPicList = new ArrayList<>();

        public PublicGridImageAdapter() {
            mInflater = LayoutInflater.from(MainActivity.this);
        }

        @Override
        public PublicGridImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PublicGridImageAdapter.MyViewHolder holder = new PublicGridImageAdapter.MyViewHolder(mInflater.inflate(
                    R.layout.item_dynamic_grid_image, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MainActivity.PublicGridImageAdapter.MyViewHolder holder, final int i) {
            //默认状态,选择图片以及视频按钮都存在
            if (iconType == 0) {
                holder.meng.setVisibility(View.INVISIBLE);
//                此时，第0个是视频第1个是照片
                if (i == 0) {
                    Glide.with(MainActivity.this).load(R.mipmap.video_add).into(holder.img);
                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            iconType = 2;
                            Intent intent = new Intent(MainActivity.this, PhotoPickActivity.class);
                            intent.putExtra("picCount", 9);
                            intent.putExtra(PhotoPickActivity.IS_SHOW_CAMERA, true);
                            intent.putExtra("picType", 1);
                            intent.putExtra("isBothVideoPic", Constants.selectVideo);
                            intent.putExtra("isShowCamera", true);
                            startActivityForResult(intent, 200);

                        }
                    });
                }
                if (i == 1) {
                    Glide.with(MainActivity.this).load(R.mipmap.photo_add).into(holder.img);
                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            iconType = 1;
                            Intent intent = new Intent(MainActivity.this, PhotoPickActivity.class);
                            intent.putExtra(PhotoPickActivity.IS_SHOW_CAMERA, true);
                            intent.putExtra("from", Constants.fromDynamic);
                            intent.putExtra("select", Constants.selectPic);
                            startActivityForResult(intent, 200);
                        }
                    });
                }

                // 当选择上传照片的逻辑
            } else if (iconType == 1) {
                holder.meng.setVisibility(View.INVISIBLE);
                if (i + 1 > mUploadList.size()) {
                    holder.mProgress.setVisibility(View.GONE);
                    Glide.with(MainActivity.this).load(R.mipmap.photo_add).into(holder.img);
                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ispermission){
                                    openAlbum();
                                }else{
                                    if (System.currentTimeMillis() - setTime > 20000) {
//                                                        限制下不要太频繁的弹窗
                                        Toast.makeText(MainActivity.this, "拒绝权限申请", Toast.LENGTH_SHORT)
                                                .show();
                                        setTime = System.currentTimeMillis();
                                    }
                                }
                            } else {
                                openAlbum();
                            }
                        }

                        private void openAlbum() {
                            Intent intent = new Intent(MainActivity.this, PhotoPickActivity.class);
                            intent.putExtra(PhotoPickActivity.IS_SHOW_CAMERA, true);
                            ArrayList<String> list = new ArrayList<String>();
                            ArrayList<PicCompress> picComlist = MyApp.getApp().getPiclist();
                            if (picComlist != null && picComlist.size() > 0) {
                                for (int i = 0; i < picComlist.size(); i++) {
                                    list.add(picComlist.get(i).getPicadress());
                                }
                            }
                            intent.putStringArrayListExtra(PhotoPickActivity.SELECT_PHOTO_LIST, list);
                            intent.putExtra("from", Constants.fromDynamic);
                            intent.putExtra("select", Constants.selectPic);
                            startActivityForResult(intent, 200);
                        }
                    });
                } else {
                    Glide.with(MainActivity.this).load(mUploadList.get(i).getPicadress()).into(holder.img);
                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPop(i);
                        }
                    });

                    if (mUploadList.get(i).isHasup()) {
                        holder.mProgress.setVisibility(View.GONE);
                        holder.img.setColorFilter(null);
                    }
                }
                //当选择上传视频的逻辑
            } else if (iconType == 2) {
                final String videoUrl = mUploadList.get(0).getPicadress();
                if (!TextUtils.isEmpty(videoUrl) || !TextUtils.isEmpty(mUploadList.get(0).getPic2com())) {
                    holder.meng.setVisibility(View.VISIBLE);
                    //当直接拍视频进来，用glid显示，当网络过来则直接显示图片
                    if (TextUtils.isEmpty(mUploadList.get(0).getFoodLivePic())) {
                        Glide.with(MainActivity.this).load(Uri.fromFile(new File(videoUrl))).into(holder.img);
                    } else {
                        Glide.with(MainActivity.this).load(mUploadList.get(0).getFoodLivePic()).into(holder.img);
                    }
                    if (!mUploadList.get(i).isHasup()) {
                        holder.img.setColorFilter(0x80000000);
                        holder.mProgress.setVisibility(View.VISIBLE);
                    }
                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);

                            if (!TextUtils.isEmpty(videoUrl)) {
                                intent.putExtra("foodvideo", videoUrl);
                                intent.putExtra("islocal", true);
                            } else {
                                intent.putExtra("foodvideo", mUploadList.get(0).getPic2com());
                                intent.putExtra("islocal", false);
                            }
                            if (!TextUtils.isEmpty(mUploadList.get(0).getName4suc())) {
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    holder.mProgress.setVisibility(View.INVISIBLE);
                    holder.img.setColorFilter(null);
                    holder.meng.setVisibility(View.INVISIBLE);
                    holder.img.setImageResource(R.mipmap.video_add);
                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //由于要重新开启界面，所以要将数据提前保存到myapp中（由于上传图片后集合中相应的数据就变化了，所以要保存一下）
                            MyApp.getApp().getPiclist().clear();
                            Intent intent = new Intent(MainActivity.this, PhotoPickActivity.class);
                            intent.putExtra(PhotoPickActivity.IS_SHOW_CAMERA, true);
                            intent.putExtra("from", Constants.fromDynamic);
                            intent.putExtra("select", Constants.selectVideo);
                            startActivity(intent);

                        }
                    });
                }

            }

        }

        @Override
        public int getItemCount() {
            if (iconType == 1) {
                //以9 为界限
                if (mUploadList.size() < 9)
                    return mUploadList.size() + 1;
                return mUploadList.size();
            } else if (iconType == 2) {
                return mUploadList.size();
            } else {
                return 2;
            }

        }

        /**
         * 显示图片大图
         *
         * @param a
         */

        private void showPop(int a) {

            if (popupWindow == null) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.uploadphoto_pop, null);
                number = (TextView) view.findViewById(R.id.upload_photo_num);
                TextView del = (TextView) view.findViewById(R.id.upload_photo_del);
                vp = (ViewPager) view.findViewById(R.id.upload_photo_vp);
                for (int i = 0; i < mUploadList.size(); i++) {
                    bigPicList.add(mUploadList.get(i).getPicadress());
                }
                vp.addOnPageChangeListener(MainActivity.this);
                pagerAdapter = new MainSelectAdapter(bigPicList, popupWindow);
                vp.setAdapter(pagerAdapter);
                vp.setCurrentItem(a);
                number.setText(a + 1 + "/" + mUploadList.size());
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.upload_dialog_title)).setMessage(getString(R.string.upload_dialog_con))
                                .setPositiveButton(getString(R.string.upload_dialog_sure), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (mUploadList.size() > 1) {
                                            MyApp.getApp().getPiclist().remove(vp.getCurrentItem());
                                            mUploadList.remove(vp.getCurrentItem());
                                            number.setText(vp.getCurrentItem() + 1 + "/" + mUploadList.size());
                                            bigPicList.remove(vp.getCurrentItem());
                                            mAdapter.notifyDataSetChanged();
                                            pagerAdapter.notifyDataSetChanged();
                                        } else {
                                            MyApp.getApp().getPiclist().remove(0);
                                            mUploadList.remove(0);
                                            /////设置icontype＝0  可以表示图片＋视频的选择
                                            iconType = 1;
                                            mAdapter.notifyDataSetChanged();
                                            bigPicList.remove(vp.getCurrentItem());
                                            pagerAdapter.notifyDataSetChanged();
                                            number.setText(vp.getCurrentItem() + 1 + "/" + mUploadList.size());

                                            //控制发布字体颜色
                                            String mContent = mEditText.getText().toString();

                                            if (popupWindow != null && popupWindow.isShowing())
                                                popupWindow.dismiss();


                                        }
                                    }
                                }).setNegativeButton(getString(R.string.upload_dialog_quit), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (popupWindow.isShowing()) {
                                    popupWindow.dismiss();
                                }
                                return;
                            }
                        }).show();
                    }
                });
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, true);
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffff));
            }
            if (!popupWindow.isShowing()) {
                bigPicList.clear();
                for (int i = 0; i < mUploadList.size(); i++) {
                    bigPicList.add(mUploadList.get(i).getPicadress());
                }
                pagerAdapter.notifyDataSetChanged();
                number.setText(a + 1 + "/" + mUploadList.size());
                vp.setCurrentItem(a);
                popupWindow.showAtLocation(cancle, Gravity.CENTER, 0, 0);
            } else {
                popupWindow.dismiss();
            }
        }

        public void removeData(int position) {
            mUploadList.remove(position);
            notifyItemRemoved(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView img;
            ImageView meng;
            TextView hint;
            ProgressBar mProgress;

            public MyViewHolder(View view) {
                super(view);
                img = (ImageView) view.findViewById(R.id.img_public_item);
                meng = (ImageView) view.findViewById(R.id.img_pic_id);
                mProgress = (ProgressBar) view.findViewById(R.id.progress_publish_dynamic);
            }
        }

    }

}
