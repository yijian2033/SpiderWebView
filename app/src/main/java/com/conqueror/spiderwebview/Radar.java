package com.conqueror.spiderwebview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/4/5.
 */

public class Radar extends View {


    private int count = 6;//绘制几边形
    private float angle = (float) (Math.PI * 2 / count);
    private float radius;//网格最大的半径
    private int centerX;//中心X
    private int centerY;//中心Y


    private String[] titles = {"助攻", "防御", "物理", "魔法", "金钱", "击杀"};//标题
    private Double[] data = {80.0, 90.0, 70.0, 80.0, 100.0, 90.0, 80.0, 90.0};//数值(各维度分值)
    private float maxValue = 100;//最大数值
    private Paint mainPaint;                //雷达区画笔
    private Paint valuePaint;               //数据区画笔
    private Paint textPaint;                //文本画笔

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public void setData(Double[] data) {
        this.data = data;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setMainPaintCoor(int mainPaintCoor) {
        this.mainPaint.setColor(mainPaintCoor);
    }

    public void setTextPaintColor(int textPaintColor) {
        this.textPaint.setColor(textPaintColor);
    }

    public void setValuePaintColor(int valuePaintColor) {
        this.valuePaint.setColor(valuePaintColor);
    }

    public Radar(Context context) {
        this(context, null);
    }

    public Radar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mainPaint = new Paint();
        textPaint = new Paint();
        valuePaint = new Paint();

        mainPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStyle(Paint.Style.STROKE);
        valuePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = (float) (Math.min(w, h) / 2 * 0.9);
        centerX = w / 2;//中心点X
        centerY = h / 2;//中心点Y
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(canvas);
        drawCenterLine(canvas);
        drawRadarText(canvas);
        drawRegion(canvas);
    }

    /**
     * 绘制正多边形
     *
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        float distance = radius / (count - 1);//给正变形之间的距离（蜘蛛丝之间的距离）
        for (int i = 1; i < count; i++) {//中心点不用绘制
            float currentR = distance * i;//当前半径
            path.reset();//重设路径
            for (int j = 0; j < count; j++) {//开始绘制
                if (j == 0) {
                    path.moveTo(centerX + currentR, centerY);//首先把坐标中心点移到要绘制的地方
                } else {
                    //根据正余弦计算出每个点的坐标
                    float x = (float) (centerX + currentR * Math.cos(angle * j));
                    float y = (float) (centerY + currentR * Math.sin(angle * j));
                    path.lineTo(x, y);
                }
            }
            path.close();//闭合路径
            canvas.drawPath(path, mainPaint);
        }

    }

    /**
     * 绘制各个线
     *
     * @param canvas
     */
    private void drawCenterLine(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            float x = (float) (centerX + radius * Math.cos(angle * i));
            float y = (float) (centerY + radius * Math.sin(angle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制文本
     *
     * @param canvas
     */
    private void drawRadarText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;

        for (int i = 0; i < count; i++) {
            float x = (float) (centerX + (radius + fontHeight / 2) * Math.cos(angle * i));
            float y = (float) (centerY + (radius + fontHeight / 2) * Math.sin(angle * i));
            if (angle * i == 0) {//第4象限
                canvas.drawText(titles[i], x, y, textPaint);
            } else if (angle * i >= 3 * Math.PI / 2 && angle * i <= Math.PI * 2) {//第3象限
                canvas.drawText(titles[i], x, y, textPaint);
            } else if (angle * i > Math.PI / 2 && angle * i <= Math.PI) {//第2象限
                float dis = textPaint.measureText(titles[i]);//文本长度
                canvas.drawText(titles[i], x - dis, y + dis, textPaint);
            } else if (angle * i >= Math.PI && angle * i < 3 * Math.PI / 2) {//第1象限
                float dis = textPaint.measureText(titles[i]);//文本长度
                canvas.drawText(titles[i], x - dis, y, textPaint);
            } else if (angle * i > 0 && angle * i <= Math.PI / 2) {
                float dis = textPaint.measureText(titles[i]);//文本长度
                canvas.drawText(titles[i], x + dis, y + dis, textPaint);
            }

        }

    }

    /***
     * 绘制区域
     *
     * @param canvas
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        valuePaint.setAlpha(255);
        valuePaint.setColor(Color.BLUE);
        for (int i = 0; i < count; i++) {
            double percent = data[i] / maxValue;
            float x = (float) (centerX + radius * Math.cos(angle * i) * percent);
            float y = (float) (centerY + radius * Math.sin(angle * i) * percent);
            if (i == 0) {
                path.moveTo(x, centerY);
            } else {
                path.lineTo(x, y);
            }
            canvas.drawCircle(x, y, 10, valuePaint);//绘制圆点
        }
//        valuePaint.setStyle(Paint.Style.STROKE);
//        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }

}
