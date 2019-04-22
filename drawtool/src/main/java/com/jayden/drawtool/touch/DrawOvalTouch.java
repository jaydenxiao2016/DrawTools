package com.jayden.drawtool.touch;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

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
        newPel.type=13;
        movePoint.set(curPoint);

        (newPel.path).addOval(new RectF(downPoint.x, downPoint.y, movePoint.x, movePoint.y), Path.Direction.CCW);

        CanvasView.setSelectedPel(selectedPel = newPel);
    }

    @Override
    public void up() {
        newPel.closure = true;
        //路径组成的点
        newPel.pathPointFList.add(downPoint);
        newPel.pathPointFList.add(movePoint);
        super.up();
    }
}
