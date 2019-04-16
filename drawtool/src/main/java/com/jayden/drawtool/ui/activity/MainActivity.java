package com.jayden.drawtool.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jayden.drawtool.R;
import com.jayden.drawtool.bean.Pel;
import com.jayden.drawtool.bean.Picture;
import com.jayden.drawtool.bean.Text;
import com.jayden.drawtool.step.CopypelStep;
import com.jayden.drawtool.step.DeletepelStep;
import com.jayden.drawtool.step.DrawpelStep;
import com.jayden.drawtool.step.FillpelStep;
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
import com.jayden.drawtool.ui.view.CanvasView;
import com.jayden.drawtool.ui.dialog.ColorpickerDialog;
import com.jayden.drawtool.ui.dialog.PenDialog;
import com.jayden.drawtool.ui.dialog.PictureDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * 类名：MainActivity.java
 * 描述：
 * 公司：北京海鑫科鑫高科技股份有限公司
 * 作者：xsf
 * 创建时间：2019/4/10
 * 最后修改时间：2019/4/10
 */
public class MainActivity extends FragmentActivity {
    /*************************************************/
    private String softDir = "/DrawTools";
    public static Context context;
    public static int SCREEN_WIDTH, SCREEN_HEIGHT;
    private String savedPath;

    //内部算法
    private List<Pel> pelList;
    private Pel selectedPel;
    private Stack<Step> undoStack;
    private static Stack<Step> redoStack;
    private static CanvasView canvasVi;

    /*************************************************/
    //退出系统用
    private AlertDialog.Builder dialog;
    private boolean hasExitAppDialog = false;
    /*************************************************/

    //控件;
    public static View[] allBtns;
    public static View topToolbarSclVi;
    private static View downToolbarSclVi;
    private static ImageView undoBtn;
    private static ImageView redoBtn;
    private Button openPelbarBtn;
    private static View transbarLinlayout;

    private static PopupWindow pelbarPopwin;
    private static PopupWindow canvasbgbarPopwin;

    private static Button extendBtn;
    public static Button colorBtn;//颜色按钮变字用

    //对话框
    private PenDialog penDialog;//调色板对话框
    private ColorpickerDialog colorpickerDialog;//调色板对话框

    //辅助用
    public static Button curToolVi;//工具条：当前选中的工具
    private static ImageView curPelVi;//图元条：当前选中的图元
    private ImageView curCanvasbgVi, whiteCanvasbgVi;//背景条：当前选中的背景
    /**************************************************************************************/
    //拍照
    private static final int REQUEST_CODE_NONE = 0;
    private static final int REQUEST_CODE_GRAPH = 1;//拍照
    private static final int REQUEST_CODE_PICTURE = 2; //缩放
    private static final String IMAGE_UNSPECIFIED = "image/*";
    /**************************************************************************************/
    //进度对话框必要组件
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    /*
     * 自定义成员函数
     */

