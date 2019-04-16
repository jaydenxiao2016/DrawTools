package com.jayden.drawtools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 图片右下角单点控制图片做旋转,移动，缩放
 * 
 * @author samuel
 *
 */
public class ControlMaskImageView extends View {

	private static final int INVALID_POINTER = -1;

	private static final int NONE = 0;
	//拖动，改变大小和角度， 中心点不变
	private static final int DRAG = 1;
	//移动
	private static final int TRANSLATE = 2;
	//图片外部点击
	private static final int OUTSIDE = 3;

	private int mCurrentMode = NONE;

	private Matrix mMatrix = new Matrix();
	private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private RectF mContentDstRect, mContentSrcRect;
	private RectF mDragBtnDstRect;

	private PointF mContentDstLeftTopPoint, mContentDstRightTopPoint, mContentDstLeftBottomPoint, mContentDstRigintBottomPoint;

	private Bitmap mContentBitmap, mDragBitmap;

	private int mActivePointerId = INVALID_POINTER;
	private float mLastX, mLastY;
	private float mCenterX, mCenterY;
	/**
	 * 旋转和缩放围绕的中心点
	 */
	private PointF mOriginPoint;

	private boolean mIsTouchMode;

	/**
	 * 图片旋转的累计角度
	 */
	private float mDegreesRotate;

	private int mBitmapWidth, mBitmapHeight;

	public ControlMaskImageView(Context context) {
		super(context);
		initialize(context);
	}

