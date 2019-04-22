package com.jayden.drawtool.bean;

import android.graphics.Paint;
import android.graphics.Typeface;

import com.jayden.drawtool.touch.DrawTouch;


public class Text {
    /**
     * 文本内容
     */
    private String content;
    private Paint paint;

    public Text(String content) {
        this.content = content;
        paint = new Paint();
        paint.setColor(DrawTouch.getCurPaint().getColor());
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public String getContent() {
        return content;
    }


    public Paint getPaint() {
        return paint;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
