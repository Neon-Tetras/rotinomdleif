package com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceSubMenu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.titomi.workertrackerloginmodule.APIs.model.leaveModel.LeaveModel;
import com.example.titomi.workertrackerloginmodule.R;

import java.util.List;

/**
 * Created by Titomi on 2/22/2018.
 */

public class LeaveAdapter extends RecyclerView.Adapter<LeaveRecyclerViewHolder> {

    private List<LeaveModel> leaveModelList;

    public LeaveAdapter(List<LeaveModel> model){
        this.leaveModelList = model;
    }

    @Override
    public LeaveRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.approved_leave_single_item_layout, null);

        LeaveRecyclerViewHolder holder = new LeaveRecyclerViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(LeaveRecyclerViewHolder holder, int position) {
        holder.dateSubmitted.setText((Integer) leaveModelList.get(position).getDate());
        holder.leave_status.setText(leaveModelList.get(position).getStatus());
        holder.leave_reason.setText(leaveModelList.get(position).getReason());
    }

    @Override
    public int getItemCount() {
        return leaveModelList.size();
    }

}
