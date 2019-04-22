package com.jayden.drawtool.touch;

import android.graphics.Path;
import android.graphics.RectF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

/**
 * 画矩阵
 */
public class DrawRectTouch extends DrawTouch {

	public DrawRectTouch() 
	{
		super();
	}

	@Override
	public void move() {
		super.move();
		
		newPel = new Pel();
		newPel.type=11;
		movePoint.set(curPoint);

		(newPel.path).addRect(new RectF(downPoint.x, downPoint.y, movePoint.x, movePoint.y), Path.Direction.CCW);

		CanvasView.setSelectedPel(selectedPel = newPel);
	}
	
	@Override
	public void up()
	{
		if(newPel!=null) {
			//路径组成的点
			newPel.pathPointFList.add(downPoint);
			newPel.pathPointFList.add(movePoint);
			newPel.closure = true;
		}
		super.up();
	}	
}
