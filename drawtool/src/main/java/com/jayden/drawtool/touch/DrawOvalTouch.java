package com.jayden.drawtool.touch;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 画圆
 */
public class DrawOvalTouch extends DrawTouch {

    public DrawOvalTouch() {
        super();
    }

    @Override
    public void move() {
        super.move();

        newPel = new Pel();
        newPel.type = 13;
        movePoint.set(curPoint);

        (newPel.path).addOval(new RectF(downPoint.x, downPoint.y, movePoint.x, movePoint.y), Path.Direction.CCW);

        CanvasView.setSelectedPel(selectedPel = newPel);
    }

    @Override
    public void up() {
        //路径组成的点
        if ((downPoint.x != curPoint.x || downPoint.y != curPoint.y) && newPel != null) {
            newPel.closure = true;
            newPel.pathPointFList.add(new PointF(downPoint.x, downPoint.y));
            newPel.pathPointFList.add(new PointF(movePoint.x, movePoint.y));
        }
        super.up();
    }

    /**
     * 构造圆pel
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
        pel.type = 13;
        if (pathPointFList != null && pathPointFList.size() == 2) {
            pel.pathPointFList = pathPointFList;
            (pel.path).addOval(new RectF(pathPointFList.get(0).x, pathPointFList.get(0).y,
                    pathPointFList.get(1).x, pathPointFList.get(1).y), Path.Direction.CCW);
        }
        return pel;
    }
}
