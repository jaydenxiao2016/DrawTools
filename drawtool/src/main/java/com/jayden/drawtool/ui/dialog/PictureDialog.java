package com.jayden.drawtool.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jayden.drawtool.R;

//调色板对话框
public class PictureDialog extends Dialog implements OnClickListener {
    private OnClickPictureListener onClickPictureListener;
    // 线形按钮
    private Button[] pictureBtns = new Button[68];
    private int[] pictureIds = new int[]
            {R.id.btn_aixin, R.id.btn_baizhi, R.id.btn_baozhi, R.id.btn_bianqian,
                    R.id.btn_caihong, R.id.btn_keche, R.id.btn_shuidi, R.id.btn_huakuang,
                    R.id.btn_qianbi, R.id.btn_xianhua, R.id.btn_liwu, R.id.btn_shouji,
                    R.id.btn_diannao, R.id.btn_wujiaoxing, R.id.btn_ren, R.id.btn_yinfu,

                    R.id.btn_yaoshi, R.id.btn_zifu, R.id.btn_yusan, R.id.btn_xinfeng,
                    R.id.btn_yonghu, R.id.btn_sanjiaoban, R.id.btn_huojian, R.id.btn_jianpan,
                    R.id.btn_jiandao, R.id.btn_jiangbei, R.id.btn_huihua, R.id.btn_ditie,
                    R.id.btn_tingtong, R.id.btn_diqiu, R.id.btn_dingwei, R.id.btn_duoyun,

                    R.id.btn_bingzhuangtu, R.id.btn_fanchuan, R.id.btn_fangdajing, R.id.btn_bangbangtang,
                    R.id.btn_gaogenxie, R.id.btn_feiji, R.id.btn_gongwenbao, R.id.btn_chuzuche,
                    R.id.btn_huabi, R.id.btn_kache, R.id.btn_lunchuan, R.id.btn_pingban,
                    R.id.btn_shalou, R.id.btn_shouyinji, R.id.btn_shoubing, R.id.btn_xiyiji,

                    R.id.btn_yinxiang, R.id.btn_zhexian, R.id.btn_zhezhi, R.id.btn_men,
                    R.id.btn_maozi, R.id.btn_neicunka, R.id.btn_pukepai, R.id.btn_reqiqiu,
                    R.id.btn_moshubang, R.id.btn_hongqi, R.id.btn_dianshiji, R.id.btn_biaoqian,
                    R.id.btn_dengpao, R.id.btn_gongju, R.id.btn_xiangpian, R.id.btn_youqitong,
                    R.id.btn_huaban, R.id.btn_motuoche, R.id.btn_maikefeng, R.id.btn_zhaoxiangji};


