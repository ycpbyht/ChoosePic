package bclb.upload.album.albumlcc.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bclb.upload.album.albumlcc.bean.DisplayInfoBean;
import bclb.upload.album.albumlcc.bean.Photobean;
import bclb.upload.album.albumlcc.view.SquareRelativeLayout;
import bclb.upload.album.utils.DiffPhotoPickCallback;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ycpb.upload.myapplication.R;


/**
 * 相册界面的adapter
 * Created by cuiliubi
 */

public class PhotoVideoAdapter extends RecyclerView.Adapter<PhotoVideoAdapter.MyViewHolder> {
    //用户选择的图片，存储为图片的完整路径
    public ArrayList<String> mSelectedImage;//选中图片的list
    private ArrayList<Integer> mSelectedImagePosition;//记录选中图片在整个mPiclist中的位置
    protected Context mContext;
    protected ArrayList<Photobean> mPicList;//(当前相册的)全部图片的list
    protected ArrayList<DisplayInfoBean> mVideoList;
    private int maxCount;
    private LayoutInflater mInflater;
    private DisplayImageViewAdapter<String> mDisplayAdapter;
    private boolean isShowCamera;
    private PhotoVideoAdapter adapter;

    public List<String> getmSelectedImage() {
        return mSelectedImage;
    }

    /**
     * 设置选中list，
     * 设置mSelectedImagePosition 中选中的位置
     *
     * @param mSelectedImage
     */
    public void setmSelectedImage(ArrayList<String> select) {
        mSelectedImage.clear();
        mSelectedImage.addAll(select);
        if (mPicList != null && mPicList.size() > 0) {
            mSelectedImagePosition.clear();
            for (int i = 0; i < mPicList.size(); i++) {
                if (select.contains(mPicList.get(i).getPath())) {
                    mPicList.get(i).setSelect(select.indexOf(mPicList.get(i).getPath()));
                    mSelectedImagePosition.add(i);
                }

                if (select.size() == mSelectedImagePosition.size()) {
//                    当已经找到全部选中图片的位置就可以跳出循环了(此方法)
                    return;
                }
            }
        }

    }

    public PhotoVideoAdapter(Context mContext, boolean isShowCamera, int maxCount) {
        this.mContext = mContext;
        this.maxCount = maxCount;
        this.isShowCamera = isShowCamera;
        mInflater = LayoutInflater.from(mContext);
        mSelectedImage = new ArrayList<>();
        mSelectedImagePosition = new ArrayList<>();
        mPicList = new ArrayList<>();
        if (isShowCamera) {
            Photobean bean = new Photobean(0, "");
            mPicList.add(0, bean);
        }
        adapter = this;
    }

    public PhotoVideoAdapter(Context mContext, boolean isShowCamera, int maxCount, ArrayList<String> mSelectedImage, ArrayList<Integer> mSelectedImagePosition,ArrayList<Photobean> mPicList) {
        this.mContext = mContext;
        this.maxCount = maxCount;
        this.isShowCamera = isShowCamera;
        mInflater = LayoutInflater.from(mContext);
        this.mSelectedImage =mSelectedImage;
        this.mSelectedImagePosition = mSelectedImagePosition;
        this.mPicList=mPicList;
        mPicList = new ArrayList<>();
        if (isShowCamera) {
            Photobean bean = new Photobean(0, "");
            mPicList.add(0, bean);
        }
        setmSelectedImage(mSelectedImage);
        adapter = this;
    }


