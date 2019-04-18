package com.jayden.drawtool.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.jayden.drawtool.R;
import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.step.Step;
import com.jayden.drawtool.touch.DrawFreehandTouch;
import com.jayden.drawtool.touch.DrawTouch;
import com.jayden.drawtool.touch.Touch;
import com.jayden.drawtool.touch.TransformTouch;
import com.jayden.drawtool.ui.activity.MainActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * 类名：CanvasView.java
 * 描述：画布
 * 作者：xsf
 * 创建时间：2019/4/10
 * 最后修改时间：2019/4/10
 */
public class CanvasView extends View {
    /**
     * 动画画笔（变换相位用）
     */
    private float phase;
    /**
     * 动画效果画笔
     */
    public static Paint animPelPaint;
    /**
     *  画画用的画笔
     */
    public static Paint drawPelPaint;
    /**
     * 画图片画笔
     */
    public static Paint drawPicturePaint;
    /**
     * 画文字画笔
     */
    private Paint drawTextPaint;
    /**
     * 画布宽
     */
    public static int CANVAS_WIDTH;
    /**
     * 画布高
     */
    public static int CANVAS_HEIGHT;
    /**
     * undo栈
     */
    public static Stack<Step> undoStack;
    /**
     * redo栈
     */
    public static Stack<Step> redoStack;
    /**
     * 图元链表
     */
    public static List<Pel> pelList;
    /**
     * 画布裁剪区域
     */
    public static Region clipRegion;
    /**
     * 当前被选中的图元
     */
    public static Pel selectedPel = null;
    /**
     * 重绘位图
     */
    public static Bitmap savedBitmap;
    /**
     * 重绘画布
     */
    private Canvas savedCanvas;
    /**
     * 缓存画布
     */
    private Canvas cacheCanvas;
    public static Bitmap backgroundBitmap;
    /**
     * 原图片副本，清空或还原时用
     */
    public static Bitmap copyOfBackgroundBitmap;
    public static Bitmap originalBackgroundBitmap;
    private PaintFlagsDrawFilter pfd;
    /**
     * 触摸操作父类
     */
    public static Touch touch;

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        //初始化画布宽高为屏幕宽高
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        CANVAS_WIDTH = wm.getDefaultDisplay().getWidth();
        CANVAS_HEIGHT = wm.getDefaultDisplay().getHeight();
        //初始化undo redo栈
        undoStack = new Stack<Step>();
        redoStack = new Stack<Step>();
        pelList = new LinkedList<Pel>();
        // 图元总链表
        savedCanvas = new Canvas();
        savedCanvas.setDrawFilter(pfd);
        //获取画布裁剪区域
        clipRegion = new Region();
        //初始化为自由手绘操作
        touch = new DrawFreehandTouch();
        drawPelPaint = DrawFreehandTouch.getCurPaint();

        animPelPaint = new Paint(drawPelPaint);
        animPelPaint.setStrokeWidth(3);
        animPelPaint.setStyle(Paint.Style.STROKE);

        drawTextPaint = new Paint();
        drawTextPaint.setColor(DrawTouch.getCurPaint().getColor());
        drawTextPaint.setTextSize(50);
        drawTextPaint.setAntiAlias(true);
        drawTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        drawPicturePaint = new Paint();
        //针对绘制bitmap添加抗锯齿
        drawPicturePaint.setFilterBitmap(true);
        drawPicturePaint.setAntiAlias(true);

