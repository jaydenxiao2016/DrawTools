package com.jayden.drawtool.bean;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;

/**
 * 类名：Pel.java
 * 描述：图元类
 * 作者：xsf
 * 创建时间：2019/4/10
 * 最后修改时间：2019/4/10
 */
public class Pel extends BasePel {
    /**
     * 路径
     */
    public Path path;
    /**
     * 区域
     */
    public Region region;
    /**
     * /画笔
     */
    public Paint paint;
    /**
     * 文本
     */
    public Text text;
    /**
     * 插画
     */
    public Picture picture;
    /**
     * 是否封闭
     */
    public boolean closure;

    //构造（实际使用时应该把Pel构造成Pel(path region paint name)的形式，形参均在外部都已经定义好了的）
    public Pel() {
        path = new Path();
        region = new Region();
        paint = new Paint();
        text = null;
        picture = null;
    }

    //深拷贝
    public Pel clone() {
        Pel pel = new Pel();
        (pel.path).set(path);
        (pel.region).set(new Region(region));
        (pel.paint).set(new Paint(paint));
        if(text!=null) {
            pel.text = new Text(text.getContent(), text.getCenterPoint(), text.getBeginPoint());
        }
        if(picture!=null) {
            pel.picture = new Picture(picture.getContentId(), picture.getCenterPoint(), picture.getBeginPoint());
            pel.picture.createContent();
        }
        pel.transDx = transDx;
        pel.transDy = transDy;
        pel.scale = scale;
        pel.degree = degree;
        pel.closure = closure;

        return pel;
    }
}
