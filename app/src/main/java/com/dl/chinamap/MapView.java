package com.dl.chinamap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by dalong  on 2017/6/3.
 */

public class MapView extends View {

    private final String TAG = "MapView";

    public Context mContext;

    private Paint mPaint;

    private int mapRes = -1;

    private List<ProvinceItem> itemList;

    private int minWidth = 200;
    private int minHeight = 200;
    //选择的省
    private ProvinceItem selectItem;

    //缩放系数
    private float scale = 1.3f;

    private RectF totalRectF;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int viewWidth = widthSize;
        int viewHeight = heightSize;

        if (totalRectF != null) {
            float widthScale = viewWidth * 1.0f / totalRectF.width();
            float heightScale = viewHeight * 1.0f / totalRectF.height();
            if (widthScale <= heightScale) {
                viewHeight = Math.min(viewHeight, (int) (widthScale * totalRectF.height()));
            } else {
                viewWidth = Math.min(viewWidth, (int) (heightScale * totalRectF.width()));
            }
            scale = Math.min(widthScale, heightScale);
        }
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(viewWidth, widthMeasureSpec),
                MeasureSpec.makeMeasureSpec(viewHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (itemList != null) {
            canvas.save();
            canvas.scale(scale, scale);
            for (ProvinceItem item : itemList) {
                if (item != selectItem) {
                    item.drawItem(canvas, mPaint, false);
                }
            }
            if (selectItem != null) {
                selectItem.drawItem(canvas, mPaint, true);
            }
        }
    }

    /**
     * 设置地图资源
     *
     * @param mapRes
     */
    public void setMapRes(int mapRes) {
        this.mapRes = mapRes;
    }

    /**
     * 解析地图数据
     */
    public void loadMap() {
        if (mapRes != -1) {
            loadMapThread.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event.getX(), event.getY());
        return true;
    }

    private void handleTouch(float x, float y) {
        if (itemList == null) {
            return;
        }
        ProvinceItem tmpItem = null;
        for (ProvinceItem item : itemList) {
            if (item.isTouch((int) (x / scale), (int) (y / scale))) {
                tmpItem = item;
                if (listener != null) {
                    listener.onItemClick(item);
                }
                break;
            }
        }
        if (tmpItem != null) {
            selectItem = tmpItem;
            postInvalidate();
        }
    }


    /**
     * 解析地图数据线程
     */
    private Thread loadMapThread = new Thread() {
        @Override
        public void run() {
            super.run();
            List<ProvinceItem> list = new ArrayList<>();
            InputStream inputStream = mContext.getResources().openRawResource(mapRes);
            try {
                //取得DocumentBuilderFactory实例
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                //从factory获取DocumentBuilder实例
                DocumentBuilder builder = factory.newDocumentBuilder();
                //解析输入流 得到Document实例
                Document doc = builder.parse(inputStream);
                Element rootElement = doc.getDocumentElement();
                NodeList items = rootElement.getElementsByTagName("path");

                float left = -1;
                float top = -1;
                float right = -1;
                float bottom = -1;
                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);
                    String id = element.getAttribute("android:id");
                    String name = element.getAttribute("android:name");
                    String pathData = element.getAttribute("android:pathData");
                    Log.i(TAG, "id: " + id);
                    Log.i(TAG, "name: " + name);
                    Log.i(TAG, "pathData: " + pathData);


                    Path path = PathParser.createPathFromPathData(pathData);

                    RectF rectF = new RectF();
                    path.computeBounds(rectF, true);

                    left = left == -1 ? rectF.left : Math.min(rectF.left, left);
                    top = top == -1 ? rectF.top : Math.min(rectF.top, top);

                    right = right == -1 ? rectF.right : Math.max(rectF.right, right);
                    bottom = bottom == -1 ? rectF.bottom : Math.max(rectF.bottom, bottom);


                    int drawColor = MapColorUtils.getMapColor(id);
                    ProvinceItem proviceItem = new ProvinceItem(id, name, path, drawColor);
                    list.add(proviceItem);
                }

                //取最大的区域 计算宽高
                totalRectF = new RectF(left, top, right, bottom);

            } catch (Exception e) {
                e.printStackTrace();
            }
            itemList = list;
            mHandler.sendEmptyMessage(0);
        }
    };

    /**
     * 加载地图
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (itemList == null) {
                return;
            }
            requestLayout();
        }
    };

    OnMapItemListener listener;

    public void setOnMapItemListener(OnMapItemListener listener) {
        this.listener = listener;
    }

    interface OnMapItemListener {
        void onItemClick(ProvinceItem item);
    }
}