	public ControlMaskImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public ControlMaskImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	private void initialize(Context context) {
		mContentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_mask);
		mDragBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_control);

		mBitmapWidth = mContentBitmap.getWidth();
		mBitmapHeight = mContentBitmap.getHeight();
		mContentDstRect = new RectF();
		mDragBtnDstRect = new RectF();

		mContentSrcRect = new RectF(0, 0, mBitmapWidth, mBitmapHeight);
		mContentDstLeftTopPoint = new PointF();
		mContentDstRightTopPoint = new PointF();
		mContentDstLeftBottomPoint = new PointF();
		mContentDstRigintBottomPoint = new PointF();

		mMatrix = null;

		mLinePaint.setColor(Color.WHITE);
		mLinePaint.setStrokeWidth(3);
		mLinePaint.setStyle(Paint.Style.STROKE);
	}

	protected void onDraw(Canvas canvas) {

		if (mMatrix == null) {// 初始化位置,默认控件所占区域的中心
			int width = getWidth();
			int height = getHeight();
			mMatrix = new Matrix();
			float initX = (width - mBitmapWidth) / 2;
			float initY = (height - mBitmapHeight) / 2;
			mMatrix.postTranslate(initX, initY);
			matrixCheck();
		}

		canvas.save();
		canvas.drawBitmap(mContentBitmap, mMatrix, null);
		canvas.restore();

			if (mIsTouchMode||mCurrentMode != OUTSIDE) {
				Log.e("simon", "mDegreesRotate>>" + mDegreesRotate);
//				drawLine(canvas);
				setAnimPaint();
				Path path=new Path();
				path.moveTo(mContentDstLeftTopPoint.x,mContentDstLeftTopPoint.y);
				path.lineTo(mContentDstRightTopPoint.x,mContentDstRightTopPoint.y);
				path.lineTo(mContentDstRigintBottomPoint.x,mContentDstRigintBottomPoint.y);
				path.lineTo(mContentDstLeftBottomPoint.x,mContentDstLeftBottomPoint.y);
				path.lineTo(mContentDstLeftTopPoint.x,mContentDstLeftTopPoint.y);
				canvas.drawPath(path,mLinePaint);

				canvas.save();
				canvas.rotate(mDegreesRotate, mContentDstRigintBottomPoint.x, mContentDstRigintBottomPoint.y);
				canvas.drawBitmap(mDragBitmap, mDragBtnDstRect.left, mDragBtnDstRect.top, null);
				canvas.restore();

				invalidate();
			}
	}

	/**
	 * 画图片实际区域矩形框
	 * @param canvas
	 */
	private void drawLine(Canvas canvas) {
		canvas.drawLine(mContentDstLeftTopPoint.x, mContentDstLeftTopPoint.y, mContentDstRightTopPoint.x, mContentDstRightTopPoint.y, mLinePaint);
		canvas.drawLine(mContentDstRightTopPoint.x, mContentDstRightTopPoint.y, mContentDstRigintBottomPoint.x, mContentDstRigintBottomPoint.y, mLinePaint);
		canvas.drawLine(mContentDstRigintBottomPoint.x, mContentDstRigintBottomPoint.y, mContentDstLeftBottomPoint.x, mContentDstLeftBottomPoint.y, mLinePaint);
		canvas.drawLine(mContentDstLeftBottomPoint.x, mContentDstLeftBottomPoint.y, mContentDstLeftTopPoint.x, mContentDstLeftTopPoint.y, mLinePaint);
	}
	// 动画画笔更新
	private float phase;// 动画画笔（变换相位用）
	private void setAnimPaint()
	{
		phase++; // 变相位
		Path p = new Path();
		p.addRect(new RectF(0, 0, 6, 3), Path.Direction.CCW); // 路径单元是矩形（也可以为椭圆）
		PathDashPathEffect effect = new PathDashPathEffect(p, 12, phase, // 设置路径效果
				PathDashPathEffect.Style.ROTATE);
		mLinePaint.setColor(Color.WHITE);
		mLinePaint.setPathEffect(effect);
	}

	public boolean onTouchEvent(MotionEvent event) {

		final int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			mIsTouchMode = true;
			mCurrentMode = NONE;
			final int pointerIndex = MotionEventCompat.getActionIndex(event);
			mLastX = MotionEventCompat.getX(event, pointerIndex);
			mLastY = MotionEventCompat.getY(event, pointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(event, pointerIndex);
			mCenterX = mContentDstRect.centerX();
			mCenterY = mContentDstRect.centerY();
			Log.e("simon", "centerX>>" + mCenterX + "  centerY" + mCenterY);
			mOriginPoint = new PointF(mCenterX, mCenterY);
			if (isInsideRF(mLastX, mLastY, mDragBtnDstRect)) {//准备拖动
				mCurrentMode = DRAG;
			} else if (isInsideContent(mLastX, mLastY)) {//移动
				mCurrentMode = TRANSLATE;
			} else {//不在范围内
				mCurrentMode = OUTSIDE;
			}
		}
		case MotionEvent.ACTION_MOVE: {
			
			final int pointerIndex = MotionEventCompat.getActionIndex(event);
			int activePointerId = MotionEventCompat.getPointerId(event, pointerIndex);
			Log.e("simon", "activePointerId>>" + activePointerId + ">>mActivePointerId>>" + mActivePointerId);
			if (activePointerId == INVALID_POINTER||activePointerId!=mActivePointerId) {
				break;
			}
			
			if (mCurrentMode == DRAG) {
				PointF lastPoint = new PointF(mLastX, mLastY);
				PointF currentPoint = new PointF(event.getX(), event.getY());
				float scale = getScale(mOriginPoint, lastPoint, currentPoint);
				mMatrix.postScale(scale, scale, mCenterX, mCenterY);// 縮放
				float angle = getAngle(mOriginPoint, lastPoint, currentPoint);
				mDegreesRotate += angle;
				mMatrix.postRotate(angle, mCenterX, mCenterY);// 旋轉
				matrixCheck();
				mLastX = MotionEventCompat.getX(event, pointerIndex);
				mLastY = MotionEventCompat.getY(event, pointerIndex);
				invalidate();
			} else if (mCurrentMode == TRANSLATE) {
				mMatrix.postTranslate(event.getX() - mLastX, event.getY() - mLastY);// 平移
				matrixCheck();
				mLastX = MotionEventCompat.getX(event, pointerIndex);
				mLastY = MotionEventCompat.getY(event, pointerIndex);
				invalidate();
			}


			break;
		}
		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER;
			Log.e("simon", "mActivePointerId>>" + mActivePointerId );
			mIsTouchMode = false;
			invalidate();
			break;
		}
		}
		return true;
	}

	/**
	 * 矩阵变换后改变参数，参数检查
	 * 
	 * @return
	 */
	private boolean matrixCheck() {
		reSetDstRect(mMatrix, mContentDstRect, mContentSrcRect);
		refreshBitmapVerticesPoint();
		reSetDragBtnRect();
		return true;//可以在这里做一些检查
	}

	/**
	 * 获取矩阵变换后，图片实际的四个顶点
	 */
	private void refreshBitmapVerticesPoint() {
		float[] f = new float[9];
		mMatrix.getValues(f);
		mContentDstLeftTopPoint.x = f[0] * 0 + f[1] * 0 + f[2];
		mContentDstLeftTopPoint.y = f[3] * 0 + f[4] * 0 + f[5];
		mContentDstRightTopPoint.x = f[0] * mBitmapWidth + f[1] * 0 + f[2];
		mContentDstRightTopPoint.y = f[3] * mBitmapWidth + f[4] * 0 + f[5];
		mContentDstLeftBottomPoint.x = f[0] * 0 + f[1] * mBitmapHeight + f[2];
		mContentDstLeftBottomPoint.y = f[3] * 0 + f[4] * mBitmapHeight + f[5];
		mContentDstRigintBottomPoint.x = f[0] * mBitmapWidth + f[1] * mBitmapHeight + f[2];
		mContentDstRigintBottomPoint.y = f[3] * mBitmapWidth + f[4] * mBitmapHeight + f[5];
	}

	/**
	 * 刷新图片矩阵变换后，实际占有的矩形区域大小
	 * @param matrix
	 * @param dstRect
	 * @param srcRect
	 */
	public void reSetDstRect(Matrix matrix, RectF dstRect, RectF srcRect) {
		matrix.mapRect(dstRect, srcRect);
	}
	/**
	 * 重新获取拖动按钮实际所在区域
	 */
	private void reSetDragBtnRect() {
		mDragBtnDstRect = new RectF(mContentDstRigintBottomPoint.x - (mDragBitmap.getWidth() * 1.0f / 2), mContentDstRigintBottomPoint.y - (mDragBitmap.getHeight() * 1.0f / 2), mContentDstRigintBottomPoint.x + (mDragBitmap.getWidth() * 1.0f / 2), mContentDstRigintBottomPoint.y + (mDragBitmap.getHeight() * 1.0f / 2));
	}

	/**
	 * 矩形区间内是否包含某一个点
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
	 * @param pointX
	 * @param pointY
	 * @return
	 */
	private boolean isInsideContent(float pointX, float pointY) {
		PointF pointF = new PointF(pointX, pointY);
		PointF[] vertexPointFs = new PointF[] { mContentDstLeftTopPoint, mContentDstRightTopPoint, mContentDstRigintBottomPoint, mContentDstLeftBottomPoint };
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
	 * 求角∠P2P1P3 (带正负角度(顺时针逆时针))
	 * @param p1    圆心 (围绕旋转的点，这里为Rect的中心点)
	 * @param p2    老坐标
	 * @param p3    新坐标 
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
	 *  求以p1p3为半径的圆 /以p1p2为半径的圆，得到缩放的比例
	 *   
	 * @param p1    圆心 (围绕旋转的点，这里为Rect的中心点)
	 * @param p2    老坐标
	 * @param p3    新坐标 
	 * @return
	 */
	private float getScale(PointF p1, PointF p2, PointF p3) {
		double p1p2 = Math.hypot(p1.x - p2.x, p1.y - p2.y);
		double p1p3 = Math.hypot(p1.x - p3.x, p1.y - p3.y);
		//新半径/老半径
		return (float) (p1p3 / p1p2);
	}


}