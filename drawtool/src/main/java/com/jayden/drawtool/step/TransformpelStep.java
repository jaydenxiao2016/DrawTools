package com.jayden.drawtool.step;

import android.graphics.Matrix;
import android.graphics.Region;

import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.ui.CanvasView;


//变换图元步骤
public class TransformpelStep extends Step {
    /**
     * 变换前的matrix
     */
    private Matrix toUndoMatrix;
    private static Region clipRegion = CanvasView.getClipRegion();
    private Pel savedPel;
    /**
     * 变换前的pel
     */
    private Pel toUndoPel;

    public TransformpelStep(Pel pel) //构造
    {
        super(pel);//重写父类
        toUndoMatrix = new Matrix();
        savedPel = curPel.clone();
    }

    @Override
    public void toUndoUpdate() //覆写
    {
        //文本和图片
        if ((curPel.text != null || curPel.picture != null) && toUndoPel != null) {
            curPel.transDy = toUndoPel.transDy;
            curPel.transDx = toUndoPel.transDx;
            curPel.degree = toUndoPel.degree;
            curPel.scale = toUndoPel.scale;
            curPel.region = toUndoPel.region;
        }
        //图元
        else if (curPel.path != null) {
            (curPel.path).transform(toUndoMatrix);
            (curPel.region).setPath(curPel.path, clipRegion);
        }
        CanvasView.setSelectedPel(null);
        canvasVi.updateSavedBitmap();
    }

    @Override
    public void toRedoUpdate() //覆写
    {
        //文本和图片
        if (curPel.text != null || curPel.picture != null) {
            curPel.transDy = savedPel.transDy;
            curPel.transDx = savedPel.transDx;
            curPel.degree = savedPel.degree;
            curPel.scale = savedPel.scale;
            curPel.region = savedPel.region;
        }
        //图元
        else if (curPel.path != null) {
            (curPel.path).set(savedPel.path);
            (curPel.region).setPath(curPel.path, clipRegion);
        }
        CanvasView.setSelectedPel(null);
        canvasVi.updateSavedBitmap();
    }

    /*
     * set()方法
     */
    public void setToUndoMatrix(Matrix matrix) {
        toUndoMatrix.set(matrix);
    }

    @Override
    public void setToUndoPel(Pel toUndoPel) {
        if(toUndoPel!=null)
        this.toUndoPel = toUndoPel.clone();
    }
}
