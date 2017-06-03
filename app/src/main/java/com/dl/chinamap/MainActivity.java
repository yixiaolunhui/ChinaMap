package com.dl.chinamap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private MapView mapView;
    private TextView mapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.mapView);
        mapName = (TextView) findViewById(R.id.address);
        mapView.setMapRes(R.raw.chinamap);
        mapView.loadMap();

        mapView.setOnMapItemListener(new MapView.OnMapItemListener() {
            @Override
            public void onItemClick(ProvinceItem item) {
                mapName.setText("地址："+item.name);
            }
        });
    }


}
