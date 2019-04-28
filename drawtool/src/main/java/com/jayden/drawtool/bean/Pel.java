package com.jayden.drawtool.bean;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;

import com.jayden.drawtool.Constant;
import com.jayden.drawtool.ui.view.CanvasView;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：Pel.java
 * 描述：图元类
 * 作者：xsf
 * 创建时间：2019/4/10
 * 最后修改时间：2019/4/10
 */
public class Pel extends BasePel {
    /**
     * 区域
     */
    public Region region;
    /**
     * /画笔
     */
    public Paint paint;
    /**
     * 路径
     */
    public Path path;
    /**
     * 类型
     * path类型：10:自由线 11：矩型 12：塞尔曲线 13：圆 14：直线 15：多重折线 16：多边形
     * 文本类型：20
     * 照片类型：30
     */
    public int type;
    /**
     * 组成path的所有点
     */
    public List<PointF> pathPointFList;
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
        pathPointFList = new ArrayList<>();
        path = new Path();
        region = new Region();
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.parseColor(Constant.PAINT_DEFAULT_COLOR));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(Constant.PAINT_DEFAULT_STROKE_WIDTH);
        paint.setTextSize(Constant.PAINT_DEFAULT_TEXT_SIZE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        centerPoint = new PointF();
        beginPoint = new PointF();
        bottomRightPointF = new PointF();
        text = null;
        picture = null;
    }

    //深拷贝
    public Pel clone() {
        Pel pel = new Pel();
        (pel.path).set(path);
        (pel.region).set(new Region(region));
        (pel.paint).set(new Paint(paint));
        if (text != null) {
            pel.text = new Text(text.getContent(), text.isVertical());
        }
        if (picture != null) {
            pel.picture = new Picture(picture.getContentId());
            pel.picture.createContent();
        }
        pel.type = type;
        pel.pathPointFList = pathPointFList;
        pel.bottomRightPointF = bottomRightPointF;
        pel.centerPoint = centerPoint;
        pel.beginPoint = beginPoint;
        pel.transDx = transDx;
        pel.transDy = transDy;
        pel.scale = scale;
        pel.degree = degree;
        pel.paintColor = paintColor;
        pel.paintStrokeWidth = paintStrokeWidth;
        pel.closure = closure;
        return pel;
    }

    /**
     * 在画布上作画
     *
     * @param canvas
     */
    public void drawObject(Canvas canvas) {
        //文本图元
        if (text != null) {
            canvas.save();
            canvas.translate(transDx, transDy);
            canvas.scale(scale, scale, centerPoint.x, centerPoint.y);
            canvas.rotate(degree, centerPoint.x, centerPoint.y);
            if (!text.isVertical()) {
                canvas.drawText(text.getContent(), beginPoint.x, beginPoint.y, text.getPaint());
            } else {
                int heightPer = (region.getBounds().bottom - region.getBounds().top - 30) / text.getContent().length();
                char[] chars = text.getContent().toCharArray();
                for (int i = chars.length - 1; i >= 0; i--) {
                    canvas.drawText(chars[i] + "", beginPoint.x, beginPoint.y - heightPer * (chars.length - 1 - i), text.getPaint());
                }
            }
            canvas.restore();
        }
        //图标图元
        else if (picture != null) {
            canvas.save();
            canvas.translate(transDx, transDy);
            canvas.scale(scale, scale, centerPoint.x, centerPoint.y);
            canvas.rotate(degree, centerPoint.x, centerPoint.y);
            canvas.drawBitmap(picture.createContent(), beginPoint.x, beginPoint.y, CanvasView.drawPicturePaint);
            canvas.restore();
        }
        //路径
        else {
            canvas.save();
            canvas.translate(transDx, transDy);
            canvas.scale(scale, scale, centerPoint.x, centerPoint.y);
            canvas.rotate(degree, centerPoint.x, centerPoint.y);
            canvas.drawPath(path, paint);
            canvas.restore();
        }

    }
}