    //初始化组件
    public void initView() {
        setContentView(R.layout.act_main);
        //根据id关联基本原始组件
        canvasVi = (CanvasView) findViewById(R.id.vi_canvas);
        extendBtn = (Button) findViewById(R.id.btn_extend);
        openPelbarBtn = (Button) findViewById(R.id.btn_openpelbar);
        topToolbarSclVi = (View) findViewById(R.id.sclvi_toptoolbar);
        downToolbarSclVi = (View) findViewById(R.id.sclvi_downtoolbar);
        undoBtn = (ImageView) findViewById(R.id.btn_undo);
        redoBtn = (ImageView) findViewById(R.id.btn_redo);
        transbarLinlayout = (View) findViewById(R.id.linlay_transbar);
        colorBtn = (Button) findViewById(R.id.btn_color);
        int[] btnIds = new int[]{R.id.btn_openpelbar, R.id.btn_opentransbar,
                R.id.btn_opencanvasbgbar, R.id.btn_openprocessingbar, R.id.btn_opendrawtext, R.id.btn_opendrawpicture, R.id.btn_color, R.id.btn_pen, R.id.btn_clear, R.id.btn_save,
                R.id.btn_undo, R.id.btn_redo};
        allBtns = new View[btnIds.length];
        for (int i = 0; i < btnIds.length; i++)
            allBtns[i] = (View) findViewById(btnIds[i]);

        //构造弹出式窗体
        //图元箱\变换箱\浏览箱\背景箱\填拷删箱
        View pelbarVi = this.getLayoutInflater().inflate(R.layout.popwin_pelbar, null);
        pelbarPopwin = new PopupWindow(pelbarVi, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View canvasbgbarVi = this.getLayoutInflater().inflate(R.layout.popwin_canvasbgbar, null);
        canvasbgbarPopwin = new PopupWindow(canvasbgbarVi, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //根据id初始化关联选中的组件
        curToolVi = openPelbarBtn;//初始化选中图元按钮
        curPelVi = (ImageView) pelbarVi.findViewById(R.id.btn_freehand);//初始化选中自由手绘按钮
        curCanvasbgVi = whiteCanvasbgVi = (ImageView) canvasbgbarVi.findViewById(R.id.btn_canvasbg0);//初始化选中“黄纸背景”按钮

        //对话框
        penDialog = new PenDialog(MainActivity.this, R.style.GraffitiDialog);
        colorpickerDialog = new ColorpickerDialog(MainActivity.this, R.style.GraffitiDialog);
    }

    //初始化数据
    public void initData() {
        //事先生成图片被存储的文件夹
        File file = new File(Environment.getExternalStorageDirectory().getPath() + softDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        SCREEN_WIDTH = wm.getDefaultDisplay().getWidth();
        SCREEN_HEIGHT = wm.getDefaultDisplay().getHeight();

        context = MainActivity.this;
        /**************************************************************************************/
        //数据结构
        pelList = CanvasView.getPelList();
        undoStack = CanvasView.getUndoStack();
        redoStack = CanvasView.getRedoStack();
        /**************************************************************************************/
    }

    /**
     * 按钮事件
     */

    //打开工具箱
    public void onOpenToolsBtn(View v) {
        ensurePelFinished();//确保图形已经完全画好
        openTools();
    }

    //关闭工具箱
    public static void closeTools() {
        ensurePelbarClosed();
        ensureCanvasbgbarClosed();
        clearRedoStack();//清空重做栈

        if (curToolVi.getId() == R.id.btn_opentransbar)
            extendBtn.setVisibility(View.VISIBLE);
        else
            extendBtn.setVisibility(View.GONE);

        Animation downDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.downdisappear);
        Animation topDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.topdisappear);
        Animation leftDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.leftdisappear);
        Animation rightDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.rightdisappear);

        downToolbarSclVi.startAnimation(downDisappearAnim);
        topToolbarSclVi.startAnimation(topDisappearAnim);
        undoBtn.startAnimation(rightDisappearAnim);
        redoBtn.startAnimation(leftDisappearAnim);

        downToolbarSclVi.setVisibility(View.GONE);
        topToolbarSclVi.setVisibility(View.GONE);
        undoBtn.setVisibility(View.GONE);
        redoBtn.setVisibility(View.GONE);

        setToolsClickable(false);