        initBitmap();
        updateSavedBitmap();
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(pfd);
        // 画其余图元
        canvas.drawBitmap(savedBitmap, 0, 0, new Paint());
        if (selectedPel != null) {
            if (touch instanceof TransformTouch) //选中状态才产生动态画笔效果
            {
                setAnimPaint();
                selectedPel.drawObject(canvas);
                //动画矩阵虚线
                Path path = new Path();
                path.moveTo(selectedPel.leftTopPoint.x, selectedPel.leftTopPoint.y);
                path.lineTo(selectedPel.rightTopPoint.x, selectedPel.rightTopPoint.y);
                path.lineTo(selectedPel.rigintBottomPoint.x, selectedPel.rigintBottomPoint.y);
                path.lineTo(selectedPel.leftBottomPoint.x, selectedPel.leftBottomPoint.y);
                path.lineTo(selectedPel.leftTopPoint.x, selectedPel.leftTopPoint.y);
                canvas.drawPath(path, animPelPaint);
                //拖曳图标
                canvas.drawBitmap(selectedPel.dragBitmap, selectedPel.dragBtnRect.left, selectedPel.dragBtnRect.top, drawPicturePaint);

                invalidate();
            } else //画图状态不产生动态画笔效果
            {
                canvas.drawPath(selectedPel.path, drawPelPaint);
            }
        }
    }

    /**
     * 触摸监听
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //第一只手指坐标
        touch.setCurPoint(new PointF(event.getX(0), event.getY(0)));

        //第二只手指坐标（可能在第二只手指还没按下时发生异常）
        try {
            touch.setSecPoint(new PointF(event.getX(1), event.getY(1)));
        } catch (Exception e) {
            touch.setSecPoint(new PointF(1, 1));
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:// 第一只手指按下
            {
                if (MainActivity.topToolbarSclVi.getVisibility() == View.VISIBLE) {
                    MainActivity.closeTools();
                    touch.dis = Float.MAX_VALUE;
                }

                touch.down1();
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN:// 第二个手指按下
                touch.down2();
                break;
            case MotionEvent.ACTION_MOVE:
                touch.move();
                break;
            case MotionEvent.ACTION_UP:// 第一只手指抬起
            case MotionEvent.ACTION_POINTER_UP://第二只手抬起
                touch.up();
                break;
        }
        invalidate();

        return true;
    }

    /**
     * 初始化画布
     */
    public void initBitmap() {
        clipRegion.set(new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT));
        BitmapDrawable backgroundDrawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.bg_canvas0);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(backgroundDrawable.getBitmap(), CANVAS_WIDTH, CANVAS_HEIGHT, true);

        ensureBitmapRecycled(backgroundBitmap);
        backgroundBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);
        ensureBitmapRecycled(scaledBitmap);

        ensureBitmapRecycled(copyOfBackgroundBitmap);
        copyOfBackgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);

        ensureBitmapRecycled(originalBackgroundBitmap);
        originalBackgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);

        cacheCanvas = new Canvas();
        savedCanvas.setDrawFilter(pfd);
        cacheCanvas.setBitmap(backgroundBitmap);
    }

    /*
     * 自定义成员函数
     */
    public void updateSavedBitmap() //更新重绘背景位图用（当且仅当选择的图元有变化的时候才调用）
    {
        //创建缓冲位图
        ensureBitmapRecycled(savedBitmap);
        savedBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);//由画布背景创建缓冲位图
        savedCanvas.setBitmap(savedBitmap);

        //画除selectedPel外的所有图元
        drawPels();

        invalidate();
    }

    public void drawPels() {
        ListIterator<Pel> pelIterator = pelList.listIterator();// 获取pelList对应的迭代器头结点
        while (pelIterator.hasNext()) {
            Pel pel = pelIterator.next();
            if (!pel.equals(selectedPel)) {
                pel.drawObject(savedCanvas);
            }
        }
    }


    public static void ensureBitmapRecycled(Bitmap bitmap) //确保传入位图已经回收
    {
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
    }

    // 动画画笔更新
    private void setAnimPaint() {
        phase++; // 变相位

        Path p = new Path();
        p.addRect(new RectF(0, 0, 6, 3), Path.Direction.CCW); // 路径单元是矩形（也可以为椭圆）
        PathDashPathEffect effect = new PathDashPathEffect(p, 12, phase, // 设置路径效果
                PathDashPathEffect.Style.ROTATE);
        animPelPaint.setPathEffect(effect);
    }

    /**
     * ---------------------get set-------------------
     **/
    public static int getCanvasWidth() {
        return CANVAS_WIDTH;
    }

    public static int getCanvasHeight() {
        return CANVAS_HEIGHT;
    }

    public static Region getClipRegion() {
        return clipRegion;
    }

    public static List<Pel> getPelList() {
        return pelList;
    }

    public static Pel getSelectedPel() {
        return selectedPel;
    }

    public static Bitmap getSavedBitmap() {
        return savedBitmap;
    }

    public static Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    public static Bitmap getCopyOfBackgroundBitmap() {
        return copyOfBackgroundBitmap;
    }

    public static Bitmap getOriginalBackgroundBitmap() {
        return originalBackgroundBitmap;
    }

    public static Touch getTouch() {
        return touch;
    }

    public static Stack<Step> getUndoStack() {
        return undoStack;
    }

    public static Stack<Step> getRedoStack() {
        return redoStack;
    }

    /*
     * set()方法:设置CanvasView下指定成员
     */
    public static void setSelectedPel(Pel pel) {
        selectedPel = pel;
    }

    public static void setTouch(Touch childTouch) {
        touch = childTouch;
    }

    public static void setCanvasSize(int width, int height) {
        CANVAS_WIDTH = width;
        CANVAS_HEIGHT = height;
    }

    public static void setSavedBitmap(Bitmap bitmap) {
        savedBitmap = bitmap;
    }

    public void setBackgroundBitmap(int id) //以已提供选择的背景图片换画布
    {
        BitmapDrawable backgroundDrawable = (BitmapDrawable) this.getResources().getDrawable(id);
        Bitmap offeredBitmap = backgroundDrawable.getBitmap();

        ensureBitmapRecycled(backgroundBitmap);
        backgroundBitmap = Bitmap.createScaledBitmap(offeredBitmap, CANVAS_WIDTH, CANVAS_HEIGHT, true);


        ensureBitmapRecycled(copyOfBackgroundBitmap);
        copyOfBackgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);

        ensureBitmapRecycled(originalBackgroundBitmap);
        originalBackgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);

        updateSavedBitmap();
    }

    public void setBackgroundBitmap(Bitmap photo)//以图库或拍照得到的背景图片换画布
    {
        ensureBitmapRecycled(backgroundBitmap);
        backgroundBitmap = Bitmap.createScaledBitmap(photo, CANVAS_WIDTH, CANVAS_HEIGHT, true);

        ensureBitmapRecycled(copyOfBackgroundBitmap);
        copyOfBackgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);

        ensureBitmapRecycled(originalBackgroundBitmap);
        originalBackgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);

        updateSavedBitmap();
    }

    public void setProcessedBitmap(Bitmap imgPro)//设置处理后的图片作为背景
    {
        ensureBitmapRecycled(backgroundBitmap);
        backgroundBitmap = Bitmap.createScaledBitmap(imgPro, CANVAS_WIDTH, CANVAS_HEIGHT, true);

        ensureBitmapRecycled(copyOfBackgroundBitmap);
        copyOfBackgroundBitmap = backgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);

        updateSavedBitmap();
    }

    public void setBackgroundBitmap() //清空画布时将之前保存的副本背景作为重绘（去掉填充）
    {
        ensureBitmapRecycled(backgroundBitmap);
        backgroundBitmap = copyOfBackgroundBitmap.copy(Bitmap.Config.ARGB_8888, true);

        updateSavedBitmap();
    }

    public Paint getDrawTextPaint() {
        return drawTextPaint;
    }
}
