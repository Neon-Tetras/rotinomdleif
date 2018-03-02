package com.example.titomi.workertrackerloginmodule.DashboardFragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.titomi.workertrackerloginmodule.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Titomi on 2/8/2018.
 */

public class FragmentInventory extends Fragment {
    View view;



    public FragmentInventory() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.inventory_fragment, container, false);

        ListView lv = view.findViewById(R.id.listInventoryGraphView);
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Snackbar snackbar = Snackbar.make(swipeRefreshLayout,"Refreshing", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, R.color.accent, R.color.colorPrimaryDark);

        ArrayList<BarData> list = new ArrayList<>();
        for (int i=0; i<4; i++){
            list.add(generateData(i+1));
        }

        ChartDataAdapter chartDataAdapter = new ChartDataAdapter(getContext(), list);
        lv.setAdapter(chartDataAdapter);

        return view;
    }

    private class ChartDataAdapter extends ArrayAdapter<BarData> {

        public ChartDataAdapter(Context context, List<BarData> objects){
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            BarData data = getItem(position);

            ViewHolder holder = null;

            if (convertView == null){
                holder = new ViewHolder();

                convertView = getLayoutInflater().from(getContext()).inflate(R.layout.list_item_barchart, null);
                holder.chart = convertView.findViewById(R.id.chart_list);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            data.setValueTextColor(Color.BLACK);
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setDrawGridBackground(false);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            holder.chart.setData(data);
            holder.chart.setFitBars(true);

            holder.chart.animateY(500);

            return convertView;
        }

        private class ViewHolder{
            BarChart chart;
        }
    }

    private BarData generateData(int count){
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i=0; i<12; i++){
            entries.add(new BarEntry(i, (float) (Math.random()*70)+30));
        }

        BarDataSet dataset = new BarDataSet(entries, "New Data set "+ count);
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);
        dataset.setBarShadowColor(Color.rgb(203,203,203));

        BarData data = new BarData(dataset);
        data.setBarWidth(0.9f);
        return data;
    }
}