        if (curToolVi.getId() == R.id.btn_opentransbar) //若为选中模式
        {
            Animation leftAppearAnim = AnimationUtils.loadAnimation(context, R.anim.leftappear);
            transbarLinlayout.setVisibility(View.VISIBLE); //显示变换箱
            transbarLinlayout.startAnimation(leftAppearAnim);
        } else if (curPelVi.getId() == R.id.btn_brokenline
                || curPelVi.getId() == R.id.btn_polygon) {
            extendBtn.setVisibility(View.VISIBLE);
            extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);
        }

    }

    //打开图形条
    public void onOpenPelbarBtn(View v) {
        ensureCanvasbgbarClosed();
        updateToolbarIcons(v);//更新工具条图标显示

        if (pelbarPopwin.isShowing())//如果悬浮栏打开
            pelbarPopwin.dismiss();//关闭
        else
            pelbarPopwin.showAtLocation(downToolbarSclVi, Gravity.BOTTOM, 0, downToolbarSclVi.getHeight());//打开悬浮窗

        //按下也要注册当前选中图元的touch
        CanvasView.setTouch(new DrawFreehandTouch());
//        int i = curPelVi.getId();
//        if (i == R.id.btn_rect) {
//            CanvasView.setTouch(new DrawRectTouch());
//
//        } else if (i == R.id.btn_bessel) {
//            CanvasView.setTouch(new DrawBesselTouch());
//
//        } else if (i == R.id.btn_oval) {
//            CanvasView.setTouch(new DrawOvalTouch());
//
//        } else if (i == R.id.btn_polygon) {
//            CanvasView.setTouch(new DrawPolygonTouch());
//
//        } else if (i == R.id.btn_brokenline) {
//            CanvasView.setTouch(new DrawBrokenlineTouch());
//
//        } else if (i == R.id.btn_line) {
//            CanvasView.setTouch(new DrawLineTouch());
//
//        } else if (i == R.id.btn_freehand) {
//            CanvasView.setTouch(new DrawFreehandTouch());
//
//        } else if (i == R.id.btn_keepdrawing) {
//            CanvasView.setTouch(new KeepDrawingTouch());
//
//        }
    }

    //打开工具箱
    public static void openTools() {
        if (transbarLinlayout.getVisibility() == View.VISIBLE) //如果变换箱为打开状态
            transbarLinlayout.setVisibility(View.GONE);//关闭

        //弹出上下工具栏的动画
        Animation downAppearAnim = AnimationUtils.loadAnimation(context, R.anim.downappear);
        Animation topAppearAnim = AnimationUtils.loadAnimation(context, R.anim.topappear);
        Animation leftAppearAnim = AnimationUtils.loadAnimation(context, R.anim.leftappear);
        Animation rightAppearAnim = AnimationUtils.loadAnimation(context, R.anim.rightappear);
        downToolbarSclVi.startAnimation(downAppearAnim);
        topToolbarSclVi.startAnimation(topAppearAnim);
        redoBtn.startAnimation(leftAppearAnim);
        undoBtn.startAnimation(rightAppearAnim);
        downToolbarSclVi.setVisibility(View.VISIBLE);
        topToolbarSclVi.setVisibility(View.VISIBLE);
        undoBtn.setVisibility(View.VISIBLE);
        redoBtn.setVisibility(View.VISIBLE);
        setToolsClickable(true);
    }

    //打开变换条
    public void onOpenTransbarBtn(View v) {
        ensurePelbarClosed();
        ensureCanvasbgbarClosed();
        updateToolbarIcons(v);
        closeTools();
        CanvasView.setTouch(new TransformTouch(this));
    }

    //打开背景条
    public void onOpenCanvasbgbarBtn(View v) {
        ensurePelbarClosed();
        if (canvasbgbarPopwin.isShowing())//如果悬浮栏打开
            canvasbgbarPopwin.dismiss();//关闭
        else
            canvasbgbarPopwin.showAtLocation(downToolbarSclVi, Gravity.BOTTOM, 0, downToolbarSclVi.getHeight());//打开悬浮窗
    }

    //切换到图片处理页面
    public void onOpenProcessingbarBtn(View v) {

    }

    //切换到文字界面
    public void onOpenDrawtextBtn(View v) {

        final EditText editTxt = new EditText(this);//作品的名称编辑框
        editTxt.setText("测试");
        class okClick implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int which) //ok
            {
                //内容
                String content = editTxt.getText().toString();

                //文本开始坐标
                PointF beginPoint = new PointF(CanvasView.CANVAS_WIDTH / 2.5f, CanvasView.CANVAS_HEIGHT / 2.5f);

                //内容宽高
                Rect rect = new Rect();
                PointF centerPoint = new PointF();
                (canvasVi.getDrawTextPaint()).getTextBounds(content, 0, content.length(), rect);

                //文本中心
                centerPoint.set(new PointF(beginPoint.x + rect.width() / 2, beginPoint.y - rect.height() / 2));

                //文本区域
                Region region = new Region();
                region.set((int) beginPoint.x, (int) beginPoint.y - rect.height(), (int) (beginPoint.x + rect.width()), (int) (beginPoint.y) + 10);

                Text text = new Text(content, centerPoint, beginPoint);
                Pel newPel = new Pel();
                newPel.text = text;
                newPel.region = region;

                //添加至文本总链表
                (CanvasView.pelList).add(newPel);

                //记录栈中信息
                undoStack.push(new DrawpelStep(newPel));//将该“步”压入undo栈

                //更新画布
                canvasVi.updateSavedBitmap();

            }
        }
        class cancelClick implements DialogInterface.OnClickListener //cancel
        {
            public void onClick(DialogInterface dialog, int which) {
            }
        }

        //实例化确认对话框
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setView(editTxt);
        dialog.setMessage("请输入文本");
        dialog.setPositiveButton("确定", new okClick());
        dialog.setNegativeButton("取消", new cancelClick());
        dialog.create();
        dialog.show();
    }

    //切换到插图界面
    public void onOpenDrawpictureBtn(View v) {
        new PictureDialog(this, R.style.GraffitiDialog, new PictureDialog.OnClickPictureListener() {
            @Override
            public void conClickContent(int contentId) {
                PointF beginPoint = new PointF(CanvasView.CANVAS_WIDTH / 2.5f, CanvasView.CANVAS_HEIGHT / 2.5f);
                PointF centerPoint = new PointF();
                Bitmap content = BitmapFactory.decodeResource(MainActivity.getContext().getResources(),
                        contentId);
                centerPoint.set(beginPoint.x + content.getWidth() / 2, beginPoint.y + content.getHeight() / 2);
                Picture picture = new Picture(contentId, new PointF(centerPoint.x, centerPoint.y),
                        new PointF(beginPoint.x, beginPoint.y));
                //文本区域
                Region region = new Region();
                region.set((int) beginPoint.x, (int) beginPoint.y, (int) (beginPoint.x + content.getWidth()), (int) (beginPoint.y) + content.getHeight());

                Pel newPel = new Pel();
                newPel.picture = picture;
                newPel.region = region;

                //添加至文本总链表
                (CanvasView.pelList).add(newPel);

                //记录栈中信息
                undoStack.push(new DrawpelStep(newPel));//将该“步”压入undo栈

                //更新画布
                canvasVi.updateSavedBitmap();
            }
        }).show();
    }

    /**
     * 图形箱
     */

    //画矩形（子）
    public void onRectBtn(View v) {
        updatePelbarIcons((ImageView) v);//加框去框、改变父菜单
        CanvasView.setTouch(new DrawRectTouch());
    }

    //画贝塞尔（子）
    public void onBesselBtn(View v) {
        updatePelbarIcons((ImageView) v);
        CanvasView.setTouch(new DrawBesselTouch());
    }

    //画椭圆（子）
    public void onOvalBtn(View v) {
        updatePelbarIcons((ImageView) v);
        CanvasView.setTouch(new DrawOvalTouch());
    }

    //画直线（子）
    public void onLineBtn(View v) {
        updatePelbarIcons((ImageView) v);
        CanvasView.setTouch(new DrawLineTouch());
    }

    //画折线（子）
    public void onBrokenlineBtn(View v) {
        updatePelbarIcons((ImageView) v);
        CanvasView.setTouch(new DrawBrokenlineTouch());
    }

    //自由手绘（子）
    public void onFreehandBtn(View v) {
        updatePelbarIcons((ImageView) v);
        CanvasView.setTouch(new DrawFreehandTouch());
    }

    //画多边形（子）
    public void onPolygonBtn(View v) {
        updatePelbarIcons((ImageView) v);
        CanvasView.setTouch(new DrawPolygonTouch());
    }


    /**
     * 变换箱
     */
    //拷贝图元
    public void onCopypelBtn(View v) {
        selectedPel = CanvasView.getSelectedPel();
        if (selectedPel != null)//选中了图元才能进行删除操作
        {
            Pel pel = (Pel) (selectedPel).clone();//以选中图元为模型，拷贝一个新对象
            if (pel.text != null) {
                PointF beginPoint = pel.text.getBeginPoint();
                PointF centerPoint = pel.text.getCenterPoint();
                Region region = pel.region;
                Rect bounds = region.getBounds();
                beginPoint.offset(10, 10);
                centerPoint.offset(10, 10);
                region.set(bounds.left + 10, bounds.top + 10, bounds.right + 10, bounds.bottom + 10);
            } else if (pel.picture != null) {
                PointF beginPoint = pel.picture.getBeginPoint();
                PointF centerPoint = pel.picture.getCenterPoint();
                Region region = pel.region;
                Rect bounds = region.getBounds();
                beginPoint.offset(10, 10);
                centerPoint.offset(10, 10);
                region.set(bounds.left + 10, bounds.top + 10, bounds.right + 10, bounds.bottom + 10);
            } else {
                (pel.path).offset(10, 10);//偏移一定距离友好示意
                (pel.region).setPath(pel.path, CanvasView.getClipRegion());
            }
            (pelList).add(pel);
            undoStack.push(new CopypelStep(pel));//将该“步”压入undo栈
            //清空选中
            CanvasView.setSelectedPel(selectedPel = null);
            if (CanvasView.touch instanceof TransformTouch) {
                TransformTouch transformTouch = (TransformTouch) CanvasView.touch;
                transformTouch.setSelectedPel(null);
            }
            canvasVi.updateSavedBitmap();
        } else {
            Toast.makeText(MainActivity.this, "请先选中一个图形！", Toast.LENGTH_SHORT).show();
        }
    }

    //填充图元(只有图元才有填充功能)
    public void onFillpelBtn(View v) {
        ensureSensorTransFinished();

        selectedPel = CanvasView.getSelectedPel();
        if (selectedPel != null)//选中了图元才能进行删除操作
        {
            if (selectedPel.picture == null && selectedPel.text == null) {
                Paint oldPaint = new Paint(selectedPel.paint);//设置旧画笔（undo用）

                (selectedPel.paint).set(DrawTouch.getCurPaint());//以当前画笔的色态作为选中画笔的
                if (selectedPel.closure == true)//封闭图形
                    (selectedPel.paint).setStyle(Paint.Style.FILL);//填充区域
                else
                    (selectedPel.paint).setStyle(Paint.Style.STROKE);//填充边框

                Paint newPaint = new Paint(selectedPel.paint);////设置新画笔（undo用）
                undoStack.push(new FillpelStep(selectedPel, oldPaint, newPaint));//将该“步”压入undo栈

                CanvasView.setSelectedPel(selectedPel = null);
                canvasVi.updateSavedBitmap();//填充了图元就自然更新缓冲画布
            } else {
                Toast.makeText(MainActivity.this, "只有图元图形才支持填充！", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "请先选中一个图形！", Toast.LENGTH_LONG).show();
        }
    }

    //删除图元
    public void onDeletepelBtn(View v) {
        ensureSensorTransFinished();

        selectedPel = CanvasView.getSelectedPel();
        if (selectedPel != null)//选中了图元才能进行删除操作
        {
            undoStack.push(new DeletepelStep(selectedPel));//将该“步”压入undo栈
            (pelList).remove(selectedPel);

            CanvasView.setSelectedPel(selectedPel = null);
            canvasVi.updateSavedBitmap();//删除了图元就自然更新缓冲画布
        } else {
            Toast.makeText(MainActivity.this, "请先选中一个图形！", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 更新图标
     */

    //更新图元箱相关图标
    public void updatePelbarIcons(ImageView v) {
        //去框、加框
        curPelVi.setImageDrawable(null);//上次选中的图元去框
        v.setImageResource(R.drawable.bg_highlight_frame);//改变子菜单的图片（加框）
        curPelVi = v;//转接当前选中

        //修改父菜单图标
        int fatherDrawableId = 0;
        int i = v.getId();
        if (i == R.id.btn_bessel) {
            fatherDrawableId = R.drawable.btn_bessel_pressed;

        } else if (i == R.id.btn_brokenline) {
            fatherDrawableId = R.drawable.btn_brokenline_pressed;

        } else if (i == R.id.btn_freehand) {
            fatherDrawableId = R.drawable.btn_freehand_pressed;

        } else if (i == R.id.btn_line) {
            fatherDrawableId = R.drawable.btn_line_pressed;

        } else if (i == R.id.btn_oval) {
            fatherDrawableId = R.drawable.btn_oval_pressed;

        } else if (i == R.id.btn_polygon) {
            fatherDrawableId = R.drawable.btn_polygon_pressed;

        } else if (i == R.id.btn_rect) {
            fatherDrawableId = R.drawable.btn_rect_pressed;

        }
        final Drawable fatherDrawable = getResources().getDrawable(fatherDrawableId);
        curToolVi.setCompoundDrawablesWithIntrinsicBounds(null, fatherDrawable, null, null);
    }

    //更新画布背景箱相关图标
    public void updateCanvasbgAndIcons(ImageView v) {
        //去框、加框
        curCanvasbgVi.setImageDrawable(null);//上次选中的图元去框
        v.setImageResource(R.drawable.bg_highlight_frame);//改变子菜单的图片（加框）
        curCanvasbgVi = v;//转接当前选中

        int backgroundDrawable = 0;
        int i = v.getId();
        if (i == R.id.btn_canvasbg0) {
            backgroundDrawable = R.drawable.bg_canvas0;

        } else if (i == R.id.btn_canvasbg1) {
            backgroundDrawable = R.drawable.bg_canvas1;

        } else if (i == R.id.btn_canvasbg2) {
            backgroundDrawable = R.drawable.bg_canvas2;

        } else if (i == R.id.btn_canvasbg3) {
            backgroundDrawable = R.drawable.bg_canvas3;

        } else if (i == R.id.btn_canvasbg4) {
            backgroundDrawable = R.drawable.bg_canvas4;

        } else if (i == R.id.btn_canvasbg5) {
            backgroundDrawable = R.drawable.bg_canvas5;

        } else if (i == R.id.btn_canvasbg6) {
            backgroundDrawable = R.drawable.bg_canvas6;

        } else if (i == R.id.btn_canvasbg7) {
            backgroundDrawable = R.drawable.bg_canvas7;

        } else {
            return;
        }
        canvasVi.setBackgroundBitmap(backgroundDrawable);
    }

    //更新工具条相关图标
    public void updateToolbarIcons(View v) {
        Button btn = (Button) v;
        //变白、变蓝(筛选图片资源)
        int lastDrawableId = 0;//上次选中的按钮需变回的图片
        int i = curToolVi.getId();
        if (i == R.id.btn_openpelbar) {
            int i1 = curPelVi.getId();
            if (i1 == R.id.btn_bessel) {
                lastDrawableId = R.drawable.btn_bessel_normal;

            } else if (i1 == R.id.btn_brokenline) {
                lastDrawableId = R.drawable.btn_brokenline_normal;

            } else if (i1 == R.id.btn_freehand) {
                lastDrawableId = R.drawable.btn_freehand_normal;

            } else if (i1 == R.id.btn_line) {
                lastDrawableId = R.drawable.btn_line_normal;

            } else if (i1 == R.id.btn_oval) {
                lastDrawableId = R.drawable.btn_oval_normal;

            } else if (i1 == R.id.btn_polygon) {
                lastDrawableId = R.drawable.btn_polygon_normal;

            } else if (i1 == R.id.btn_rect) {
                lastDrawableId = R.drawable.btn_rect_normal;

            }

        } else if (i == R.id.btn_opentransbar) {
            lastDrawableId = R.drawable.btn_selectpel_normal;

        }

        int nextDrawableId = 0;//刚才按下的按钮将要变成的图片
        int i1 = v.getId();
        if (i1 == R.id.btn_openpelbar) {
            int i2 = curPelVi.getId();
            if (i2 == R.id.btn_bessel) {
                nextDrawableId = R.drawable.btn_bessel_pressed;

            } else if (i2 == R.id.btn_brokenline) {
                nextDrawableId = R.drawable.btn_brokenline_pressed;

            } else if (i2 == R.id.btn_freehand) {
                nextDrawableId = R.drawable.btn_freehand_pressed;

            } else if (i2 == R.id.btn_line) {
                nextDrawableId = R.drawable.btn_line_pressed;

            } else if (i2 == R.id.btn_oval) {
                nextDrawableId = R.drawable.btn_oval_pressed;

            } else if (i2 == R.id.btn_polygon) {
                nextDrawableId = R.drawable.btn_polygon_pressed;

            } else if (i2 == R.id.btn_rect) {
                nextDrawableId = R.drawable.btn_rect_pressed;

            }

        } else if (i1 == R.id.btn_opentransbar) {
            nextDrawableId = R.drawable.btn_selectpel_pressed;

        }

        final Drawable lastDrawable = getResources().getDrawable(lastDrawableId);
        curToolVi.setCompoundDrawablesWithIntrinsicBounds(null, lastDrawable, null, null);
        curToolVi.setTextColor(Color.WHITE);

        final Drawable nextDrawable = getResources().getDrawable(nextDrawableId);
        btn.setCompoundDrawablesWithIntrinsicBounds(null, nextDrawable, null, null);
        btn.setTextColor(Color.parseColor("#0099CC"));

        curToolVi = btn;//转接当前选中
    }

    /**
     * 确保正确关闭和完成
     */
    //确保悬浮图形条关闭
    private static void ensurePelbarClosed() {
        if (pelbarPopwin.isShowing())//如果悬浮栏打开
            pelbarPopwin.dismiss();//关闭
    }

    //确保悬浮背景条关闭
    private static void ensureCanvasbgbarClosed() {
        if (canvasbgbarPopwin.isShowing())
            canvasbgbarPopwin.dismiss();//关闭
    }

    //确保未画完的图元能够真正敲定
    private void ensurePelFinished() {
        Touch touch = CanvasView.getTouch();
        selectedPel = CanvasView.getSelectedPel();

        if (selectedPel != null) {
            //使人为敲定图元的操作(贝塞尔、折线、多边形)
            if (touch instanceof DrawBesselTouch) {
                touch.control = true;
                touch.up();
            } else if (touch instanceof DrawBrokenlineTouch) {
                touch.hasFinished = true;
                touch.up();
            } else if (touch instanceof DrawPolygonTouch) {
                (touch.curPoint).set(touch.beginPoint);
                touch.up();
            } else if (touch instanceof TransformTouch) {
                TransformTouch transformTouch = (TransformTouch) touch;
                CanvasView.setSelectedPel(null);
                transformTouch.setSelectedPel(null);
                canvasVi.updateSavedBitmap();//重绘位图
            } else //单纯选中
            {
                CanvasView.setSelectedPel(null);//失去焦点
                canvasVi.updateSavedBitmap();//重绘位图
            }
        }
    }

    private void ensureSensorTransFinished() {

    }

    //清空重做栈
    public static void clearRedoStack() {
        if (!redoStack.empty())//redo栈不空
            redoStack.clear();//清空redo栈
    }

    /**
     * 中间部分按钮
     */
    //撤销
    public void onUndoBtn(View v) {
        if (!undoStack.empty())//非浏览模式，且栈不为空
        {
            Step step = undoStack.pop();//从undo栈弹出栈顶
            step.toRedoUpdate();//调用栈顶步骤的更新方法
            redoStack.push(step);//将栈顶转移进redo栈
        }
    }

    //重做
    public void onRedoBtn(View v) {
        if (!redoStack.empty())//非浏览模式，且栈不为空
        {
            Step step = redoStack.pop(); //从redo栈弹出栈顶
            step.toUndoUpdate();//调用栈顶步骤的更新方法
            undoStack.push(step);//将栈顶转移进undo栈
        }
    }

    //清空
    public void onClearBtn(View v) {
        //弹出再次确认对话框
        class okClick implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int which) //ok
            {
                clearData();
            }
        }
        class cancelClick implements DialogInterface.OnClickListener //cancel
        {
            public void onClick(DialogInterface dialog, int which) {
            }
        }

        //实例化确认对话框
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setMessage("您确定要清空？");
        dialog.setPositiveButton("确定", new okClick());
        dialog.setNegativeButton("取消", new cancelClick());
        dialog.create();
        dialog.show();
    }

    //清空内部所有数据
    public void clearData() {
        pelList.clear();
        undoStack.clear();
        redoStack.clear();
        CanvasView.setSelectedPel(null);//若有选中的图元失去焦点
        updateCanvasbgAndIcons(whiteCanvasbgVi);//画布背景图标复位
        canvasVi.setBackgroundBitmap();//清除填充过颜色的地方
    }

    //笔触
    public void onPenBtn(View v) {
        penDialog.show();
    }

    //调色板
    public void onColorBtn(View v) {
        colorpickerDialog.show();
        colorpickerDialog.picker.setOldCenterColor(DrawTouch.getCurPaint().getColor()); //获取当前画笔的颜色作为左半圆颜色
    }

    //保存
    public void onSaveBtn(final View v) {
        final EditText editTxt = new EditText(MainActivity.this);//作品的名称编辑框
        //弹出编辑对话框
        class okClick implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int which) //ok
            {
                try {
                    savedPath = Environment.getExternalStorageDirectory().getPath() + softDir + "/" + editTxt.getText().toString() + ".jpg";
                    File file = new File(savedPath);

                    if (!file.exists()) //文件不存在
                    {
                        Bitmap bitmap = CanvasView.getSavedBitmap();
                        FileOutputStream fileOutputStream = new FileOutputStream(savedPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        fileOutputStream.close();

                        Toast.makeText(MainActivity.this, "图片已保存在(" + savedPath + ")", Toast.LENGTH_SHORT).show();
                    } else //文件存在
                    {
                        //询问用户是否覆盖提示框
                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
                        dialog1.setIcon(android.R.drawable.ic_dialog_info);
                        dialog1.setMessage("该名称已存在，是否覆盖？");
                        dialog1.setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Bitmap bitmap = CanvasView.getSavedBitmap();
                                    FileOutputStream fileOutputStream = new FileOutputStream(savedPath);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                    try {
                                        fileOutputStream.close();
                                        Toast.makeText(MainActivity.this, "图片已保存(" + savedPath + ")", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog1.create();
                        dialog1.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        class cancelClick implements DialogInterface.OnClickListener //cancel
        {
            public void onClick(DialogInterface dialog, int which) {
            }
        }

        //实例化确认对话框
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage("给您的作品取个名字吧~");
        dialog.setView(editTxt);
        dialog.setPositiveButton("保存", new okClick());
        dialog.setNegativeButton("取消", new cancelClick());
        dialog.create();
        dialog.show();
    }

    //换背景
    public void onCanvasbg0Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
    }

    public void onCanvasbg1Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
    }

    public void onCanvasbg2Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
    }

    public void onCanvasbg3Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
    }

    public void onCanvasbg4Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
    }

    public void onCanvasbg5Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
    }

    public void onCanvasbg6Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
    }

    public void onCanvasbg7Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
    }

    //图库
    public void onCanvasbg8Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, REQUEST_CODE_PICTURE);

    }

    //拍照
    public void onCanvasbg9Btn(View v) {
        updateCanvasbgAndIcons((ImageView) v);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.name());
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, Configuration.ORIENTATION_LANDSCAPE);
        startActivityForResult(intent, REQUEST_CODE_GRAPH);

    }

    //打开图库、拍照完毕后需要执行抉择的动作
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

    }
    /*************************************************************************************/
