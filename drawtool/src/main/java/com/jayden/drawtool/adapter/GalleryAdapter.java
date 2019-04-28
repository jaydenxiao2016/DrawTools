package com.jayden.drawtool.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jayden.drawtool.R;

/**
 * 类名：GalleryAdapter.java
 * 描述：画廊适配器
 * 作者：xsf
 * 创建时间：2019/4/23
 * 最后修改时间：2019/4/23
 */
public class GalleryAdapter extends BaseAblistViewAdapter<String> {
    private onClickGalleryListener onClickGalleryListener;

    public GalleryAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_gallary, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.ivPicture = (ImageView) convertView
                    .findViewById(R.id.iv_picture);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(getItem(position)).into(viewHolder.ivPicture);
        viewHolder.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickGalleryListener != null) {
                    onClickGalleryListener.onClick(position);
                }
            }
        });
        return convertView;
    }

    private final class ViewHolder {
        ImageView ivPicture;
    }

    public void setOnClickGalleryListener(onClickGalleryListener onClickGalleryListener) {
        this.onClickGalleryListener = onClickGalleryListener;
    }

    public interface onClickGalleryListener {
        void onClick(int position);
    }

}
