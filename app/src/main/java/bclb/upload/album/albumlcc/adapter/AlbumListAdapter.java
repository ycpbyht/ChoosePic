package bclb.upload.album.albumlcc.adapter;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bclb.upload.album.albumlcc.bean.AlbumModel;
import ycpb.upload.myapplication.R;


public class AlbumListAdapter extends BaseAdapter {
    private List<AlbumModel> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> name;

    public AlbumListAdapter(List<AlbumModel> list, Context context, ArrayList<String> name) {
        if (list == null) {
            list = new ArrayList<>();
        }
        mList = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.name = name;
    }

    public AlbumListAdapter(Context context, ArrayList<String> name) {
        this(null, context, name);

    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public AlbumModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list__albums_item, parent, false);
            holder = new ViewHolder();
            holder.albumImage = (ImageView) convertView.findViewById(R.id.iv_album);
            holder.albumName = (TextView) convertView.findViewById(R.id.tv_album_name);
            holder.albumNum = (TextView) convertView.findViewById(R.id.tv_album_num);
            holder.check = (ImageView) convertView.findViewById(R.id.album_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AlbumModel model = getItem(position);
        if (name != null && name.size() > 0) {
            if (name.get(0).equals(model.getName())) {
                holder.check.setSelected(true);
                convertView.setBackgroundColor(Color.parseColor("#F0F0F0"));
            } else {
                holder.check.setSelected(false);
                convertView.setBackgroundColor(Color.WHITE);
            }
        } else {
            if (position == 0) {
                holder.check.setSelected(true);
                convertView.setBackgroundColor(Color.parseColor("#F0F0F0"));
            } else {
                holder.check.setSelected(false);
                convertView.setBackgroundColor(Color.WHITE);
            }
        }


        holder.albumName.setText(model.getName());
        if (model.getRecent().endsWith(".mp4")) {
            Glide.with(mContext).load(Uri.fromFile(new File(model.getRecent()))).into(holder.albumImage);
            holder.albumNum.setText(model.getCount() + mContext.getString(R.string.howmany_video));
        } else {
            Glide.with(mContext).load(model.getRecent()).into(holder.albumImage);
            holder.albumNum.setText(model.getCount() + mContext.getString(R.string.howmany_pic));

        }
        return convertView;
    }

    class ViewHolder {
        ImageView albumImage;
        TextView albumName;
        TextView albumNum;
        ImageView check;
    }


    /**
     * 更新数据
     */
    public void notifyDataSetChanged(List<AlbumModel> models) {
        if (null != models && models.size() != 0) {
            this.mList.addAll(models);
            this.notifyDataSetChanged();
        } else {
//            空数据
            this.mList.clear();
            this.notifyDataSetChanged();
            Toast.makeText(mContext, mContext.getString(R.string.photo_empty), Toast.LENGTH_SHORT).show();

        }
    }

}
