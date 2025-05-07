package com.example.travelapp.Interface;

import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.Location;
import com.example.travelapp.Domain.MyTicket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/item")
    @Headers({
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ii1PUGRjWnoxNXVYR0RCNVByLUpzIiwiZW1haWwiOiJodXkwNDA0MjRAZ21haWwuY29tIiwiZnVsbG5hbWUiOiJIdXkgTmd1eeG7hW4iLCJpYXQiOjE3NDY2Mjk5NDMsImV4cCI6MTc1NTI2OTk0M30.8k2JhasGwr9M5En-4957Hj98AEMSs1OpKjZn4qSdfc0"
    })
    Call<List<ItemDomain>> searchLocation( @Query("q") String query);
    @GET("/api/item")
    @Headers({
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ii1PUGRjWnoxNXVYR0RCNVByLUpzIiwiZW1haWwiOiJodXkwNDA0MjRAZ21haWwuY29tIiwiZnVsbG5hbWUiOiJIdXkgTmd1eeG7hW4iLCJpYXQiOjE3NDY2Mjk5NDMsImV4cCI6MTc1NTI2OTk0M30.8k2JhasGwr9M5En-4957Hj98AEMSs1OpKjZn4qSdfc0"
    })
    Call<List<ItemDomain>> GetItem(@Query("c") int categoryId);
    @GET("api/order")
    @Headers({
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ii1PUGRjWnoxNXVYR0RCNVByLUpzIiwiZW1haWwiOiJodXkwNDA0MjRAZ21haWwuY29tIiwiZnVsbG5hbWUiOiJIdXkgTmd1eeG7hW4iLCJpYXQiOjE3NDY2Mjk5NDMsImV4cCI6MTc1NTI2OTk0M30.8k2JhasGwr9M5En-4957Hj98AEMSs1OpKjZn4qSdfc0"
    })
    Call<List<MyTicket>> getOrders(@Query("u") String username);
}


