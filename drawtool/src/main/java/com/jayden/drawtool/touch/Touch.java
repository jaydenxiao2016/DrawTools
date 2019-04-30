package com.jayden.drawtool.touch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Region;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.step.Step;
import com.jayden.drawtool.ui.activity.DrawMainActivity;
import com.jayden.drawtool.ui.view.CanvasView;

import java.util.ListIterator;

/**
 * 类名：Touch.java
 * 描述：触摸类
 * 作者：xsf
 * 创建时间：2019/4/10
 * 最后修改时间：2019/4/10
 */
public class Touch {
    /**
     * 画布裁剪区域
     */
    protected static Region clipRegion = CanvasView.getClipRegion();
    /**
     * 当前选中图元
     */
    protected Pel selectedPel;
    /**
     * 当前重绘位图
     */
    protected Bitmap savedBitmap;
    /**
     * 重绘画布
     */
    protected Canvas savedCanvas;

    public PointF curPoint; //当前第一只手指事件坐标
    public PointF secPoint; //当前第二只手指事件坐标
    public static Step step = null; //当前touch事件结束以后将要压入undo栈的步骤
    public float dis;//整个触摸过程在x和y方向上的偏移总量
    protected PointF frontPoint1, frontPoint2;//上一个事件坐标

    //特殊处理用
    public boolean control = false; //贝塞尔曲线切换时敲定
    public PointF beginPoint;//多边形时敲定
    public boolean hasFinished = false;
    public static Matrix oriMatrix;//浏览图片的初始因子

    /*
     * 需继承的方法
     */
    public Touch() {
        selectedPel = CanvasView.getSelectedPel();

        savedCanvas = new Canvas();
        curPoint = new PointF();
        secPoint = new PointF();
        beginPoint = new PointF();
        frontPoint1 = new PointF();
        frontPoint2 = new PointF();
        dis = 0;
    }

    // 第一只手指按下
    public void down1() {
        dis = 0;
        frontPoint1.set(curPoint);
    }

    // 第二只手指按下
    public void down2() {
        dis = 0;
        frontPoint2.set(secPoint);
    }

    // 手指移动
    public void move() {
        float dis1 = Math.abs(curPoint.x - frontPoint1.x) + Math.abs(curPoint.y - frontPoint1.y);
        float dis2 = 0;

        if (secPoint != null) {
            dis2 = Math.abs(secPoint.x - frontPoint2.x) + Math.abs(secPoint.y - frontPoint2.y);
            frontPoint2.set(secPoint);
        }
        dis += dis1 + dis2;

        frontPoint1.set(curPoint);
    }

    // 手指抬起
    public void up() {
        if (dis < 10f) {
            dis = 0;
            DrawMainActivity.openOrCloseTools();
            return;
        }
        dis = 0;
    }

    //更新重绘背景位图用（当且仅当选择的图元有变化的时候才调用）
    protected void updateSavedBitmap() {
        //创建缓冲位图
        Bitmap backgroundBitmap = CanvasView.getBackgroundBitmap();
        CanvasView.ensureBitmapRecycled(savedBitmap);
        savedBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);//由画布背景创建缓冲位图
        savedCanvas.setBitmap(savedBitmap); //与画布建立联系

        drawPels();

        CanvasView.setSavedBitmap(savedBitmap); // 改变CanvasView中的savedBitmap方便更新
    }

    public void drawPels() {
        ListIterator<Pel> pelIterator = CanvasView.getPelList().listIterator();// 获取pelList对应的迭代器头结点
        while (pelIterator.hasNext()) {
            Pel pel = pelIterator.next();
            if (!pel.equals(selectedPel)) {
                pel.drawObject(savedCanvas);
            }
        }
    }

    public void setCurPoint(PointF point) {
        curPoint.set(point);
    }

    public void setSecPoint(PointF point) {
        secPoint.set(point);
    }

    public static Step getStep() //返回当前步骤包含的操作
    {
        return step;
    }

    public void setSelectedPel(Pel selectedPel) {
        this.selectedPel = selectedPel;
    }
}

