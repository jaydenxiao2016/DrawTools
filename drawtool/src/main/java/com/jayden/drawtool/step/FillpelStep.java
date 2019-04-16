package com.jayden.drawtool.step;

import android.graphics.Paint;

import com.jayden.drawtool.bean.Pel;


//填充图元步骤
public class FillpelStep extends Step 
{
	private Paint oldPaint,newPaint;
	
	public FillpelStep(Pel pel, Paint oldPaint, Paint newPaint)
	{
		super(pel);
		this.oldPaint=new Paint(oldPaint);
		this.newPaint=new Paint(newPaint);
	}
	
	@Override
	public void toUndoUpdate() //覆写
	{
		(curPel.paint).set(newPaint);
		canvasVi.updateSavedBitmap();
	}
	
	@Override
	public void toRedoUpdate() //覆写
	{
		(curPel.paint).set(oldPaint);
		canvasVi.updateSavedBitmap();
	}
}
