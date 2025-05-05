package com.example.travelapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelapp.Adapter.MyTicketAdapter;
import com.example.travelapp.Domain.ApiClient;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.MyTicket;
import com.example.travelapp.Interface.ApiService;
import com.example.travelapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTicketActivity extends AppCompatActivity {
    String itemID;
    ItemDomain items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_ticket);
        loadTicket();
        itemID = getIntent().getStringExtra("itemsId");
        items = (ItemDomain) getIntent().getSerializableExtra("object");
    }

    private void loadTicket() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<MyTicket>> call = apiService.getOrders(username);

        call.enqueue(new Callback<List<MyTicket>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<MyTicket>> call, Response<List<MyTicket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MyTicket> result = response.body();
                    if (result.isEmpty()) {
                        Toast.makeText(MyTicketActivity.this, "Không lấy được items", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("review");
                        reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<String> reviewedOrderIds = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String reviewedOrderId = snapshot.child("orderId").getValue(String.class);
                                    if (reviewedOrderId != null) {
                                        reviewedOrderIds.add(reviewedOrderId);
                                    }
                                }
                                List<ItemDomain> tourList = new ArrayList<>();
                                List<MyTicket> filteredTickets = new ArrayList<>();

                                for (MyTicket rs : result) {
                                    if ((itemID != null && itemID.equals(rs.getItemId())) ||
                                            reviewedOrderIds.contains(rs.getOrderId())) {
                                        continue; // Bỏ qua tour đã review hoặc đã được truyền itemID
                                    }
                                    tourList.add(rs.getTourInfo());
                                    filteredTickets.add(rs);
                                }

                                MyTicketAdapter myTicketAdapter = new MyTicketAdapter(MyTicketActivity.this, tourList);
                                ListView listView = findViewById(R.id.lvMyTicket);
                                listView.setAdapter(myTicketAdapter);

                                listView.setOnItemClickListener((parent, view, position, id) -> {
                                    MyTicket selectedTicket = filteredTickets.get(position);
                                    LocalDate currentDate = LocalDate.now();
                                    LocalDate tourDate = LocalDate.parse(selectedTicket.getTourInfo().getDateTour());

                                    if (currentDate.isAfter(tourDate)) {
                                        Intent reviewIntent = new Intent(MyTicketActivity.this, ReviewActivity.class);
                                        reviewIntent.putExtra("object", selectedTicket.getTourInfo());
                                        reviewIntent.putExtra("orderid", selectedTicket.getOrderId());
                                        startActivity(reviewIntent);
                                    } else {
                                        Intent ticketIntent = new Intent(MyTicketActivity.this, TicketActivity.class);
                                        ticketIntent.putExtra("object", selectedTicket.getTourInfo());
                                        startActivity(ticketIntent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(MyTicketActivity.this, "Lỗi khi tải dữ liệu review", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyTicket>> call, Throwable t) {
                Toast.makeText(MyTicketActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}