package com.rpos.pos.data.remote.api;

import com.rpos.pos.data.remote.dto.licenceserver.LicenceServerLoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LicenceKeyApiService {


    /*@FormUrlEncoded
    @POST("login")
    Call<LoginResponseDTO> branchLogin(@Field("email") String email,@Field("password") String password);

    @FormUrlEncoded
    @POST("employee/check")
    Call<CheckInResponse> checkInEmployee(@Field("employee_id") String employeeId,@Field("branch_id") String branchId,@Field("date") String date,@Field("time") String time);


    @FormUrlEncoded
    @POST("employee/list")
    Call<EmployeeListDTO> getEmployeeList(@Field("branch_id") String branchId);*/


    @FormUrlEncoded
    @POST("api/login")
    Call<LicenceServerLoginResponse> licenceKeyValidate(@Field("license_key") String licenceKey);


}
