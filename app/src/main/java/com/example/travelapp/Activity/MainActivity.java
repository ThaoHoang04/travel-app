package com.example.travelapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.travelapp.Adapter.CategoryAdapter;
import com.example.travelapp.Adapter.FavoriteAdapter;
import com.example.travelapp.Adapter.PopularAdapter;
import com.example.travelapp.Adapter.RecimmendedAdapter;
import com.example.travelapp.Adapter.SliderAdapter;
import com.example.travelapp.Domain.ApiClient;
import com.example.travelapp.Domain.Category;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.Location;
import com.example.travelapp.Domain.slider;
import com.example.travelapp.Interface.ApiService;
import com.example.travelapp.R;
import com.example.travelapp.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

    public class MainActivity extends BaseActivity {
        ActivityMainBinding binding;
        private boolean isFabMenuOpen = false;
        private ArrayList<Location> locationList = new ArrayList<>(); // Danh sách đầy đủ địa điểm
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            EdgeToEdge.enable(this);
            setContentView(binding.getRoot());
            getWidget();
            initLocation();
            intBanner();
            initCategory();
            initRecommended();
            initPopular();
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", ""); // Lấy username từ bộ nhớ
            Toast.makeText(this, "Xin chào " + username, Toast.LENGTH_SHORT).show();

            setupBottomNavigation();
            
            binding.btnSearch.setOnClickListener(view -> searchLocation());

        }

        // Hàm tìm kiếm địa điểm theo từ khóa nhập vào
        private void searchLocation() {
            String query = binding.txtSearch.getText().toString().trim().toLowerCase();

            if (query.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa điểm", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
             intent.putExtra("searchResults", query);
                      startActivity(intent);
//                );
            }

        }

        private void initPopular() {
            DatabaseReference myRef = database.getReference("Popular");
            binding.progressBarpopular.setVisibility(View.VISIBLE);

            ArrayList<ItemDomain> list = new ArrayList<>();

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if(snapshot.exists()) {
                            for (DataSnapshot issue : snapshot.getChildren()) {
                                list.add(issue.getValue(ItemDomain.class));
                            }

                            if (!list.isEmpty()) {
                                binding.recyclerViewPopular.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                RecyclerView.Adapter adapter = new PopularAdapter(list);
                                binding.recyclerViewPopular.setAdapter(adapter);
                                adapter.notifyDataSetChanged();  // Gọi notifyDataSetChanged()
                            }
                            binding.progressBarpopular.setVisibility(View.GONE);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void initRecommended() {
            DatabaseReference myRef = database.getReference("Item");
            Query query = myRef.orderByChild("status").equalTo(1);
            binding.progressBarRecommended.setVisibility(View.VISIBLE);

            ArrayList<ItemDomain> list = new ArrayList<>();
            Date today = new Date(); // Ngày hiện tại

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Boolean deleted = issue.child("deleted").getValue(Boolean.class);
                            String dateTourStr = issue.child("dateTour").getValue(String.class);
                            if (deleted != null && !deleted && dateTourStr != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date dateTour = sdf.parse(dateTourStr);
                                    if (dateTour != null && !dateTour.before(today)) {
                                        list.add(issue.getValue(ItemDomain.class));
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace(); // Lỗi định dạng ngày
                                }
                            }
                        }

                        if (!list.isEmpty()) {
                            binding.recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            RecyclerView.Adapter adapter = new RecimmendedAdapter(list);
                            binding.recyclerViewRecommended.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        binding.progressBarRecommended.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        private void initCategory() {
            DatabaseReference myRef = database.getReference("Category");
            binding.progressBarCategory.setVisibility(View.VISIBLE);
            ArrayList<Category> list = new ArrayList<>();

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Boolean deleted = issue.child("deleted").getValue(Boolean.class);
                            Long status = issue.child("status").getValue(Long.class);
                            // Nếu deleted = false (hoặc null) và status = 1 thì mới thêm
                            if ((deleted == null || !deleted) && status != null && status == 1) {
                                Category category = issue.getValue(Category.class);
                                if (category != null) {
                                    list.add(category);
                                }
                            }
                        }

                        if (!list.isEmpty()) {
                            binding.recyclerViewCategory.setLayoutManager(
                                    new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false)
                            );
                            RecyclerView.Adapter adapter = new CategoryAdapter(list);
                            binding.recyclerViewCategory.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        binding.progressBarCategory.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.progressBarCategory.setVisibility(View.GONE);
                    // Xử lý lỗi nếu cần
                }
            });
        }


        private void initLocation() {
            DatabaseReference myRef = database.getReference("Location");
            ArrayList<Location> list = new ArrayList<>();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        locationList.clear();  // Xóa danh sách cũ
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Location loc = issue.getValue(Location.class);
//                            list.add(issue.getValue(Location.class));
                        }
                        ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        binding.locationspiner.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void banner(ArrayList<slider> sliders) {
            binding.viewPagerSlider.setAdapter(new SliderAdapter(sliders, binding.viewPagerSlider));
            binding.viewPagerSlider.setClipToPadding(false);
            binding.viewPagerSlider.setClipChildren(false);
            binding.viewPagerSlider.setOffscreenPageLimit(3);
            binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
            binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
        }
        private void intBanner() {
            DatabaseReference myRef = database.getReference("Banner");
            binding.viewPagerSlider.setVisibility(View.VISIBLE);
            ArrayList<slider> sliders = new ArrayList<>();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for (DataSnapshot issue:snapshot.getChildren()) {
                            sliders.add(issue.getValue(slider.class));
                        }
                        banner(sliders);
                        binding.progressBarBaner.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        private void getWidget() {
            // Already handled in setupBottomNavigation()
        }
    }