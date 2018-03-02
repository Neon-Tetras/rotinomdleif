package com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceSubMenu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;

/**
 * Created by Titomi on 2/22/2018.
 */

public class LeaveRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView dateSubmitted, leave_reason, leave_status;

    public LeaveRecyclerViewHolder(View itemView){
        super(itemView);

        itemView.setOnClickListener(this);
        dateSubmitted = itemView.findViewById(R.id.dateSubmitted);
        leave_reason = itemView.findViewById(R.id.leave_reason);
        leave_status = itemView.findViewById(R.id.leave_status);
    }

    @Override
    public void onClick(View v) {

    }
}
