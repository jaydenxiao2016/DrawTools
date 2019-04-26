package com.jayden.drawtool.touch;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.jayden.drawtool.R;
import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.step.Step;
import com.jayden.drawtool.step.TransformPelStep;
import com.jayden.drawtool.ui.view.CanvasView;

import java.util.ListIterator;


//变换触摸类
public class TransformTouch extends Touch {

    private Matrix cachedMatrix; //选中图元的最初因子
    private PointF downPoint; //按下，移动，两指中点
    public PointF centerPoint; //缩放、旋转中心
    private Pel savedPel; //重绘图元
    private RectF originalRect;//开始矩阵
    private int originalRegionWidth;//开始矩阵宽
    private int originalRegionHeight;//开始矩阵高
    private static final int NONE = 0; // 平移操作
    private static final int DRAG = 1; //缩放、旋转 操作
    private static final int TRANSLATE = 2; // 平移操作
    private static int mode = NONE; // 当前操作类型
    private float dx, dy, oridx, oridy; //最初平移偏移量
    private static final float MIN_ZOOM = 0.3f; //缩放下限

    private Step step = null;
    private Context context;

    public TransformTouch(Context context) {
        super();
        this.context = context;
        downPoint = new PointF();
        cachedMatrix = new Matrix();
        centerPoint = new PointF();
        savedPel = new Pel();
        oridx = oridy = 0;
        originalRegionWidth = 0;
        originalRegionHeight = 0;
    }

