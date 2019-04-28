package com.jayden.drawtool.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jayden.drawtool.R;
import com.jayden.drawtool.adapter.PictureAdapter;
import com.jayden.drawtool.bean.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片图元选择对话框
 */
public class PictureDialog extends Dialog {
    private OnClickPictureListener onClickPictureListener;
    private GridView gridView;
    private PictureAdapter adapter;

    // 构造函数
    public PictureDialog(Context context, int theme) {
        super(context, theme);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_picture);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        initView();
        initData();
    }

    // 初始化界面对象
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridView);
    }

    // 初始化数据
    private void initData() {
        List<Picture> pictureList = new ArrayList<>();
        pictureList.add(new Picture(R.drawable.ic_picture_arrow, "箭头"));
        pictureList.add(new Picture(R.drawable.ic_picture_circle, "环路"));
        pictureList.add(new Picture(R.drawable.ic_picture_y_road, "Y型路"));
        pictureList.add(new Picture(R.drawable.ic_picture_crossroads, "十字路"));
        pictureList.add(new Picture(R.drawable.ic_picture_three_road, "三向路"));
//        pictureList.add(new Picture(R.drawable.ic_picture_1, "电阻器"));
//        pictureList.add(new Picture(R.drawable.ic_picture_2, "电位器"));
        adapter = new PictureAdapter(getContext());
        adapter.setData(pictureList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Picture item = adapter.getItem(position);
                if (onClickPictureListener != null) {
                    onClickPictureListener.conClickContent(item.getContentId());
                }
                dismiss();
            }
        });
    }

    public void setOnClickPictureListener(OnClickPictureListener onClickPictureListener) {
        this.onClickPictureListener = onClickPictureListener;
    }

    public interface OnClickPictureListener {
        void conClickContent(int contentId);
    }
}
