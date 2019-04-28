package com.jayden.drawtool.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jayden.drawtool.R;
import com.jayden.drawtool.touch.DrawTouch;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;


/**
 * 调色板对话框
 */
public class ColorPickerDialog extends Dialog implements OnClickListener {
    /**
     * 调色板控件相关
     */
    public ColorPicker picker;
    private OpacityBar opacityBar;
    private SaturationBar saturationBar;
    private ValueBar valueBar;

    private Button okBtn;

    public ColorPickerDialog(Context context, int theme) {
        super(context, theme);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_colorpicker);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        initView();
        initData();
    }

    /**
     * 初始化界面对象
     */
    private void initView() {
        //找到实例对象
        picker = (ColorPicker) findViewById(R.id.colorpicker_picker);
        opacityBar = (OpacityBar) findViewById(R.id.colorpicker_opacitybar);
        saturationBar = (SaturationBar) findViewById(R.id.colorpicker_saturationbar);
        valueBar = (ValueBar) findViewById(R.id.colorpicker_valuebar);
        //按钮对象
        okBtn = (Button) findViewById(R.id.btn_colorpicker_ok);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //使环形取色器和拖动条建立关系
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);
        //初始化当前画笔颜色
        picker.setColor( DrawTouch.getCurPaint().getColor());
        okBtn.setOnClickListener(this);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_colorpicker_ok) {
            int curColor = picker.getColor();
            DrawTouch.getCurPaint().setColor(curColor);
            this.dismiss();
        }
    }
}
