package com.jayden.drawtool.bean;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;

import com.jayden.drawtool.touch.DrawTouch;


public class Text {
    /**
     * 文本内容
     */
    private String content;
    /**
     * 中心点
     */
    private PointF centerPoint;
    /**
     * 绘图开始点坐标
     */
    private PointF beginPoint;
    private Paint paint;

    public Text(String content, PointF centerPoint, PointF beginPoint) {
        this.content = content;
        this.centerPoint = new PointF();
        (this.centerPoint).set(centerPoint);
        this.beginPoint = new PointF();
        (this.beginPoint).set(beginPoint);
        paint = new Paint();
        paint.setColor(DrawTouch.getCurPaint().getColor());
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public String getContent() {
        return content;
    }

    public PointF getCenterPoint() {
        return centerPoint;
    }

    public PointF getBeginPoint() {
        return beginPoint;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCenterPoint(PointF centerPoint) {
        this.centerPoint = centerPoint;
    }

    public void setBeginPoint(PointF beginPoint) {
        this.beginPoint = beginPoint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
