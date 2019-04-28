package com.jayden.drawtool;

import android.os.Environment;

/**
 * 类名：Constant.java
 * 描述：常量配置
 * 作者：xsf
 * 创建时间：2019/4/23
 * 最后修改时间：2019/4/23
 */
public class Constant {
    /**
     * 文件保存路径
     */
    public static final String SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/DrawTools";
    /**
     * 保存画图图片文件后缀名
     */
    public static final String SAVE_IMAGE_FILE_SUFFIX = ".png";
    /**
     * 保存画图数据文件后缀名
     */
    public static final String SAVE_DATA_FILE_SUFFIX = ".and";
    /**
     * 默认画笔颜色
     */
    public static final String PAINT_DEFAULT_COLOR= "#ff000000";
    /**
     * 默认画笔粗细大小
     */
    public static final int PAINT_DEFAULT_STROKE_WIDTH= 5;
    /**
     * 默认画笔textsize大小
     */
    public static final int PAINT_DEFAULT_TEXT_SIZE = 30;
}
