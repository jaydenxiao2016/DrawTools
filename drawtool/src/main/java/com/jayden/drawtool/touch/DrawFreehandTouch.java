package com.jayden.drawtool.touch;

import android.graphics.PointF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.CanvasView;


/**
 * 手绘图元
 */
public class DrawFreehandTouch extends DrawTouch 
{
	private PointF lastPoint;
	
	public DrawFreehandTouch()
	{
		super();
		lastPoint=new PointF();
	}

	@Override
	public void down1()
	{
		super.down1();
		lastPoint.set(downPoint);

		newPel=new Pel();
		(newPel.path).moveTo(lastPoint.x, lastPoint.y);
	}

	@Override
	public void move() {
		super.move();
		
		movePoint.set(curPoint);
		//贝塞尔曲线
		(newPel.path).quadTo(lastPoint.x,lastPoint.y, (lastPoint.x+movePoint.x)/2, (lastPoint.y+movePoint.y)/2);
		lastPoint.set(movePoint);
		
		CanvasView.setSelectedPel(selectedPel = newPel);
	}
	
	@Override
	public void up()
	{
		newPel.closure=true;
		super.up();
	}
}