    public void setOnDisplayImageAdapter(DisplayImageViewAdapter<String> adapter) {
        this.mDisplayAdapter = adapter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PhotoVideoAdapter.MyViewHolder holder;
        if (viewType == 1) {
            holder = new PhotoVideoAdapter.MyViewHolder(mInflater.inflate(R.layout.grid_photo_item, parent, false));
        } else {
            holder = new PhotoVideoAdapter.MyViewHolder(mInflater.inflate(R.layout.grid_camera_item, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (mDisplayAdapter == null) {
            return;
        }
        if (isShowCamera == true) {
            if (position == 0) {

                holder.textFirst.setText(mContext.getString(R.string.photo_take));
                holder.imgFirst.setImageResource(R.mipmap.photo_icon_takephoto);

                holder.camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSelectedImage.size() < maxCount) {
                            mDisplayAdapter.onItemClick(mContext, 0, "");
                        } else {
                            Toast.makeText(mContext.getApplicationContext(),
                                    "不能超过" + maxCount + "张图片", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else {
                bindItemView(holder, position);
            }
        } else {
            bindItemView(holder, position);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position, List<Object> payloads) {
        //实际这里可以为了节省性能写的更简单一点
        //整体的数据增删可以走diffutil来做，否则的话，比如只是选中状态，可以不用diffutil来比较数据，而是选择直接传递非空payloads进行notify处理
        //不过带来的问题就是需要手动比较数据，每当取消选中的时候，最多需要调整9个（maxCount）个数据；
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle payload = (Bundle) payloads.get(0);
            Photobean bean = mPicList.get(position);
            for (String key : payload.keySet()) {
                switch (key) {
                    case "SELECT":
                        //这里可以用payload里的数据，不过data也是新的 也可以用
                        if (bean.getSelect() == 0) {
//                            未曾选中
                            holder.selectFlag.setSelected(false);
                            holder.img.setColorFilter(null);
                            holder.text.setVisibility(View.INVISIBLE);
                        } else {
//                            选中
                            //已经被选中的照片的显示逻辑
                            holder.selectFlag.setSelected(true);
                            holder.img.setColorFilter(0x80000000);
                            holder.text.setVisibility(View.VISIBLE);
                            holder.text.setText(mPicList.get(position).getSelect() + "");
                        }
                        Glide.with(mContext).load(mPicList.get(position).getPath()).into(holder.img);
                        break;
                    case "PATH":

                        //小圆点的点击逻辑


                        break;


//                    case "CANCLE":
//                        被取消选中

//                        break;

                    case "CHANGE":
//                        改变数据
                        holder.selectFlag.setSelected(true);
                        holder.img.setColorFilter(0x80000000);
                        holder.text.setVisibility(View.VISIBLE);
                        holder.text.setText(mPicList.get(position).getSelect() + "");
                        Log.d("============>", position + "   ");
                        break;
                    default:
                        break;
                }


            }

        }
    }

    public void bindItemView(final MyViewHolder holder, final int position) {

        //照片
        holder.video_time.setVisibility(View.GONE);
        holder.shader_text.setVisibility(View.GONE);

        //图片上的小圆点的逻辑
        if (maxCount >= 1) {
            holder.selectFlag.setVisibility(View.VISIBLE);
        } else {
            holder.selectFlag.setVisibility(View.GONE);
        }
        //图片赋值

        Glide.with(mContext).load(mPicList.get(position).getPath()).into(holder.img);
        //已经被选中的照片的显示逻辑
        if (mPicList.get(position).getSelect() > 0) {
            holder.selectFlag.setSelected(true);
            holder.img.setColorFilter(0x80000000);
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText(mPicList.get(position).getSelect() + "");
        } else {
            holder.selectFlag.setSelected(false);
            holder.img.setColorFilter(null);
            holder.text.setVisibility(View.INVISIBLE);
        }

        //小圆点的点击逻辑
        holder.selectFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mPicList.get(position).getSelect() > 0;
                if (isChecked) {
                    holder.selectFlag.setSelected(false);
                    holder.img.setColorFilter(null);
                    holder.text.setVisibility(View.INVISIBLE);

                    ////////////////////////////
                    ////////////////////////////
                    ////////////////////////////
                    ////////////////////////////
/**
 * 这三行／／  中间的部分可以完全不用做操作，直接在mDisplayAdapter.onImageCheckL( 的回调进行  adapter.notify 就可以，
 *
 *
 * 但是为了避免增加／取消选中会带来的recycleview刷新全部数据导致的闪动，采用局部刷新。暂时想到两种方式。目前还没考虑好哪一种方式更节省性能
 * 当然从逻辑理解上来说，毫无疑问方式1更简单方便，不用进行onBindViewHolder的操作。但是直觉上方式2能节省一丢丢性能，所以拿出来试一试，
 * 有两种方式，1，遍历整个mPiclist，新建一个newList，然后把更新的数据给到新的 newList，然后使用下方的  diff方法进行比较
 *           2，手动比较，单条刷新，使用notifyItemRangeChanged；此方式需要onBindViewHolder关注下操作。下述方式：
 *           2.1，改进方式2，依然手动刷新，在setmSelectImg时，取得这几个选中在整体相册列表中的位置，进行调整，同样采用
 *
 *
 *
 *           现在采用方式2.1，虽然并不知道能节省多少性能。。。
 *           总体思路已经在下面都加入了注释，总体按照如下思路进行操作
 *           1 如果当前是最后一位选中取消，直接取消不用做任何操作
 *           2 如果不是最后一位选中取消，那么取消选中的前几位不用操作，只需要操作取消位置取消选中状态，然后改变后续位置改变选中数字，刷新需要改动的数据部分即可
 *
 */

                    int index = mSelectedImage.indexOf(mPicList.get(position).getPath());//变动位置
                    mPicList.get(position).setSelect(0);
//                        在此处，所有的位置都需要进行调整，比如取消了第一个，那么后续所有的已选中都需要进行重新进行设置
//                        进行判断，如果是最后一位，就不用判断，直接取消选中就好了
//                        其他位置的话需要进行位置变更
                    if (index == mSelectedImage.size() - 1) {
//                        last position
//                        最后一位，只需要刷新当前itemview即可
                        mPicList.get(mSelectedImagePosition.get(index)).setSelect(0);
                        mSelectedImage.remove(mPicList.get(position).getPath());
                        mSelectedImagePosition.remove(new Integer(position));
                    } else {
//                            取消非末位的选中,考虑，不改变index之前的数据展示，只改变index之后，所以从index+1开始循环这个选中的mSelectImage
                        for (int i = index + 1; i < mSelectedImage.size(); i++) {
                            //取消选中之后的图片，需要调整其数字
                            //index位置是取消选中的位置，所以从下一位开始变动选中的数据，index往后的select数值减少1
                            mPicList.get(mSelectedImagePosition.get(i)).setSelect(i);//把ho
                            Bundle payload = new Bundle();
                            payload.putInt("CHANGE", mSelectedImagePosition.get(i));
                            //通知下一位置变化数据
                            adapter.notifyItemChanged(mSelectedImagePosition.get(i), payload);
                        }
                        //                            取消的选中的图片位置
                        //在mPicList中，删除点数据
                        if (index >= 0) {
                            mPicList.get(mSelectedImagePosition.get(index)).setSelect(0);
                        }
                        mSelectedImage.remove(mPicList.get(position).getPath());
                        mSelectedImagePosition.remove(new Integer(position));

                    }
                    ////////////////////////////
                    ////////////////////////////
                    ////////////////////////////
                    ////////////////////////////
                    ////////////////////////////
                } else {
                    if (mSelectedImage.size() >= maxCount) {
                        Toast.makeText(mContext.getApplicationContext(),
                                "不能超过" + maxCount + "张图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSelectedImage.add(mPicList.get(position).getPath());
                    mSelectedImagePosition.add(position);
                    mPicList.get(position).setSelect(mSelectedImage.size());
                    holder.text.setVisibility(View.VISIBLE);
                    holder.text.setText(mPicList.get(position).getSelect() + "");
                    holder.selectFlag.setSelected(true);
                    holder.img.setColorFilter(0x80000000);
                }

                mDisplayAdapter.onImageCheckL(mPicList.get(position).getPath(), !isChecked);

            }
        });

        //添加点击事件
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisplayAdapter.onItemClick(mContext,
                        mPicList.indexOf(mPicList.get(position)),
                        mPicList.get(position).getPath());
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera && position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return mPicList == null ? 0 : mPicList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView textFirst;
        TextView video_time;
        ImageView img;
        ImageView shader_text;
        ImageView imgFirst;
        ImageView selectFlag;
        SquareRelativeLayout camera;

        //实际上是，两种布局，为了懒省劲儿共用了一个holder。。。不过现在选择视频部分被先去掉了，所以看起来冗余了一部分
        public MyViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            video_time = (TextView) itemView.findViewById(R.id.video_time);
            img = (ImageView) itemView.findViewById(R.id.ic_image_pick);
            shader_text = (ImageView) itemView.findViewById(R.id.shader_text);
            selectFlag = (ImageView) itemView.findViewById(R.id.ic_check_box);
            //拍照／录像的view所包含的
            imgFirst = (ImageView) itemView.findViewById(R.id.camera_pick);
            camera = (SquareRelativeLayout) itemView.findViewById(R.id.camera_id);
            textFirst = (TextView) itemView.findViewById(R.id.camera_text);
        }
    }

    /**
     * 更新数据
     */
    public void notifyDataSetChanged(List<String> mList) {

        ArrayList<Photobean> list = new ArrayList();
        if (mList == null) {
            mList = new ArrayList<>();
        }

        if (isShowCamera) {
            if (mList.size()>0&&!TextUtils.isEmpty(mList.get(0))){
                mList.add(0, "");
            }
        }
        for (int i = 0; i < mList.size(); i++) {
            //当相册发生变动的时候，需要更新 mSelectedImagePosition 数据
            Photobean bean = new Photobean(0, "");
            if (mSelectedImage.contains(mList.get(i))) {
                bean.setSelect(mSelectedImage.indexOf(mList.get(i)) + 1);
                mSelectedImagePosition.add(i);
                bean.setPath(mList.get(i));
            } else {
                bean.setSelect(0);
                bean.setPath(mList.get(i));
            }
            list.add(bean);
        }


        diff(list, mPicList, this);
//        mPicList.clear();
//        mPicList.addAll(list);
//        adapter.notifyDataSetChanged();
    }

    /**
     * 更新视频数据
     */
    public void notifyVideoDtChanged(List<DisplayInfoBean> mList) {
        this.mVideoList.clear();
        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (isShowCamera) {
            mList.add(0, new DisplayInfoBean("", ""));
        }
        this.mVideoList.addAll(mList);
        this.notifyDataSetChanged();
    }

    //    时间类型转换
    public static String getMS(long cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        re_StrTime = sdf.format(new Date(cc_time));
        return re_StrTime;
    }

    /**
     * 比较数据进行局部刷新用，暂时先停掉了，效果不太理想
     * @param newList
     * @param oldList
     * @param photoVideoAdapter
     */
    private void diff(final ArrayList<Photobean> newList,
                      final ArrayList<Photobean> oldList, final PhotoVideoAdapter photoVideoAdapter) {
//        放到了工作线程做操作
        rx.Observable.create(new rx.Observable.OnSubscribe<DiffUtil.DiffResult>() {
            @Override
            public void call(Subscriber<? super DiffUtil.DiffResult> subscriber) {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffPhotoPickCallback(newList, oldList, 0));
                subscriber.onNext(diffResult);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<DiffUtil.DiffResult>() {
            @Override
            public void call(DiffUtil.DiffResult diffResult) {
                diffResult.dispatchUpdatesTo(photoVideoAdapter);
                mPicList.clear();
                mPicList.addAll(newList);
            }
        });


    }

}
