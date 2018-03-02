package com.example.titomi.workertrackerloginmodule.APIs.rest;

import com.example.titomi.workertrackerloginmodule.APIs.model.Users;
import com.example.titomi.workertrackerloginmodule.APIs.model.leaveModel.LeaveModel;

import java.util.List;

/**
 * Created by Titomi on 2/20/2018.
 */

public class ServerEvent {
    private Users users;
    private List<LeaveModel> leaveModel;

    public List<LeaveModel> getLeaveModel() {
        return leaveModel;
    }

    public void setLeaveModel(List<LeaveModel> leaveModel) {
        this.leaveModel = leaveModel;
    }

    public ServerEvent(Users users){
        this.users = users;

    }

    public ServerEvent(List<LeaveModel> leaveModel){
        this.leaveModel = leaveModel;
    }

    public Users getUsers(){
        return users;
    }

    public void setUsers(Users users){
        this.users = users;
    }
}
