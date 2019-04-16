package com.jayden.drawtool.touch;

import android.graphics.Path;
import android.graphics.RectF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.CanvasView;

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

        movePoint.set(curPoint);

        (newPel.path).addOval(new RectF(downPoint.x, downPoint.y, movePoint.x, movePoint.y), Path.Direction.CCW);

        CanvasView.setSelectedPel(selectedPel = newPel);
    }

    @Override
    public void up() {
        newPel.closure = true;
        super.up();
    }
}