/**
 * 辅助方法
 */
    /**
     * get()
     */

    public static CanvasView getCanvasView() {
        return canvasVi;
    }

    public static Context getContext() {
        return context;
    }


    public void addRecognizedPel(Pel recognizedPel) {
        /**
         * 公共操作
         */
        //包装图元
        (recognizedPel.region).setPath(recognizedPel.path, CanvasView.getClipRegion());
        (recognizedPel.paint).set(DrawTouch.getCurPaint());

        //更新数据
        pelList.add(recognizedPel);//加入链表
        undoStack.push(new DrawpelStep(recognizedPel));//将该“步”压入undo栈

        //更新画布
        CanvasView.setSelectedPel(selectedPel = null);//刚才画的图元失去焦点
        canvasVi.updateSavedBitmap();//重绘位图
    }

    /**********************************************************************************/
    public void onOpenTransChildren(View v) {
        View parentBtn = (View) findViewById(R.id.btn_opentranschildren);

        View deletepelBtn = (View) findViewById(R.id.btn_deletepel);
        View copypelBtn = (View) findViewById(R.id.btn_copypel);
        View fillpelBtn = (View) findViewById(R.id.btn_fillpel);


        if (deletepelBtn.getVisibility() == View.GONE) {
            parentBtn.setBackgroundDrawable(null);
            parentBtn.setBackgroundResource(R.drawable.btn_arrow_close);

            deletepelBtn.setVisibility(View.VISIBLE);
            copypelBtn.setVisibility(View.VISIBLE);
            fillpelBtn.setVisibility(View.VISIBLE);
        } else {
            parentBtn.setBackgroundDrawable(null);
            parentBtn.setBackgroundResource(R.drawable.btn_arrow_open);

            deletepelBtn.setVisibility(View.GONE);
            copypelBtn.setVisibility(View.GONE);
            fillpelBtn.setVisibility(View.GONE);
        }
    }

    public static void setToolsClickable(boolean bool) {
        for (int i = 0; i < allBtns.length; i++)
            allBtns[i].setClickable(bool);
    }
}

