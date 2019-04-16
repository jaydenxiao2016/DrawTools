package com.jayden.drawtool.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.jayden.drawtool.touch.DrawTouch;
import com.jayden.drawtool.ui.activity.MainActivity;

/**
 * 画笔粗细形状
 */
public class PeneffectView extends View {
    private Path path;

    public PeneffectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPath();
    }

    //以当前选中的笔触（粗细、特效）画在矩形示意框里
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, DrawTouch.getCurPaint());
    }

    public void initPath() {
        path = new Path();

        float width = MainActivity.SCREEN_WIDTH;
        float height = 160;

        path.moveTo(0, height / 2);
        path.cubicTo(0, height / 2, width / 4, height / 4, width / 2, height / 2);

        Path path2 = new Path(); //下波浪 连接用
        path2.moveTo(width / 2, height / 2);
        path2.cubicTo(width / 2, height / 2, width / 4 * 3, height / 4 * 3, width, height / 2);

        path.addPath(path2);
    }
}
