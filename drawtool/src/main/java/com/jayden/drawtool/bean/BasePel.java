package com.jayden.drawtool.bean;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * 类名：BasePel.java
 * 描述：
 * 作者：xsf
 * 创建时间：2019/4/12
 * 最后修改时间：2019/4/12
 */
public class BasePel {
    /**
     * 以下四个点主要用于选中编辑画动画边框，选中会重新初始化
     */
    /**
     * 左上角
     */
    public PointF leftTopPoint;
    /**
     * 右上角
     */
    public PointF rightTopPoint;
    /**
     * 右下角
     */
    public PointF rigintBottomPoint;
    /**
     * 左下角
     */
    public PointF leftBottomPoint;
    /**
     * 拖动图标坐标
     */
    public RectF dragBtnRect;
    /**
     * 拖动图标
     */
    public Bitmap dragBitmap;


    /**
     * x偏移
     */
    public float transDx;
    /**
     * y偏移
     */
    public float transDy;
    /**
     * 放大倍数
     */
    public float scale=1;
    /**
     * 旋转角度
     */
    public float degree;

}
