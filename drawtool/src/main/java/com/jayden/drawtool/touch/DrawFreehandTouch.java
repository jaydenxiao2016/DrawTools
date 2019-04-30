package com.jayden.drawtool.touch;

import android.graphics.PointF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 手绘图元
 */
public class DrawFreehandTouch extends DrawTouch {
    private PointF lastPoint;

    public DrawFreehandTouch() {
        super();
        lastPoint = new PointF();
    }

    @Override
    public void down1() {
        super.down1();
        lastPoint.set(downPoint);

        newPel = new Pel();
        newPel.type = 10;
        (newPel.path).moveTo(lastPoint.x, lastPoint.y);
        //路径组成的点
        newPel.pathPointFList.add(new PointF(lastPoint.x, lastPoint.y));
    }

    @Override
    public void move() {
        super.move();
        movePoint.set(curPoint);
        if (dis > 10) {
            //贝塞尔曲线
            (newPel.path).quadTo(lastPoint.x, lastPoint.y, (lastPoint.x + movePoint.x) / 2, (lastPoint.y + movePoint.y) / 2);
            //路径组成的点
            newPel.pathPointFList.add(new PointF(lastPoint.x, lastPoint.y));
            newPel.pathPointFList.add(new PointF((lastPoint.x + movePoint.x) / 2, (lastPoint.y + movePoint.y) / 2));

            lastPoint.set(movePoint);
            CanvasView.setSelectedPel(selectedPel = newPel);
        }
    }

    @Override
    public void up() {
        newPel.closure = true;
        super.up();
    }

    /**
     * 构造手绘pel
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
        pel.type = 10;
        if (pathPointFList != null && pathPointFList.size() > 1) {
            pel.pathPointFList = pathPointFList;
            (pel.path).moveTo(pathPointFList.get(0).x, pathPointFList.get(0).y);
            for (int i = 1; i < pel.pathPointFList.size(); i += 2) {
                (pel.path).quadTo(pathPointFList.get(i).x, pathPointFList.get(i).y, pathPointFList.get(i + 1).x, pathPointFList.get(i + 1).y);
            }
        }
        return pel;
    }
}
