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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.jayden.drawtool.R;
import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.bean.Picture;
import com.jayden.drawtool.bean.Text;
import com.jayden.drawtool.step.Step;
import com.jayden.drawtool.touch.DrawBesselTouch;
import com.jayden.drawtool.touch.DrawBrokenlineTouch;
import com.jayden.drawtool.touch.DrawFreehandTouch;
import com.jayden.drawtool.touch.DrawLineTouch;
import com.jayden.drawtool.touch.DrawOvalTouch;
import com.jayden.drawtool.touch.DrawPolygonTouch;
import com.jayden.drawtool.touch.DrawRectTouch;
import com.jayden.drawtool.touch.DrawTouch;
import com.jayden.drawtool.touch.Touch;
import com.jayden.drawtool.touch.TransformTouch;
import com.jayden.drawtool.ui.activity.MainActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
     * 画画用的画笔
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
                path.lineTo(selectedPel.rightBottomPoint.x, selectedPel.rightBottomPoint.y);
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

    /**
     * 保存and数据文件（读和取位置要一一对应）
     *
     * @param path
     */
    public synchronized void saveFileData(String path) throws Exception {
        if (!TextUtils.isEmpty(path)) {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(path));
            //1.保存背景
            //2.保存各图元数据
            ListIterator<Pel> pelIterator = pelList.listIterator();// 获取pelList对应的迭代器头结点
            //图元总数
            out.writeInt(pelList.size());
            while (pelIterator.hasNext()) {
                Pel pel = pelIterator.next();
                //类型
                out.writeInt(pel.type);
                /**
                 * 类型
                 * path类型：10:自由线 11：矩型 12：塞尔曲线 13：圆 14：直线 15：多重折线 16：多边形
                 * 文本类型：20
                 * 照片类型：30
                 */
                switch (pel.type) {
                    //path类型
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                        //点总数
                        out.writeInt(pel.pathPointFList != null ? pel.pathPointFList.size() : 0);
                        //点坐标
                        if (pel.pathPointFList != null) {
                            for (PointF pointF : pel.pathPointFList) {
                                out.writeFloat(pointF.x);
                                out.writeFloat(pointF.y);
                            }
                        }
                        break;
                    //文本
                    case 20:
                        if (pel.text != null) {
                            out.writeUTF(pel.text.getContent());
                            out.writeInt(pel.text.getPaint().getColor());
                        }
                        break;
                    //照片
                    case 30:
                        if (pel.picture != null) {
                            out.writeInt(pel.picture.getContentId());
                        }
                        break;
                }
                //矩阵
                Rect rect = pel.region.getBounds();
                out.writeInt(rect.left);
                out.writeInt(rect.top);
                out.writeInt(rect.right);
                out.writeInt(rect.bottom);
                //平移x
                out.writeFloat(pel.transDx);
                //平移y
                out.writeFloat(pel.transDy);
                //缩放倍数
                out.writeFloat(pel.scale);
                //中心点x
                out.writeFloat(pel.centerPoint.x);
                //中心点y
                out.writeFloat(pel.centerPoint.y);
                //开始点x
                out.writeFloat(pel.beginPoint.x);
                //开始点y
                out.writeFloat(pel.beginPoint.y);
                //区域右下角点x
                out.writeFloat(pel.bottomRightPointF.x);
                ///区域右下角点y
                out.writeFloat(pel.bottomRightPointF.y);
                //画笔颜色
                out.writeInt(pel.paintColor);
                ///画笔宽度
                out.writeFloat(pel.paintStrokeWidth);
            }
            out.close();
        }
    }

    /**
     * 加载and文件数据（读和取位置要一一对应）
     *
     * @param path
     */
    public synchronized void loadFileData(String path) throws Exception {
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            CanvasView.pelList.clear();
            DataInputStream in = new DataInputStream(new FileInputStream(path));
            //1.获取背景
            //2.获取各图元数据
            int pelSize = in.readInt();
            for (int i = 0; i < pelSize; i++) {
                //类型
                int type = in.readInt();
                Pel pel = null;
                /**
                 * 类型
                 * path类型：10:自由线 11：矩型 12：塞尔曲线 13：圆 14：直线 15：多重折线 16：多边形
                 * 文本类型：20
                 * 照片类型：30
                 */
                switch (type) {
                    //path类型
                    //自由线
                    case 10:
                        pel = DrawFreehandTouch.loadPel(in);
                        break;
                    //矩型
                    case 11:
                        pel = DrawRectTouch.loadPel(in);
                        break;
                    //塞尔曲线
                    case 12:
                        pel = DrawBesselTouch.loadPel(in);
                        break;
                    //圆
                    case 13:
                        pel = DrawOvalTouch.loadPel(in);
                        break;
                    //直线
                    case 14:
                        pel = DrawLineTouch.loadPel(in);
                        break;
                    //多重折线
                    case 15:
                        pel = DrawBrokenlineTouch.loadPel(in);
                        break;
                    //多边形
                    case 16:
                        pel = DrawPolygonTouch.loadPel(in);
                        break;
                    //文本
                    case 20:
                        Text text = new Text(in.readUTF());
                        pel = new Pel();
                        pel.type = 20;
                        pel.text = text;
                        break;
                    //照片
                    case 30:
                        Picture picture = new Picture(in.readInt());
                        pel = new Pel();
                        pel.type = 30;
                        pel.picture = picture;
                        break;
                }
                if (pel != null) {
                    //矩阵
                    pel.region.set(in.readInt(), in.readInt(), in.readInt(), in.readInt());
                    //平移x
                    pel.transDx = in.readFloat();
                    //平移y
                    pel.transDy = in.readFloat();
                    //缩放倍数
                    pel.scale = in.readFloat();
                    //中心点
                    pel.centerPoint = new PointF(in.readFloat(), in.readFloat());
                    //开始点x
                    pel.beginPoint = new PointF(in.readFloat(), in.readFloat());
                    //区域右下角点x
                    pel.bottomRightPointF = new PointF(in.readFloat(), in.readFloat());
                    //画笔颜色
                    int color = in.readInt();
                    pel.paint.setColor(color);
                    pel.paintColor=color;
                    ///画笔宽度
                    float width = in.readFloat();
                    pel.paint.setStrokeWidth(width);
                    pel.paintStrokeWidth=width;
                }
                CanvasView.pelList.add(pel);
            }
            in.close();
            //更新
            updateSavedBitmap();
        }
    }
}
