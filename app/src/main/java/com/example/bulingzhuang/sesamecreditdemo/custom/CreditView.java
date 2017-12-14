package com.example.bulingzhuang.sesamecreditdemo.custom;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.bulingzhuang.sesamecreditdemo.R;
import com.example.bulingzhuang.sesamecreditdemo.utils.Tools;

/**
 * Created by bulingzhuang
 * on 2016/11/30
 * E-mail:bulingzhuang@foxmail.com
 */

public class CreditView extends View {

    private String[] texts = {"较差", "中等", "良好", "优秀", "极好"};
    private int mMaxNum, mStartAngle, mSweepAngle;
    private int mSweepInWidth, mSweepOutWidth;
    private Paint mPaint, mPaint_1, mPaint_2, mPaint_3;
    private int mWSize, mHSize;
    private int mWMode, mHMode;
    private int mWidth, mHeight;
    private int mRadius;
    private Context mContext;

    private int[] indicatorColor = {0xffffffff, 0x00ffffff, 0x99ffffff, 0xffffffff};

    private int currentNum;

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
        invalidate();
    }

    public void setCurrentNumAnim(int num) {
        float duration = (float) Math.abs(num - currentNum) / mMaxNum * 1500 + 500;
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentNum", num);
        anim.setDuration((long) Math.min(duration, 2000));
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue("currentNum");
                int color = calculateColor(value);
                setBackgroundColor(color);
            }
        });
        anim.start();
    }

    private int calculateColor(int value) {
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        float fraction;
        int color;
        if (value <= mMaxNum / 2) {
            fraction = (float) value/(mMaxNum/2);
            color = (int) argbEvaluator.evaluate(fraction,0xFFFF6347,0xFFFF8C00);//由红到橙
        }else {
            fraction = ( (float)value-mMaxNum/2 ) / (mMaxNum/2);
            color = (int) argbEvaluator.evaluate(fraction,0xFFFF8C00,0xFF00CED1); //由橙到蓝
        }
        return color;
    }

    public CreditView(Context context) {
        this(context, null);
    }

    public CreditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //对当前控件不使用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initAttr(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint_1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CreditView);
        mMaxNum = typedArray.getInt(R.styleable.CreditView_maxNum, 500);
        mStartAngle = typedArray.getInt(R.styleable.CreditView_startAngle, 160);
        mSweepAngle = typedArray.getInt(R.styleable.CreditView_sweepAngle, 220);

        //内外圆环弧度的宽度
        mSweepInWidth = Tools.dp2px(context, 8);
        mSweepOutWidth = Tools.dp2px(context, 3);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWSize = MeasureSpec.getSize(widthMeasureSpec);
        mWMode = MeasureSpec.getMode(widthMeasureSpec);
        mHSize = MeasureSpec.getSize(heightMeasureSpec);
        mHMode = MeasureSpec.getMode(heightMeasureSpec);

        if (mWMode == MeasureSpec.EXACTLY) {
            mWidth = mWSize;
        } else {
            mWidth = Tools.dp2px(mContext, 300);
        }

        if (mHMode == MeasureSpec.EXACTLY) {
            mHeight = mHSize;
        } else {
            mHeight = Tools.dp2px(mContext, 400);
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRadius = getMeasuredWidth() / 4;
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);
        drawRound(canvas);//画内外圆弧
        drawScale(canvas);//画刻度
        drawIndicator(canvas);//画当前进度值
        drawCenterText(canvas);//画中间文字
        canvas.restore();
    }

    private void drawCenterText(Canvas canvas) {
        canvas.save();
        mPaint_3.setStyle(Paint.Style.FILL);
        mPaint_3.setTextSize(mRadius / 2);
        mPaint_3.setColor(0xffffffff);
        Log.e("blz","画数值："+currentNum);
        canvas.drawText(currentNum + "", -mPaint_3.measureText(currentNum + "") / 2, 0, mPaint_3);
        mPaint_3.setTextSize(mRadius / 4);
        String content = "信用";
        if (currentNum < mMaxNum / 5) {
            content += texts[0];
        } else if (currentNum >= mMaxNum / 5 && currentNum < mMaxNum * 2 / 5) {
            content += texts[1];
        } else if (currentNum >= mMaxNum * 2 / 5 && currentNum < mMaxNum * 3 / 5) {
            content += texts[2];
        } else if (currentNum >= mMaxNum * 3 / 5 && currentNum < mMaxNum * 4 / 5) {
            content += texts[3];
        } else if (currentNum >= mMaxNum * 4 / 5) {
            content += texts[4];
        }
        Rect rect = new Rect();
        mPaint_3.getTextBounds(content, 0, content.length(), rect);
        canvas.drawText(content, -rect.width() / 2, rect.height() + 20, mPaint_3);
        canvas.restore();
    }

    private void drawIndicator(Canvas canvas) {
        canvas.save();
        mPaint_1.setStyle(Paint.Style.STROKE);
        int sweep;
        if (currentNum <= mMaxNum) {
            sweep = (int) ((float) currentNum / (float) mMaxNum * mSweepAngle);
        } else {
            sweep = mSweepAngle;
        }
        mPaint_1.setStrokeWidth(mSweepOutWidth);
        SweepGradient sweepGradient = new SweepGradient(0, 0, indicatorColor, null);
        mPaint_1.setShader(sweepGradient);
        int w = Tools.dp2px(mContext, 10);
        RectF rectF = new RectF(-mRadius - w, -mRadius - w, mRadius + w, mRadius + w);
        canvas.drawArc(rectF, mStartAngle, sweep, false, mPaint_1);
        float x = (float) ((mRadius + Tools.dp2px(mContext, 10)) * Math.cos(Math.toRadians(mStartAngle + sweep)));
        float y = (float) ((mRadius + Tools.dp2px(mContext, 10)) * Math.sin(Math.toRadians(mStartAngle + sweep)));
        mPaint_2.setStyle(Paint.Style.FILL);
        mPaint_2.setColor(0xffffffff);
        mPaint_2.setMaskFilter(new BlurMaskFilter(Tools.dp2px(mContext, 3), BlurMaskFilter.Blur.SOLID));
        canvas.drawCircle(x, y, Tools.dp2px(mContext, 3), mPaint_2);
        canvas.restore();
    }

    private void drawScale(Canvas canvas) {
        canvas.save();
        float angle = (float) mSweepAngle / 30;//刻度间隔
        canvas.rotate(-270 + mStartAngle);
        for (int i = 0; i <= 30; i++) {
            if (i % 6 == 0) {//画粗刻度和刻度值
                mPaint.setStrokeWidth(Tools.dp2px(mContext, 2));
                mPaint.setAlpha(0x70);
                canvas.drawLine(0, -mRadius - mSweepInWidth / 2, 0, -mRadius + mSweepInWidth / 2 + Tools.dp2px(mContext, 1), mPaint);
                drawText(canvas, i * mMaxNum / 30 + "", mPaint);
            } else {
                mPaint.setStrokeWidth(Tools.dp2px(mContext, 1));
                mPaint.setAlpha(0x50);
                canvas.drawLine(0, -mRadius - mSweepInWidth / 2, 0, -mRadius + mSweepInWidth / 2, mPaint);
            }
            if (i == 3 || i == 9 || i == 15 || i == 21 || i == 27) {
                mPaint.setStrokeWidth(Tools.dp2px(mContext, 2));
                mPaint.setAlpha(0x90);
                drawText(canvas, texts[(i - 3) / 6], mPaint);
            }
            canvas.rotate(angle);
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas, String str, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(Tools.sp2px(mContext, 8));
        float textWidth = mPaint.measureText(str);
        //下面方法获得的数据有字的长和高，但是数值是int型，在小字上会很不精确
//        Rect rect = new Rect();
//        mPaint.getTextBounds(str,0,str.length(),rect);
        canvas.drawText(str, -textWidth / 2, -mRadius + Tools.dp2px(mContext, 15), mPaint);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void drawRound(Canvas canvas) {
        canvas.save();
        //内圆
        mPaint.setAlpha(0x40);
        mPaint.setStrokeWidth(mSweepInWidth);
        RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mPaint);

        //外圆
        mPaint.setStrokeWidth(mSweepOutWidth);
        int w = Tools.dp2px(mContext, 10);
        RectF rectF1 = new RectF(-mRadius - w, -mRadius - w, mRadius + w, mRadius + w);
        canvas.drawArc(rectF1, mStartAngle, mSweepAngle, false, mPaint);
        canvas.restore();
    }
}
