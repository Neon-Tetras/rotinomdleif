package com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceSubMenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.APIs.model.leaveModel.LeaveModel;
import com.example.titomi.workertrackerloginmodule.R;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Titomi on 2/24/2018.
 */

public class AdapterLeave extends RecyclerView.Adapter<AdapterLeave.ViewHolder>{

    private List<LeaveModel> leaveModels;
private     Context mContext;

    public AdapterLeave(Context context, List<LeaveModel> getDataSet) {
        this.mContext = context;
        this.leaveModels = getDataSet;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.approved_leave_single_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LeaveModel leaveModel = leaveModels.get(position);

        holder.leave_reason.setText(leaveModel.getReason());
        holder.leave_status.setText(leaveModel.getStatus());
        DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT,Locale.getDefault());

        holder.dateSubmitted.setText(dtf.format(leaveModel.getDate()));
    }

    @Override
    public int getItemCount() {
        return leaveModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView leave_status, leave_reason, dateSubmitted;

        public ViewHolder(View v) {
            super(v);

            leave_reason = v.findViewById(R.id.leave_reason);
            leave_status = v.findViewById(R.id.leave_status);
            dateSubmitted = v.findViewById(R.id.dateSubmitted);
        }
    }
}
