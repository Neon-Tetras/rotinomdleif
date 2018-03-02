package com.example.titomi.workertrackerloginmodule.APIs.Interfaces;

import com.example.titomi.workertrackerloginmodule.APIs.model.leaveModel.LeaveModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Titomi on 2/22/2018.
 */

public interface LeaveApiService {

    String view = "by_id";

    @GET("leave/view.php")
    Call<List<LeaveModel>> getLeave(
            @Query("key") String key,
            @Query("view")  String view,
            @Query("id") String id);
}