    // 第一只手指按下
    @Override
    public void down1() {
        // 获取down事件的发生位置
        downPoint.set(curPoint);

        // 判断是否相交
        Pel minDisPel = null;
        float minHorizontalDis = Float.MAX_VALUE;
        float minVerticalDis = Float.MAX_VALUE;
        //未选中
        if (selectedPel == null) {
            ListIterator<Pel> pelIterator = pelList.listIterator(); // 获取pelList对应的迭代器头结点
            while (pelIterator.hasNext()) {
                Pel pel = pelIterator.next();
                Rect bounds = (pel.region).getBounds();
                RectF rect = new RectF();
                RectF oldRect = new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom);
                Matrix cachedMatrix = new Matrix();
                //开始位置
                cachedMatrix.setTranslate(pel.transDx, pel.transDy);
                cachedMatrix.postScale(pel.scale, pel.scale, pel.centerPoint.x, pel.centerPoint.y);
                cachedMatrix.postRotate(pel.degree, pel.centerPoint.x, pel.centerPoint.y);
                reSetDstRect(cachedMatrix, rect, oldRect);
                float leftDis = Math.abs(rect.left - downPoint.x);
                float rightDis = Math.abs(rect.right - downPoint.x);
                float horizontalDis = leftDis + rightDis;

                float topDis = Math.abs(rect.top - downPoint.y);
                float bottomDis = Math.abs(rect.bottom - downPoint.y);
                float verticalDis = topDis + bottomDis;

                if (horizontalDis < minHorizontalDis || verticalDis < minVerticalDis) {
                    if (leftDis + rightDis < rect.width() + 5) {
                        if (topDis + bottomDis < rect.height() + 5) {
                            minDisPel = pel;
                            minHorizontalDis = leftDis + rightDis;
                            minVerticalDis = topDis + bottomDis;
                        }
                    }
                }
            }
            // 圆域扩展到最大是否有选中任何图元
            if (minDisPel != null) {
                // 敲定该图元
                CanvasView.setSelectedPel(selectedPel = minDisPel);
                (savedPel.path).set(selectedPel.path); // 原始选中图元所在位置记忆到零时图元中去
                //初始化选中
                initSelect();
                updateSavedBitmap();

                mode = TRANSLATE;
            } else //超过阈值未选中
            {
                mode = NONE;
                CanvasView.setSelectedPel(selectedPel = null); //同步CanvasView中当前选中的图元
                updateSavedBitmap();
            }
        }
        //已选中
        else {
            //准备拖动
            if (isInsideRF(downPoint.x, downPoint.y, selectedPel.dragBtnRect)) {
                //初始化选中
                initSelect();
                mode = DRAG;
            }
            //移动
            else if (isInsideContent(downPoint.x, downPoint.y)) {
                //初始化选中
                initSelect();
                mode = TRANSLATE;
            } else {//不在范围内
                mode = NONE;
                CanvasView.setSelectedPel(selectedPel = null); //同步CanvasView中当前选中的图元
                updateSavedBitmap();
            }
        }
    }

    // 第二只手指按下
    @Override
    public void down2() {

    }

    // 手指移动
    @Override
    public void move() {
        super.move();
        // 获取move事件的发生位置
        if (selectedPel != null)// 前提是要选中了图元
        {
            if (mode == TRANSLATE)// 平移操作
            {
                // 对选中图元施行平移变换
                cachedMatrix.setTranslate(originalRect.left + oridx + (curPoint.x - downPoint.x), originalRect.top + oridy + (curPoint.y - downPoint.y)); // 作用于平移变换因子
                cachedMatrix.postScale(selectedPel.scale, selectedPel.scale, centerPoint.x, centerPoint.y);
                cachedMatrix.postRotate(selectedPel.degree, centerPoint.x, centerPoint.y);
                //重新确定四个点
                matrixCheck();
                selectedPel.transDx = oridx + (curPoint.x - downPoint.x);
                selectedPel.transDy = oridy + (curPoint.y - downPoint.y);
            } else if (mode == DRAG) // 缩放旋转操作
            {
                //开始位置
                cachedMatrix.setTranslate(originalRect.left + oridx, originalRect.top + oridy);

                //累计缩放倍数
                selectedPel.scale = getScale(centerPoint,
                        new PointF(selectedPel.bottomRightPointF.x + selectedPel.transDx,
                                selectedPel.bottomRightPointF.y + selectedPel.transDy),
                        curPoint);
                //累计旋转角度
                selectedPel.degree = getAngle(centerPoint,
                        new PointF(selectedPel.bottomRightPointF.x + selectedPel.transDx,
                                selectedPel.bottomRightPointF.y + selectedPel.transDy),
                        curPoint);
                cachedMatrix.postScale(selectedPel.scale, selectedPel.scale, centerPoint.x, centerPoint.y);
                cachedMatrix.postRotate(selectedPel.degree, centerPoint.x, centerPoint.y);
                //重新确定四个点
                matrixCheck();
            }
        }
    }

    // 手指抬起
    @Override
    public void up() {
        //为判断是否属于“选中（即秒抬）”情况
        float disx = Math.abs(curPoint.x - downPoint.x);
        float disy = Math.abs(curPoint.y - downPoint.y);
        if ((disx > 2f || disy > 2f) && step != null) //移动距离至少要满足大于2f
        {
            //敲定当前对应步骤
            if (selectedPel != null) {
                oridx = selectedPel.transDx;
                oridy = selectedPel.transDy;
                step.setToUndoPel(selectedPel);//设置进行该次步骤后的变换因子
                undoStack.push(step);//将该“步”压入undo栈
                // 敲定此次操作的最终区域
                if (selectedPel != null)
                    (savedPel.path).set(selectedPel.path); //初始位置也同步更新
            }
        }

        mode = NONE;
    }

    private void initSelect() {

        //敲定开始矩阵宽高
        Rect bounds = selectedPel.region.getBounds();
        originalRect = new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom);
        originalRegionWidth = (int) (originalRect.right - originalRect.left);
        originalRegionHeight = (int) (originalRect.bottom - originalRect.top);
        //拖动图标
        selectedPel.dragBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_control);
        selectedPel.leftBottomPoint = new PointF();
        selectedPel.leftTopPoint = new PointF();
        selectedPel.rightTopPoint = new PointF();
        selectedPel.rightBottomPoint = new PointF();
        selectedPel.dragBtnRect = new RectF();
        //初始化原有偏移，偏移是累计的
        oridx = selectedPel.transDx;
        oridy = selectedPel.transDy;
        //初始化变换矩阵中心点
        centerPoint.set(calPelCenterPoint(selectedPel));
        // 获取选中图元的初始matrix
        cachedMatrix = new Matrix();
        //开始位置
        cachedMatrix.setTranslate(originalRect.left + oridx, originalRect.top + oridy);
        cachedMatrix.postScale(selectedPel.scale, selectedPel.scale, centerPoint.x, centerPoint.y);
        cachedMatrix.postRotate(selectedPel.degree, centerPoint.x, centerPoint.y);
        //矩阵参数检查,设置4个点坐标
        matrixCheck();
        //由已知信息构造该步骤
        step = new TransformPelStep(selectedPel);//设置该步骤对应图元
    }

    /**
     * 矩形区间内是否包含某一个点
     *
     * @param x
     * @param y
     * @param rect
     * @return
     */
    public boolean isInsideRF(float x, float y, RectF rect) {
        if (rect.contains(x, y))
            return true;
        return false;
    }

    /**
     * 图片实际内容区域，是否包含某一个坐标
     *
     * @param pointX
     * @param pointY
     * @return
     */
    private boolean isInsideContent(float pointX, float pointY) {
        PointF pointF = new PointF(pointX, pointY);
        PointF[] vertexPointFs = new PointF[]{selectedPel.leftTopPoint,
                selectedPel.rightTopPoint,
                selectedPel.rightBottomPoint,
                selectedPel.leftBottomPoint};
        int nCross = 0;
        for (int i = 0; i < vertexPointFs.length; i++) {
            PointF p1 = vertexPointFs[i];
            PointF p2 = vertexPointFs[(i + 1) % vertexPointFs.length];
            if (p1.y == p2.y)
                continue;
            if (pointF.y < Math.min(p1.y, p2.y))
                continue;
            if (pointF.y >= Math.max(p1.y, p2.y))
                continue;
            double x = (double) (pointF.y - p1.y) * (double) (p2.x - p1.x) / (double) (p2.y - p1.y) + p1.x;
            if (x > pointF.x)
                nCross++;
        }
        return (nCross % 2 == 1);
    }

    /**
     * 矩阵变换后改变参数，参数检查
     *
     * @return
     */
    private boolean matrixCheck() {
        //计算图片矩阵四个坐标点
        refreshBitmapVerticesPoint();
        reSetDragBtnRect();
        return true;//可以在这里做一些检查
    }

    /**
     * 刷新图片矩阵变换后，实际占有的矩形区域大小
     *
     * @param matrix
     * @param dstRect
     * @param srcRect
     */
    public void reSetDstRect(Matrix matrix, RectF dstRect, RectF srcRect) {
        matrix.mapRect(dstRect, srcRect);
    }

    /**
     * 获取矩阵变换后，图片实际的四个坐标点
     */
    private void refreshBitmapVerticesPoint() {
        float[] f = new float[9];
        cachedMatrix.getValues(f);
        selectedPel.leftTopPoint.x = f[0] * 0 + f[1] * 0 + f[2];
        selectedPel.leftTopPoint.y = f[3] * 0 + f[4] * 0 + f[5];
        selectedPel.rightTopPoint.x = f[0] * originalRegionWidth + f[1] * 0 + f[2];
        selectedPel.rightTopPoint.y = f[3] * originalRegionWidth + f[4] * 0 + f[5];
        selectedPel.leftBottomPoint.x = f[0] * 0 + f[1] * originalRegionHeight + f[2];
        selectedPel.leftBottomPoint.y = f[3] * 0 + f[4] * originalRegionHeight + f[5];
        selectedPel.rightBottomPoint.x = f[0] * originalRegionWidth + f[1] * originalRegionHeight + f[2];
        selectedPel.rightBottomPoint.y = f[3] * originalRegionWidth + f[4] * originalRegionHeight + f[5];
    }

    /**
     * 重新获取拖动按钮实际所在区域
     */
    private void reSetDragBtnRect() {
        selectedPel.dragBtnRect = new RectF(selectedPel.rightBottomPoint.x - (selectedPel.dragBitmap.getWidth() * 1.0f / 2),
                selectedPel.rightBottomPoint.y - (selectedPel.dragBitmap.getHeight() * 1.0f / 2),
                selectedPel.rightBottomPoint.x + (selectedPel.dragBitmap.getWidth() * 1.0f / 2),
                selectedPel.rightBottomPoint.y + (selectedPel.dragBitmap.getHeight() * 1.0f / 2));
    }

    /**
     * 求角∠P2P1P3 (带正负角度(顺时针逆时针))
     *
     * @param p1 圆心 (围绕旋转的点，这里为Rect的中心点)
     * @param p2 老坐标
     * @param p3 新坐标
     * @return
     */
    private float getAngle(PointF p1, PointF p2, PointF p3) {

        float dx = p3.x - p1.x;
        float dy = p3.y - p1.y;
        double a = Math.atan2(dy, dx);

        float dpx = p2.x - p1.x;
        float dpy = p2.y - p1.y;
        double b = Math.atan2(dpy, dpx);

        double diff = a - b;
        return (float) Math.toDegrees(diff);
    }

    /**
     * 求以p1p3为半径的圆 /以p1p2为半径的圆，得到缩放的比例
     *
     * @param p1 圆心 (围绕旋转的点，这里为Rect的中心点)
     * @param p2 老坐标
     * @param p3 新坐标
     * @return
     */
    private float getScale(PointF p1, PointF p2, PointF p3) {
        double p1p2 = Math.hypot(p1.x - p2.x, p1.y - p2.y);
        double p1p3 = Math.hypot(p1.x - p3.x, p1.y - p3.y);
        //新半径/老半径
        float scale = (float) (p1p3 / p1p2);
        return scale > MIN_ZOOM ? scale : MIN_ZOOM;
    }

    public static PointF calPelCenterPoint(Pel selectedPel) {
        Rect boundRect = new Rect();
        selectedPel.region.getBounds(boundRect);

        return new PointF((boundRect.right + boundRect.left) / 2 + selectedPel.transDx, (boundRect.bottom + boundRect.top) / 2 + selectedPel.transDy);
    }
}