package com.jayden.drawtool.touch;

import android.graphics.PointF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 画线
 */
public class DrawLineTouch extends DrawTouch {

    public DrawLineTouch() {
        super();
    }
    @Override
    public void down1()
    {
        super.down1();
        newPel=null;
    }
    @Override
    public void move() {
        super.move();

        newPel = new Pel();
        newPel.type = 14;
        movePoint.set(curPoint);

        (newPel.path).moveTo(downPoint.x, downPoint.y);
        (newPel.path).lineTo(movePoint.x, movePoint.y);
        (newPel.path).lineTo(movePoint.x, movePoint.y + 1);

        CanvasView.setSelectedPel(selectedPel = newPel);
    }

    @Override
    public void up() {
        if(newPel!=null) {
            newPel.closure = false;
            //路径组成的点
            newPel.pathPointFList.add(new PointF(downPoint.x, downPoint.y));
            newPel.pathPointFList.add(new PointF(movePoint.x, movePoint.y));
            newPel.pathPointFList.add(new PointF(movePoint.x, movePoint.y + 1));
        }
        super.up();
    }

    /**
     * 构造直线pel
     * @param in
     * @return
     */
    public static Pel loadPel(DataInputStream in) throws Exception{
        //点总数
        int pointSize = in.readInt();
        List<PointF>pathPointFList=new ArrayList<>();
        //点坐标
        for (int i = 0; i < pointSize; i++) {
            Float x = in.readFloat();
            Float y = in.readFloat();
            pathPointFList.add(new PointF(x, y));
        }
        Pel pel = new Pel();
        pel.type = 14;
        if (pathPointFList != null && pathPointFList.size() == 3) {
            pel.pathPointFList = pathPointFList;
            (pel.path).moveTo(pathPointFList.get(0).x, pathPointFList.get(0).y);
            (pel.path).lineTo(pathPointFList.get(1).x, pathPointFList.get(1).y);
            (pel.path).lineTo(pathPointFList.get(2).x, pathPointFList.get(2).y + 1);
        }
        return pel;
    }
}
