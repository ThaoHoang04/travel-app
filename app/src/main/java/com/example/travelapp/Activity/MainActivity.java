    package com.example.travelapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.travelapp.Adapter.CategoryAdapter;
import com.example.travelapp.Adapter.PopularAdapter;
import com.example.travelapp.Adapter.RecimmendedAdapter;
import com.example.travelapp.Adapter.SliderAdapter;
import com.example.travelapp.Domain.Category;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.Location;
import com.example.travelapp.Domain.slider;
import com.example.travelapp.R;
import com.example.travelapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

    public class MainActivity extends BaseActivity {
        ActivityMainBinding binding;
        private ArrayList<Location> locationList = new ArrayList<>(); // Danh sách đầy đủ địa điểm

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            EdgeToEdge.enable(this);
            setContentView(binding.getRoot());
            initLocation();
            searchLocation();
            intBanner();
            initCategory();
            initRecommended();
            initPopular();
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", ""); // Lấy username từ bộ nhớ
            Toast.makeText(this, "Xin chào " + username, Toast.LENGTH_SHORT).show();

            ChipNavigationBar chipNavigationBar = findViewById(R.id.chipNavigationBar);
            // Xử lý sự kiện khi nhấn vào menu
            chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int id) {


                    if (id == R.id.profile) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.putExtra("username", username);  // Truyền username thật
                        startActivity(intent);
                    }
                    else if(id == R.id.cart){
                        Intent intent = new Intent(MainActivity.this, BookMarkActivity.class);
                        startActivity(intent);
                    }
                    else if(id == R.id.favorites){
                        Intent intent = new Intent(MainActivity.this, ExplorerActivity.class);
                        startActivity(intent);

                    }
                }
            });
            binding.btnSearch.setOnClickListener(view -> searchLocation());

        }

        // Hàm tìm kiếm địa điểm theo từ khóa nhập vào
        private void searchLocation() {
            String query = binding.txtSearch.getText().toString().trim().toLowerCase();

            if (query.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa điểm", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Location> filteredList = new ArrayList<>();
            for (Location loc : locationList) {
                if (loc.getLoc().toLowerCase().contains(query)) {
                    filteredList.add(loc);
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
            }

            // Cập nhật dữ liệu vào Spinner
            ArrayAdapter<Location> adapter = new ArrayAdapter<>(this, R.layout.sp_item, filteredList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.locationspiner.setAdapter(adapter);
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
            binding.progressBarRecommended.setVisibility(View.VISIBLE);

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
                             binding.recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                             RecyclerView.Adapter adapter = new RecimmendedAdapter(list);
                             binding.recyclerViewRecommended.setAdapter(adapter);
                             adapter.notifyDataSetChanged();  // Gọi notifyDataSetChanged()
                         }
                         binding.progressBarRecommended.setVisibility(View.GONE);
                     }
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
                    if(snapshot.exists()) {
                        for(DataSnapshot issue:snapshot.getChildren()) {
                            list.add(issue.getValue(Category.class));
                        }
                        if(!list.isEmpty()) {
                            binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL, false));
                            RecyclerView.Adapter adapter = new CategoryAdapter(list);
                            binding.recyclerViewCategory.setAdapter(adapter);
                            adapter.notifyDataSetChanged();  // Gọi notifyDataSetChanged()
                        }
                        binding.progressBarCategory.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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
                        binding.locationspiner.setAdapter(adapter);
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
    }