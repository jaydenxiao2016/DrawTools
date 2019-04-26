package com.jayden.drawtool.step;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.activity.MainActivity;
import com.jayden.drawtool.ui.view.CanvasView;

/**
 * 类名：Step.java
 * 描述：步骤类
 * 作者：xsf
 * 创建时间：2019/4/10
 * 最后修改时间：2019/4/10
 */
public class Step {
    protected static CanvasView canvasVi = MainActivity.getCanvasView(); //通知重绘用
    protected Pel curPel;//最早放入undo的图元

    public Step(Pel pel) //构造
    {
        this.curPel = pel;
    }

    public void toUndoUpdate() //进undo栈时对List中图元的更新（子类覆写）
    {
    }

    public void toRedoUpdate()//进redo栈时对List中图元的反悔（子类覆写）
    {
    }

    public void setToUndoPel(Pel pel) {
    }
}
