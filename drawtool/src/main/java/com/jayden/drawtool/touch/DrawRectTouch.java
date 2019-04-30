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
 * 画矩阵
 */
public class DrawRectTouch extends DrawTouch {

    public DrawRectTouch() {
        super();
    }

    @Override
    public void move() {
        super.move();
        movePoint.set(curPoint);
        if (dis > 10) {
            newPel = new Pel();
            newPel.type = 11;

            (newPel.path).addRect(new RectF(downPoint.x, downPoint.y, movePoint.x, movePoint.y), Path.Direction.CCW);

            CanvasView.setSelectedPel(selectedPel = newPel);
        }
    }

    @Override
    public void up() {
        //路径组成的点
        if ((downPoint.x != curPoint.x || downPoint.y != curPoint.y) && newPel != null) {
            newPel.pathPointFList.add(new PointF(downPoint.x, downPoint.y));
            newPel.pathPointFList.add(new PointF(movePoint.x, movePoint.y));
            newPel.closure = true;
        }
        super.up();
    }

    /**
     * 构造矩阵pel
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
        pel.type = 11;
        if (pathPointFList != null && pathPointFList.size() == 2) {
            pel.pathPointFList = pathPointFList;
            (pel.path).addRect(new RectF(pathPointFList.get(0).x, pathPointFList.get(0).y,
                    pathPointFList.get(1).x, pathPointFList.get(1).y), Path.Direction.CCW);
        }
        return pel;
    }
}
