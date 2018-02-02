package bclb.upload.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bclb.upload.album.albumlcc.adapter.DisplayImageViewAdapter;
import bclb.upload.album.albumlcc.bean.DisplayInfoBean;
import bclb.upload.album.albumlcc.utils.AlbumHelpler;
import bclb.upload.album.albumlcc.utils.PhotoSelectorHelper;
import uk.co.senab.photoview.PhotoView;
import ycpb.upload.myapplication.R;
import bclb.upload.album.albumlcc.adapter.PhotoVideoAdapter;


public class PhotoPreviewActivity extends AppCompatActivity implements PhotoSelectorHelper.OnLoadPhotoListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private ImageView mCheckBox;
    private TextView mCountText;
    private TextView mPreviewNum;
    public static final String PHOTO_PATH_IN_ALBUM = "photo_path_in_album";
    private int index, maxPickCount;
    private String albumName;
    private List<String> mList;
    private PhotoAdapter mPhotoAdapter;
    private TextView countText;
    private TextView checkbox_sel_num;
    private ArrayList<String> selectList;
    private boolean isShowCamera = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        selectList = new ArrayList<>();
        selectList=getIntent().getStringArrayListExtra("select");
        maxPickCount = getIntent().getIntExtra(PhotoPickActivity.MAX_PICK_COUNT, 1);
        albumName = getIntent().getStringExtra("album_name");
        isShowCamera = getIntent().getBooleanExtra("isShowCamera", true);

        index = getIntent().getIntExtra(PHOTO_PATH_IN_ALBUM, 0);
        if (!isShowCamera) {
            index = index + 1;
        }
        ImageView backImg = (ImageView) findViewById(R.id.pre_head_view_back);
        TextView backText = (TextView) findViewById(R.id.pre_head_view_back_t);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = getIntent();
                in.putExtra("count", mPhotoAdapter.getCount());
                in.putStringArrayListExtra("select",selectList);
                setResult(RESULT_OK, in);
                finish();

            }
        });
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = getIntent();
                in.putExtra("count", mPhotoAdapter.getCount());
                in.putStringArrayListExtra("select",selectList);
                setResult(RESULT_OK, in);
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.pre_head_view_title);
        titleText.setText(albumName);
        countText = (TextView) findViewById(R.id.pre_tv_to_confirm0);
        checkbox_sel_num = (TextView) findViewById(R.id.checkbox_sel_num);
        mViewPager = (ViewPager) this.findViewById(R.id.viewpager_preview_photo);
        mCheckBox = (ImageView) this.findViewById(R.id.checkbox_sel_flag);
        mPreviewNum = (TextView) this.findViewById(R.id.tv_preview_num);
        mCountText = (TextView) this.findViewById(R.id.pre_tv_to_confirm);
        mViewPager.addOnPageChangeListener(this);

        mViewPager.setAdapter(mPhotoAdapter = new PhotoAdapter(mList = new ArrayList<String>()));
        mPhotoAdapter.setOnDisplayImageAdapter(new DisplayImageViewAdapter<String>() {
            @Override
            public void onDisplayImage(Context context, ImageView imageView, String path) {
                Glide.with(context).load(path)
                        .error(R.mipmap.placeholder5)
                        .into(imageView);
            }
        });
        if (albumName != null && !albumName.equals(AlbumHelpler.ALBUM_NAME)) {
            new PhotoSelectorHelper(this).getAlbumPhotoList(albumName, this);
        } else {
            new PhotoSelectorHelper(this).getAllPics(this);
        }

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selCount =selectList.size();
                if (selCount >= maxPickCount) {
                    Toast.makeText(getApplicationContext(), getString(R.string.select_max) + selCount + getString(R.string.select_max_value), Toast.LENGTH_SHORT).show();
                    return;
                }
                int currentItem = mViewPager.getCurrentItem();
                boolean selFlag = selectList.contains(mList.get(currentItem));
                mCheckBox.setSelected(!selFlag);
                if (selFlag) {
                    selectList.remove(mList.get(currentItem));
                    checkbox_sel_num.setVisibility(View.INVISIBLE);
                } else {
                    checkbox_sel_num.setVisibility(View.VISIBLE);
                    selectList.add(mList.get(currentItem));
                    checkbox_sel_num.setText(String.valueOf(selectList.indexOf(mList.get(currentItem)) + 1));
                }
                updateCountView();
            }
        });

        updateCountView();

        mCountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = getIntent();
                in.putExtra("count", mPhotoAdapter.getCount());
                in.putStringArrayListExtra("select",selectList);
                setResult(RESULT_OK, in);
                finish();
            }
        });
    }

    @Override
    public void onPhotoLoaded(List<String> photos) {
        mList.clear();
        mList.addAll(photos);
        mPhotoAdapter.notifyDataSetChanged();

        mViewPager.setCurrentItem(index - 1, false);
        mPreviewNum.setText(index + "/" + mList.size());
        mCheckBox.setSelected(selectList.contains(mList.get(index-1)));
        if(selectList.contains(mList.get(index-1))){
            checkbox_sel_num.setVisibility(View.VISIBLE);
            checkbox_sel_num.setText(String.valueOf(selectList.indexOf(mList.get(index-1))+1));
        }else{
            checkbox_sel_num.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onVideoLoaded(List<DisplayInfoBean> videos) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCheckBox.setSelected(selectList.contains(mList.get(position)));
        if(selectList.contains(mList.get(position))){
            checkbox_sel_num.setVisibility(View.VISIBLE);
            checkbox_sel_num.setText(String.valueOf(selectList.indexOf(mList.get(position))+1));
        }else{
            checkbox_sel_num.setVisibility(View.INVISIBLE);
        }
        mPreviewNum.setText(position + 1 + "/" + mList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class PhotoAdapter extends PagerAdapter {
        private List<String> imgList;

        public PhotoAdapter(List<String> imgList) {
            this.imgList = imgList;
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
                mDisplayAdapter.onDisplayImage(PhotoPreviewActivity.this, photoView, imgList.get(position));
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

    private void updateCountView() {
        if (selectList.size() == 0) {
            mCountText.setEnabled(false);
            mCountText.setText(getString(R.string.head_welldone) + "(" + selectList.size() + "/" + maxPickCount + ")");
        } else {
            mCountText.setEnabled(true);
            mCountText.setText(getString(R.string.head_welldone) + "(" + selectList.size() + "/" + maxPickCount + ")");
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

    @Override
    public void onBackPressed() {
        Intent in = getIntent();
        in.putStringArrayListExtra("select",selectList);
        in.putExtra("count", mPhotoAdapter.getCount());
        setResult(RESULT_OK, in);
        finish();
    }
}
