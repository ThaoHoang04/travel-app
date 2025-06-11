package com.example.travelapp.Interface;

import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.ItemResponse;
import com.example.travelapp.Domain.Location;
import com.example.travelapp.Domain.MailTo;
import com.example.travelapp.Domain.MyTicket;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/item")
    @Headers({
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ii1PUGRjWnoxNXVYR0RCNVByLUpzIiwiZW1haWwiOiJodXkwNDA0MjRAZ21haWwuY29tIiwiZnVsbG5hbWUiOiJIdXkgTmd1eeG7hW4iLCJpYXQiOjE3NDgxNjQ4NDksImV4cCI6MTc1NjgwNDg0OX0.T8fM3e1IoEhE5eFShKsoGIrLnGJz6OH5YHQsN_88pqM"
    })
    Call<ItemResponse> searchLocation(@Query("q") String query);
    @GET("/api/item")
    @Headers({
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ii1PUGRjWnoxNXVYR0RCNVByLUpzIiwiZW1haWwiOiJodXkwNDA0MjRAZ21haWwuY29tIiwiZnVsbG5hbWUiOiJIdXkgTmd1eeG7hW4iLCJpYXQiOjE3NDgxNjQ4NDksImV4cCI6MTc1NjgwNDg0OX0.T8fM3e1IoEhE5eFShKsoGIrLnGJz6OH5YHQsN_88pqM"
    })
    Call<List<ItemDomain>> GetItem(@Query("c") int categoryId);
    @GET("api/order")
    @Headers({
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ii1PUGRjWnoxNXVYR0RCNVByLUpzIiwiZW1haWwiOiJodXkwNDA0MjRAZ21haWwuY29tIiwiZnVsbG5hbWUiOiJIdXkgTmd1eeG7hW4iLCJpYXQiOjE3NDgxNjQ4NDksImV4cCI6MTc1NjgwNDg0OX0.T8fM3e1IoEhE5eFShKsoGIrLnGJz6OH5YHQsN_88pqM"
    })
    Call<List<MyTicket>> getOrders(@Query("u") String username);
    @POST("api/otp/send-email")
    @Headers({
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ii1PUGRjWnoxNXVYR0RCNVByLUpzIiwiZW1haWwiOiJodXkwNDA0MjRAZ21haWwuY29tIiwiZnVsbG5hbWUiOiJIdXkgTmd1eeG7hW4iLCJpYXQiOjE3NDgxNjQ4NDksImV4cCI6MTc1NjgwNDg0OX0.T8fM3e1IoEhE5eFShKsoGIrLnGJz6OH5YHQsN_88pqM"
    })
    Call<ResponseBody> sendEmail(@Body MailTo mailToRequest);
}


