package com.jayden.drawtool.step;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.view.CanvasView;


/**
 * 编辑变换图元步骤
 */
public class TransformPelStep extends Step {
    private Pel savedPel;
    /**
     * 变换前的pel
     */
    private Pel toUndoPel;

    public TransformPelStep(Pel pel) //构造
    {
        super(pel);//重写父类
        savedPel = curPel.clone();
    }

    @Override
    public void toUndoUpdate() //覆写
    {
        if (toUndoPel != null) {
            curPel.transDy = toUndoPel.transDy;
            curPel.transDx = toUndoPel.transDx;
            curPel.degree = toUndoPel.degree;
            curPel.scale = toUndoPel.scale;
            curPel.region = toUndoPel.region;
        }
        CanvasView.setSelectedPel(null);
        canvasVi.updateSavedBitmap();
    }

    @Override
    public void toRedoUpdate() //覆写
    {
        if (savedPel != null) {
            curPel.transDy = savedPel.transDy;
            curPel.transDx = savedPel.transDx;
            curPel.degree = savedPel.degree;
            curPel.scale = savedPel.scale;
            curPel.region = savedPel.region;
        }
        CanvasView.setSelectedPel(null);
        canvasVi.updateSavedBitmap();
    }

    @Override
    public void setToUndoPel(Pel toUndoPel) {
        if (toUndoPel != null)
            this.toUndoPel = toUndoPel.clone();
    }
}
