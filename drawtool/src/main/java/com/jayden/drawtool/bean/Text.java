package com.jayden.drawtool.bean;

import android.graphics.Paint;
import android.graphics.Typeface;

import com.jayden.drawtool.Constant;
import com.jayden.drawtool.touch.DrawTouch;


public class Text {
    /**
     * 文本内容
     */
    private String content;
    /**
     * 文本画笔
     */
    private Paint paint;
    /**
     * 是否垂直显示
     */
    private boolean isVertical;

    public Text(String content,boolean isVertical) {
        this.content = content;
        this.isVertical = isVertical;
        paint = new Paint();
        paint.setColor(DrawTouch.getCurPaint().getColor());
        paint.setTextSize(Constant.PAINT_DEFAULT_TEXT_SIZE);
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

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
