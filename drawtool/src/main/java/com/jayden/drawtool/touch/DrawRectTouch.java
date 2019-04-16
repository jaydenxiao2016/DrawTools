package com.jayden.drawtool.touch;

import android.graphics.Path;
import android.graphics.RectF;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.CanvasView;

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

		movePoint.set(curPoint);

		(newPel.path).addRect(new RectF(downPoint.x, downPoint.y, movePoint.x, movePoint.y), Path.Direction.CCW);

		CanvasView.setSelectedPel(selectedPel = newPel);
	}
	
	@Override
	public void up()
	{
		if(newPel!=null) {
			newPel.closure = true;
		}
		super.up();
	}	
}
