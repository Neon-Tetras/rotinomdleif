package com.example.titomi.workertrackerloginmodule.report_module;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;

import java.util.List;

/**
 * Created by Titomi on 2/12/2018.
 */

public class ListAdapterClass extends BaseAdapter {

    Context context;
    List<Report> valueList;

    public ListAdapterClass(List<Report> listValue, Context context)
    {
        this.context = context;
        this.valueList = listValue;
    }

    @Override
    public int getCount()
    {
        return this.valueList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.valueList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewItem viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItem();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.reportlistview_item, null);

            viewItem.TextViewStudentName = (TextView)convertView.findViewById(R.id.textview1);

            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItem) convertView.getTag();
        }

        viewItem.TextViewStudentName.setText(valueList.get(position).TaskId);

        return convertView;
    }
}

class ViewItem
{
    TextView TextViewStudentName;

}
