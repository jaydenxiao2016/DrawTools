package com.jayden.drawtool.step;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;

import java.util.List;

//画图元步骤
public class DrawPelStep extends Step
{
	protected static List<Pel> pelList= CanvasView.getPelList(); // 图元链表
	protected int location; //图元所在链表位置

	public DrawPelStep(Pel pel) //构造
	{
		super(pel); //重写父类
		location=pelList.indexOf(pel); //找到该图元所在链表的位置
	}

	@Override
	public void toUndoUpdate() //覆写
	{
		pelList.add(location,curPel); //更新图元链表数据
		canvasVi.updateSavedBitmap();
	}

	@Override
	public void toRedoUpdate() //覆写
	{
		pelList.remove(location); //删除链表对应索引位置图元
		canvasVi.updateSavedBitmap();
	}
}
