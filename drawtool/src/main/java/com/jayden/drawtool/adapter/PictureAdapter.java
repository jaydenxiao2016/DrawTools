package com.jayden.drawtool.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jayden.drawtool.R;
import com.jayden.drawtool.bean.Picture;

/**
 * 类名：PictureAdapter.java
 * 描述：图片adapter
 * 作者：xsf
 * 创建时间：2019/4/17
 * 最后修改时间：2019/4/17
 */
public class PictureAdapter extends BaseAblistViewAdapter<Picture> {

    public PictureAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_picture, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.tv_name);
            viewHolder.ivPicture = (ImageView) convertView
                    .findViewById(R.id.iv_picture);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picture item = getItem(position);
        viewHolder.tvName.setText(item.getName());
        viewHolder.ivPicture.setImageResource(item.getContentId());
        return convertView;
    }

    private final class ViewHolder {
        ImageView ivPicture;
        TextView tvName;
    }
}
