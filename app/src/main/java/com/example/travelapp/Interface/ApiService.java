package com.example.travelapp.Interface;

import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/item")
    Call<List<ItemDomain>> searchLocation(@Query("q") String query);
}
