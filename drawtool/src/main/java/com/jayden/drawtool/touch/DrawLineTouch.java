package com.jayden.drawtool.touch;

import android.graphics.PointF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

/**
 * 画线
 */
public class DrawLineTouch extends DrawTouch {

    public DrawLineTouch() {
        super();
    }

    @Override
    public void move() {
        super.move();

        newPel = new Pel();
        newPel.type=14;
        movePoint.set(curPoint);

        (newPel.path).moveTo(downPoint.x, downPoint.y);
        (newPel.path).lineTo(movePoint.x, movePoint.y);
        (newPel.path).lineTo(movePoint.x, movePoint.y + 1);

        CanvasView.setSelectedPel(selectedPel = newPel);
    }

    @Override
    public void up() {
        newPel.closure = false;
        //路径组成的点
        newPel.pathPointFList.add(new PointF(downPoint.x, downPoint.y));
        newPel.pathPointFList.add(new PointF(movePoint.x, movePoint.y));
        newPel.pathPointFList.add(new PointF(movePoint.x, movePoint.y + 1));
        super.up();
    }
}