    // 构造函数
    public PictureDialog(Context context, int theme, OnClickPictureListener onClickPictureListener) {
        super(context, theme);
        this.onClickPictureListener = onClickPictureListener;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_picture);
        initView();
        initData();
    }

    // 初始化界面对象
    private void initView() {
        for (int i = 0; i < pictureBtns.length; i++) {
            pictureBtns[i] = (Button) findViewById(pictureIds[i]);
        }
        //4特效按钮
        for (int i = 0; i < pictureBtns.length; i++) {
            pictureBtns[i] = (Button) findViewById(pictureIds[i]);
        }
    }

    // 初始化数据
    private void initData() {
        // 设置监听
        for (int i = 0; i < pictureBtns.length; i++) //线形按钮
        {
            pictureBtns[i].setOnClickListener(this);
        }
        for (int i = 0; i < pictureBtns.length; i++) //特效按钮
        {
            pictureBtns[i].setOnClickListener(this);
        }
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_caihong) {
            updateDataAndCanvas(R.drawable.pic_caihong);

        } else if (i == R.id.btn_liwu) {
            updateDataAndCanvas(R.drawable.pic_liwu);

        } else if (i == R.id.btn_xianhua) {
            updateDataAndCanvas(R.drawable.pic_xianhua);

        } else if (i == R.id.btn_shuidi) {
            updateDataAndCanvas(R.drawable.pic_shuidi);

        } else if (i == R.id.btn_shouji) {
            updateDataAndCanvas(R.drawable.pic_shouji);

        } else if (i == R.id.btn_baizhi) {
            updateDataAndCanvas(R.drawable.pic_baizhi);

        } else if (i == R.id.btn_keche) {
            updateDataAndCanvas(R.drawable.pic_keche);

        } else if (i == R.id.btn_bianqian) {
            updateDataAndCanvas(R.drawable.pic_bianqian);

        } else if (i == R.id.btn_baozhi) {
            updateDataAndCanvas(R.drawable.pic_baozhi);

        } else if (i == R.id.btn_diannao) {
            updateDataAndCanvas(R.drawable.pic_diannao);

        } else if (i == R.id.btn_wujiaoxing) {
            updateDataAndCanvas(R.drawable.pic_wujiaoxing);

        } else if (i == R.id.btn_huakuang) {
            updateDataAndCanvas(R.drawable.pic_huakuang);

        } else if (i == R.id.btn_qianbi) {
            updateDataAndCanvas(R.drawable.pic_qianbi);

        } else if (i == R.id.btn_ren) {
            updateDataAndCanvas(R.drawable.pic_ren);

        } else if (i == R.id.btn_aixin) {
            updateDataAndCanvas(R.drawable.pic_aixin);

        } else if (i == R.id.btn_yinfu) {
            updateDataAndCanvas(R.drawable.pic_yinfu);

        } else if (i == R.id.btn_yaoshi) {
            updateDataAndCanvas(R.drawable.pic_yaoshi);

        } else if (i == R.id.btn_zifu) {
            updateDataAndCanvas(R.drawable.pic_zifu);

        } else if (i == R.id.btn_yusan) {
            updateDataAndCanvas(R.drawable.pic_yusan);

        } else if (i == R.id.btn_xinfeng) {
            updateDataAndCanvas(R.drawable.pic_xinfeng);

        } else if (i == R.id.btn_yonghu) {
            updateDataAndCanvas(R.drawable.pic_yonghu);

        } else if (i == R.id.btn_sanjiaoban) {
            updateDataAndCanvas(R.drawable.pic_sanjiaoban);

        } else if (i == R.id.btn_huojian) {
            updateDataAndCanvas(R.drawable.pic_huojian);

        } else if (i == R.id.btn_jianpan) {
            updateDataAndCanvas(R.drawable.pic_jianpan);

        } else if (i == R.id.btn_jiandao) {
            updateDataAndCanvas(R.drawable.pic_jiandao);

        } else if (i == R.id.btn_jiangbei) {
            updateDataAndCanvas(R.drawable.pic_jiangbei);

        } else if (i == R.id.btn_huihua) {
            updateDataAndCanvas(R.drawable.pic_huihua);

        } else if (i == R.id.btn_ditie) {
            updateDataAndCanvas(R.drawable.pic_ditie);

        } else if (i == R.id.btn_tingtong) {
            updateDataAndCanvas(R.drawable.pic_tingtong);

        } else if (i == R.id.btn_diqiu) {
            updateDataAndCanvas(R.drawable.pic_diqiu);

        } else if (i == R.id.btn_dingwei) {
            updateDataAndCanvas(R.drawable.pic_dingwei);

        } else if (i == R.id.btn_duoyun) {
            updateDataAndCanvas(R.drawable.pic_duoyun);

        } else if (i == R.id.btn_bingzhuangtu) {
            updateDataAndCanvas(R.drawable.pic_bingzhuangtu);

        } else if (i == R.id.btn_fanchuan) {
            updateDataAndCanvas(R.drawable.pic_fanchuan);

        } else if (i == R.id.btn_fangdajing) {
            updateDataAndCanvas(R.drawable.pic_fangdajing);

        } else if (i == R.id.btn_bangbangtang) {
            updateDataAndCanvas(R.drawable.pic_bangbangtang);

        } else if (i == R.id.btn_gaogenxie) {
            updateDataAndCanvas(R.drawable.pic_gaogenxie);

        } else if (i == R.id.btn_feiji) {
            updateDataAndCanvas(R.drawable.pic_feiji);

        } else if (i == R.id.btn_gongwenbao) {
            updateDataAndCanvas(R.drawable.pic_gongwenbao);

        } else if (i == R.id.btn_chuzuche) {
            updateDataAndCanvas(R.drawable.pic_chuzuche);

        } else if (i == R.id.btn_huabi) {
            updateDataAndCanvas(R.drawable.pic_huabi);

        } else if (i == R.id.btn_kache) {
            updateDataAndCanvas(R.drawable.pic_kache);

        } else if (i == R.id.btn_lunchuan) {
            updateDataAndCanvas(R.drawable.pic_lunchuan);

        } else if (i == R.id.btn_pingban) {
            updateDataAndCanvas(R.drawable.pic_pingban);

        } else if (i == R.id.btn_zhaoxiangji) {
            updateDataAndCanvas(R.drawable.pic_zhaoxiangji);

        } else if (i == R.id.btn_shalou) {
            updateDataAndCanvas(R.drawable.pic_shalou);

        } else if (i == R.id.btn_shouyinji) {
            updateDataAndCanvas(R.drawable.pic_shouyinji);

        } else if (i == R.id.btn_shoubing) {
            updateDataAndCanvas(R.drawable.pic_shoubing);

        } else if (i == R.id.btn_xiyiji) {
            updateDataAndCanvas(R.drawable.pic_xiyiji);

        } else if (i == R.id.btn_yinxiang) {
            updateDataAndCanvas(R.drawable.pic_yinxiang);

        } else if (i == R.id.btn_zhexian) {
            updateDataAndCanvas(R.drawable.pic_zhexian);

        } else if (i == R.id.btn_zhezhi) {
            updateDataAndCanvas(R.drawable.pic_zhezhi);

        } else if (i == R.id.btn_men) {
            updateDataAndCanvas(R.drawable.pic_men);

        } else if (i == R.id.btn_maozi) {
            updateDataAndCanvas(R.drawable.pic_maozi);

        } else if (i == R.id.btn_neicunka) {
            updateDataAndCanvas(R.drawable.pic_neicunka);

        } else if (i == R.id.btn_pukepai) {
            updateDataAndCanvas(R.drawable.pic_pukepai);

        } else if (i == R.id.btn_reqiqiu) {
            updateDataAndCanvas(R.drawable.pic_reqiqiu);

        } else if (i == R.id.btn_moshubang) {
            updateDataAndCanvas(R.drawable.pic_moshubang);

        } else if (i == R.id.btn_hongqi) {
            updateDataAndCanvas(R.drawable.pic_hongqi);

        } else if (i == R.id.btn_dianshiji) {
            updateDataAndCanvas(R.drawable.pic_dianshiji);

        } else if (i == R.id.btn_biaoqian) {
            updateDataAndCanvas(R.drawable.pic_biaoqian);

        } else if (i == R.id.btn_dengpao) {
            updateDataAndCanvas(R.drawable.pic_dengpao);

        } else if (i == R.id.btn_gongju) {
            updateDataAndCanvas(R.drawable.pic_gongju);

        } else if (i == R.id.btn_xiangpian) {
            updateDataAndCanvas(R.drawable.pic_xiangpian);

        } else if (i == R.id.btn_youqitong) {
            updateDataAndCanvas(R.drawable.pic_youqitong);

        } else if (i == R.id.btn_huaban) {
            updateDataAndCanvas(R.drawable.pic_huaban);

        } else if (i == R.id.btn_motuoche) {
            updateDataAndCanvas(R.drawable.pic_motuoche);

        } else if (i == R.id.btn_maikefeng) {
            updateDataAndCanvas(R.drawable.pic_maikefeng);

        }
    }

    public void updateDataAndCanvas(int contentId) {
        if (onClickPictureListener != null) {
            onClickPictureListener.conClickContent(contentId);
        }
        this.dismiss();//关闭窗口
    }

    public interface OnClickPictureListener {
        void conClickContent(int contentId);
    }
}
