package com.xiaxl.fading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class FadingPic extends View {


    private Paint paint;

    private Paint paint1;

    private Bitmap bitmap;

    private Bitmap rotateBitmap;

    private int height;

    private int width;

    public FadingPic(Context context) {
        super(context);
        init(context, null);
    }

    public FadingPic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FadingPic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        // 原图片
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.beautify);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        // 倒影图片
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        // 从上到下 黑色Alpha值从0~1的渐变
        paint = new Paint();
        paint.setAntiAlias(true);
        //  [Sa * Da, Sa * Dc]
        // alpha:渐变颜色 颜色值:alpha*bitmapColor
        // 可参考：http://www.jianshu.com/p/d11892bbe055
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setShader(new LinearGradient(0, 0, 0, height, 0x00000000, 0xff000000, Shader.TileMode.CLAMP));

        // 从上到下 黑色Alpha从1~0
        paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint1.setShader(new LinearGradient(0, height, 0, 2 * height, 0xff000000, 0x00000000, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(2 * width, 2 * height);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        // ################
        // 1、绘制 左侧 原始图片
        c.drawBitmap(bitmap, 0, 0, null);
        // ################
        // 2、绘制 右侧 渐变图片
        // 创建一个新的图层，在处理xfermode的时候，原canvas上的图（包括背景）会影响src和dst的合成
        c.saveLayer(width, 0,
                width * 2, height,
                null, Canvas.ALL_SAVE_FLAG);
        // 绘制 右侧 渐变图片
        c.drawBitmap(bitmap, width, 0, null);
        //  [Sa * Da, Sa * Dc]
        // alpha:渐变颜色 颜色值:alpha*bitmapColor
        c.drawRect(width, 0, width * 2, height, paint);
        // 将新图层绘制到屏幕上
        c.restore();
        // ################
        // 3、绘制 下边 倒影
        c.drawBitmap(rotateBitmap, 0, height, null);
        // 渐变
        c.drawRect(0, height, width, 2 * height, paint1);
    }
}