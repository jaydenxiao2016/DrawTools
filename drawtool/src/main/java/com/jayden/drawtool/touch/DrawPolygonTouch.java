package com.jayden.drawtool.touch;

import android.graphics.Path;
import android.graphics.PointF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 画多边形
 */
public class DrawPolygonTouch extends DrawTouch {
    protected boolean firstDown = true;
    protected Path lastPath;

    protected final float MAX_CIRCLE = 50;

    public DrawPolygonTouch() {
        super();
        lastPath = new Path();
    }

    @Override
    public void down1() {
        super.down1();

        if (firstDown == true)// 画折线的第一笔
        {
            beginPoint.set(downPoint);

            newPel = new Pel();
            newPel.type = 16;
            (newPel.path).moveTo(beginPoint.x, beginPoint.y);
            lastPath.set(newPel.path);
            //路径组成的点
            newPel.pathPointFList.add(new PointF(beginPoint.x, beginPoint.y));
            firstDown = false;
        }
    }

    @Override
    public void move() {
        super.move();

        movePoint.set(curPoint);

        (newPel.path).set(lastPath);
        (newPel.path).lineTo(movePoint.x, movePoint.y);

        CanvasView.setSelectedPel(selectedPel = newPel);
    }

    @Override
    public void up() {
        if (isNeedToOpenTools() == true) {
            return;
        } else {
            PointF endPoint = new PointF();
            endPoint.set(curPoint);

            if (distance(beginPoint, endPoint) <= MAX_CIRCLE) {
                (newPel.path).set(lastPath);
                (newPel.path).close();
                //路径组成的点
                newPel.pathPointFList.add(new PointF(beginPoint.x, beginPoint.y));
                newPel.closure = true;
                super.up();

                firstDown = true;
            }
            // //手指移动才生效
            if (downPoint.x != curPoint.x || downPoint.y != curPoint.y) {
                //路径组成的点
                if (newPel != null) {
                    newPel.pathPointFList.add(new PointF(endPoint.x, endPoint.y));
                    lastPath.set(newPel.path);
                }
            }
        }
    }

    public boolean isNeedToOpenTools() {
        if (dis < 10f) {
            dis = 0;
            return true;
        } else {
            return false;
        }
    }

    // 计算up与最先down下的距离是否在领域内
    public float distance(PointF begin, PointF end) {
        float x = begin.x - end.x;
        float y = begin.y - end.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 构造折线pel
     *
     * @param in
     * @return
     */
    public static Pel loadPel(DataInputStream in) throws Exception {
        //点总数
        int pointSize = in.readInt();
        List<PointF> pathPointFList = new ArrayList<>();
        //点坐标
        for (int i = 0; i < pointSize; i++) {
            Float x = in.readFloat();
            Float y = in.readFloat();
            pathPointFList.add(new PointF(x, y));
        }
        Pel pel = new Pel();
        pel.type = 16;
        if (pathPointFList != null && pathPointFList.size() > 1) {
            pel.pathPointFList = pathPointFList;
            (pel.path).moveTo(pathPointFList.get(0).x, pathPointFList.get(0).y);
            for (int i = 1; i < pathPointFList.size(); i++) {
                (pel.path).lineTo(pathPointFList.get(i).x, pathPointFList.get(i).y);
            }
        }
        return pel;
    }
}
