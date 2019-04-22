package com.jayden.drawtool.bean;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jayden.drawtool.ui.activity.MainActivity;


public class Picture {
    private int contentId;
    private Bitmap content;
    private String name;

    public Picture(int contentId, String name) {
        this.contentId = contentId;
        this.name = name;
    }

    public Picture(int contentId) {
        this.contentId = contentId;
    }

    public Bitmap createContent() {
        content = BitmapFactory.decodeResource(MainActivity.getContext().getResources(),
                contentId);
        return content;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
