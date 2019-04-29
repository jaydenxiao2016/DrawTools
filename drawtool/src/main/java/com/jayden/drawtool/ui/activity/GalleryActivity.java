package com.jayden.drawtool.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.jayden.drawtool.Constant;
import com.jayden.drawtool.R;
import com.jayden.drawtool.adapter.GalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 类名：GalleryActivity.java
 * 描述：我的画廊
 * 作者：xsf
 * 创建时间：2019/4/23
 * 最后修改时间：2019/4/23
 */
public class GalleryActivity extends AppCompatActivity {
    private GalleryAdapter adapter;
    private GridView gridView;
    private List<String> imagePathList;
    private List<String> imageDataPathList;
    private AsyncTask<Void, Void, Void> asyncTask;
    private ProgressDialog progressDialog;

    /**
     * 入库
     *
     * @param activity
     */
    public static void startActionForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, GalleryActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gallery);
        initData();
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setFocusable(true);
        adapter = new GalleryAdapter(this);
        adapter.setOnClickGalleryListener(new GalleryAdapter.onClickGalleryListener() {
            @Override
            public void onClick(int position) {
                if (imageDataPathList != null && imageDataPathList.size() > position) {
                    Intent intent = new Intent();
                    intent.putExtra("imageDataPath", imageDataPathList.get(position));
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(GalleryActivity.this, "找不到对应的数据文件", Toast.LENGTH_SHORT).show();
                }
            }
        });
        gridView.setAdapter(adapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        imagePathList = new ArrayList<>();
        imageDataPathList = new ArrayList<>();
        asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(GalleryActivity.this, "提示", "正在加载数据中...");
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (asyncTask != null && !asyncTask.isCancelled()) {
                    String photosPath = Constant.SAVE_PATH + "/";
                    File f = new File(photosPath);
                    File[] files = f.listFiles();
                    /**
                     * 将所有文件存入ArrayList中,这个地方存的还是文件路径
                     */
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (isImage(file.getAbsolutePath())) {
                            imagePathList.add(file.getAbsolutePath());
                        } else if (isImageData(file.getAbsolutePath())) {
                            imageDataPathList.add(file.getAbsolutePath());
                        }
                    }
                    /**
                     * 按时间降序
                     */
                    Collections.sort(imagePathList, new Comparator<String>() {
                        @Override
                        public int compare(String lhs, String rhs) {
                            return rhs.compareTo(lhs);
                        }
                    });
                    Collections.sort(imageDataPathList, new Comparator<String>() {
                        @Override
                        public int compare(String lhs, String rhs) {
                            return rhs.compareTo(lhs);
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (imagePathList.size() == 0) {
                    Toast.makeText(GalleryActivity.this, "画廊里暂时还没有作品", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.reset(imagePathList);
                    Toast.makeText(GalleryActivity.this, "点击图片可以重新载入编辑", Toast.LENGTH_SHORT).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 返回
     *
     * @param view
     */
    public void onBackBtn(View view) {
        finish();
    }

    /**
     * 是否是图片格式
     *
     * @param fName
     * @return
     */
    private boolean isImage(String fName) {
        boolean re;
        /* 取得扩展名 */
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();
        /* 按扩展名的类型决定MimeType */
        if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            re = true;
        } else {
            re = false;
        }
        return re;
    }

    /**
     * 是否是图片数据格式
     *
     * @param fName
     * @return
     */
    private boolean isImageData(String fName) {
        boolean re;
        /* 取得扩展名 */
        String end = fName
                .substring(fName.lastIndexOf("."), fName.length())
                .toLowerCase();
        /* 按扩展名的类型决定MimeType */
        if (end.equals(Constant.SAVE_DATA_FILE_SUFFIX)) {
            re = true;
        } else {
            re = false;
        }
        return re;
    }

    @Override
    protected void onDestroy() {
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        super.onDestroy();
    }
}
