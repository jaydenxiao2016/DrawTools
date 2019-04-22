package com.jayden.drawtool.touch;

import android.graphics.PointF;
import android.util.Log;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.activity.MainActivity;
import com.jayden.drawtool.ui.view.CanvasView;

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
        ifNeedToOpenTools();

        PointF upPoint = new PointF();
        upPoint.set(curPoint);

        if (control == false) //非拉伸贝塞尔曲线操作则记录落脚点
        {
            endPoint.set(upPoint);//记录落脚点
            control = true;
        } else {
            newPel.closure = false;
            //路径组成的点
            newPel.pathPointFList.add(beginPoint);
            newPel.pathPointFList.add(movePoint);
            newPel.pathPointFList.add(endPoint);
            super.up(); //最终敲定
            control = false;
        }
    }

    public void ifNeedToOpenTools() {
        if (dis < 10f) {
            Log.v("v", "ttttttttttttttttttttt" );
            dis = 0;
            MainActivity.openTools();
            control = true;
            return;
        }
    }
}
