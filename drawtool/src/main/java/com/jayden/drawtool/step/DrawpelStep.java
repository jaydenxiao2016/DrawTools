package com.jayden.drawtool.step;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

/**
 * 画图元步骤
 */
public class DrawPelStep extends Step
{

	/**
	 * 图元所在链表位置
	 */
	protected int location;

	public DrawPelStep(Pel pel) //构造
	{
		super(pel); //重写父类
		location= CanvasView.getPelList().indexOf(pel); //找到该图元所在链表的位置
	}

	@Override
	public void toUndoUpdate() //覆写
	{
		CanvasView.getPelList().add(location,curPel); //更新图元链表数据
		canvasVi.updateSavedBitmap();
	}

	@Override
	public void toRedoUpdate() //覆写
	{
		CanvasView.getPelList().remove(location); //删除链表对应索引位置图元
		canvasVi.updateSavedBitmap();
	}

	@Override
	public void setToUndoPel(Pel pel) {

	}
}
