package com.dl.chinamap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Created by dalong  on 2017/6/3.
 */

public class ProvinceItem {
    //id
    public String id;
    //名字
    public String name;
    //绘制路径
    public Path path;
    //绘制颜色
    public int drawColor;

    public ProvinceItem(String id, String name, Path path, int drawColor) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.drawColor = drawColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getDrawColor() {
        return drawColor;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    /**
     * 绘制item
     *
     * @param canvas
     * @param paint
     * @param isSelected
     */
    public void drawItem(Canvas canvas, Paint paint, boolean isSelected) {
        if (isSelected) {
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8, 0, 0, Color.RED);
            canvas.drawPath(path, paint);

            paint.clearShadowLayer();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(path, paint);
        } else {
            //非选中时，绘制描边效果
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(drawColor);
            canvas.drawPath(path, paint);

            paint.setStyle(Paint.Style.STROKE);
            int strokeColor = 0xFFD0E8F4;
            paint.setColor(strokeColor);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 是否触摸
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isTouch(int x, int y) {
        //构造一个区域对象
        RectF rectF = new RectF();
        // 计算控制点的边界
        path.computeBounds(rectF, true);
        Region region = new Region();
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        return region.contains(x, y);
    }
}
