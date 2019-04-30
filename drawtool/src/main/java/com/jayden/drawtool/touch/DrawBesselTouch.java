package com.jayden.drawtool.touch;

import android.graphics.PointF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 画贝塞尔曲线
 */
public class DrawBesselTouch extends DrawTouch {
    private PointF beginPoint, endPoint;

    public DrawBesselTouch() {
        super();

        beginPoint = new PointF();
        endPoint = new PointF();
    }

    @Override
    public void down1() {
        super.down1();

        if (control == false) //非拉伸曲线操作表明是新图元的开端
        {
            beginPoint.set(downPoint); //记录起点
            newPel = new Pel();
            newPel.type = 12;
        }
    }

    @Override
    public void move() {
        super.move();

        movePoint.set(curPoint);

        (newPel.path).reset();
        if (control == false) //非拉伸贝塞尔曲线操作
        {
            (newPel.path).moveTo(beginPoint.x, beginPoint.y);
            (newPel.path).cubicTo(beginPoint.x, beginPoint.y, beginPoint.x, beginPoint.y, movePoint.x, movePoint.y);
        } else {
            (newPel.path).moveTo(beginPoint.x, beginPoint.y);
            (newPel.path).cubicTo(beginPoint.x, beginPoint.y, movePoint.x, movePoint.y, endPoint.x, endPoint.y);
        }

        CanvasView.setSelectedPel(selectedPel = newPel);
    }

    @Override
    public void up() {
        if (dis < 10f) {
            super.up();
            control=false;
            return;
        }
        PointF upPoint = new PointF();
        upPoint.set(curPoint);

        if (control == false) //非拉伸贝塞尔曲线操作则记录落脚点
        {
            endPoint.set(upPoint);//记录落脚点
            control = true;
        } else {
            newPel.closure = true;
            //路径组成的点
            newPel.pathPointFList.add(new PointF(beginPoint.x, beginPoint.y));
            newPel.pathPointFList.add(new PointF(movePoint.x, movePoint.y));
            newPel.pathPointFList.add(new PointF(endPoint.x, endPoint.y));
            super.up(); //最终敲定
            control = false;
        }
        movePoint.set(beginPoint);
    }


    /**
     * 构造贝塞尔曲线pel
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
        pel.type = 12;
        if (pathPointFList != null && pathPointFList.size() == 3) {
            pel.pathPointFList = pathPointFList;
            (pel.path).moveTo(pathPointFList.get(0).x, pathPointFList.get(0).y);
            (pel.path).cubicTo(pathPointFList.get(0).x, pathPointFList.get(0).y,
                    pathPointFList.get(1).x, pathPointFList.get(1).y,
                    pathPointFList.get(2).x, pathPointFList.get(2).y);
        }
        return pel;
    }
}
