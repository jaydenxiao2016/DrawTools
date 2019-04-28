package com.jayden.drawtool.step;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.activity.DrawMainActivity;
import com.jayden.drawtool.ui.view.CanvasView;

/**
 * 类名：Step.java
 * 描述：步骤类
 * 作者：xsf
 * 创建时间：2019/4/10
 * 最后修改时间：2019/4/10
 */
public class Step {
    /**
     * 通知重绘用
     */
    protected static CanvasView canvasVi = DrawMainActivity.getCanvasView();
    /**
     * 最早放入undo的图元
     */
    protected Pel curPel;

    public Step(Pel pel)
    {
        this.curPel = pel;
    }

    /**
     * 进undo栈时对List中图元的更新（子类覆写）
     */
    public void toUndoUpdate()
    {
    }

    /**
     * 进redo栈时对List中图元的反悔（子类覆写）
     */
    public void toRedoUpdate()
    {
    }

    /**
     * 进undo栈时对List中图元的更新（子类覆写）
     * @param pel
     */
    public void setToUndoPel(Pel pel) {
    }
}
