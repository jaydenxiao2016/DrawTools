package com.jayden.drawtool.bean;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.jayden.drawtool.ui.activity.MainActivity;


public class Picture {
    private int contentId;
    private Bitmap content;
    private PointF centerPoint;
    private PointF beginPoint;
    private String name;

    public Picture(int contentId, String name) {
        this.contentId = contentId;
        this.name = name;
    }

    public Picture(int contentId, PointF centerPoint, PointF beginPoint) {
        this.contentId = contentId;
        this.centerPoint = new PointF();
        (this.centerPoint).set(centerPoint);
        this.beginPoint = new PointF();
        (this.beginPoint).set(beginPoint);
    }

    public Bitmap createContent() {
        content = BitmapFactory.decodeResource(MainActivity.getContext().getResources(),
                contentId);
        return content;
    }


    public PointF getCenterPoint() {
        return centerPoint;
    }

    public PointF getBeginPoint() {
        return beginPoint;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public Bitmap getContent() {
        return content;
    }

    public void setContent(Bitmap content) {
        this.content = content;
    }

    public void setCenterPoint(PointF centerPoint) {
        this.centerPoint = centerPoint;
    }

    public void setBeginPoint(PointF beginPoint) {
        this.beginPoint = beginPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
