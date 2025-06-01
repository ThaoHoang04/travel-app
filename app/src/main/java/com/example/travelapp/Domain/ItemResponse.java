package com.example.travelapp.Domain;

import java.util.List;

public class ItemResponse {
    private List<ItemDomain> data;

    public List<ItemDomain> getData() {
        return data;
    }

    public void setData(List<ItemDomain> data) {
        this.data = data;
    }
}

