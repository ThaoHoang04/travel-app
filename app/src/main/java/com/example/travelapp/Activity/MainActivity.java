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
        private ArrayList<Location> locationList = new ArrayList<>(); // Danh sách đầy đủ địa điểm
        private ImageView fabMain, fabZalo, imgAi;
        private FloatingActionButton fabFacebook, fabCall;
        private Animation fabOpen, fabClose;
        private boolean isFabMenuOpen = false;
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
        private void getWidget(){
            // Khởi tạo các view
            fabMain = findViewById(R.id.fab_main);
            fabFacebook = findViewById(R.id.fab_facebook);
            fabZalo = findViewById(R.id.fab_zalo);
            fabCall = findViewById(R.id.fab_call);
            imgAi = findViewById(R.id.img_ai);

            // Khởi tạo animations
            fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
            fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
            // Thiết lập sự kiện click cho nút chính
            fabMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleFabMenu();
                }
            });

            // Thiết lập sự kiện click cho nút Facebook
            fabFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFacebook();
                    toggleFabMenu();
                }
            });

            // Thiết lập sự kiện click cho nút Zalo
            fabZalo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openZalo();
                    toggleFabMenu();
                }
            });

            // Thiết lập sự kiện click cho nút Gọi điện
            fabCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makePhoneCall();
                    toggleFabMenu();
                }
            });

            // Thiết lập sự kiện click cho nút AI
            imgAi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAiAssistant();
                    toggleFabMenu();
                }
            });
        }
        // Phương thức để mở/đóng menu
        private void toggleFabMenu() {
            if (isFabMenuOpen) {
                // Đóng menu
                fabFacebook.startAnimation(fabClose);
                fabZalo.startAnimation(fabClose);
                fabCall.startAnimation(fabClose);
                imgAi.startAnimation(fabClose);

                fabFacebook.setClickable(false);
                fabZalo.setClickable(false);
                fabCall.setClickable(false);
                imgAi.setClickable(false);

                fabFacebook.setVisibility(View.INVISIBLE);
                fabZalo.setVisibility(View.INVISIBLE);
                fabCall.setVisibility(View.INVISIBLE);
                imgAi.setVisibility(View.INVISIBLE);

                isFabMenuOpen = false;
            } else {
                // Mở menu
                fabFacebook.startAnimation(fabOpen);
                fabZalo.startAnimation(fabOpen);
                fabCall.startAnimation(fabOpen);
                imgAi.startAnimation(fabOpen);

                fabFacebook.setClickable(true);
                fabZalo.setClickable(true);
                fabCall.setClickable(true);
                imgAi.setClickable(true);

                fabFacebook.setVisibility(View.VISIBLE);
                fabZalo.setVisibility(View.VISIBLE);
                fabCall.setVisibility(View.VISIBLE);
                imgAi.setVisibility(View.VISIBLE);

                isFabMenuOpen = true;
            }
        }

        // Phương thức mở Facebook
        private void openFacebook() {
            try {
                Intent intent = getPackageManager().getLaunchIntentForPackage("https://www.facebook.com/huydz24");
                if (intent != null) {
                    // Ứng dụng Facebook đã được cài đặt, mở nó
                    startActivity(intent);
                } else {
                    // Ứng dụng Facebook chưa được cài đặt, mở trang web
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/huydz24"));
                    startActivity(intent);
                }
            } catch (Exception e) {
                // Xử lý lỗi
                Toast.makeText(MainActivity.this, "Không thể mở Facebook", Toast.LENGTH_SHORT).show();
            }
        }

        // Phương thức mở Zalo
        private void openZalo() {
            try {
                Intent intent = getPackageManager().getLaunchIntentForPackage("https://zalo.me/0364356053");
                if (intent != null) {
                    // Ứng dụng Zalo đã được cài đặt, mở nó
                    startActivity(intent);
                } else {
                    // Ứng dụng Zalo chưa được cài đặt, mở trang web
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://zalo.me/0364356053"));
                    startActivity(intent);
                }
            } catch (Exception e) {
                // Xử lý lỗi
                Toast.makeText(MainActivity.this, "Không thể mở Zalo", Toast.LENGTH_SHORT).show();
            }
        }

        // Phương thức gọi điện
        private void makePhoneCall() {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:0364356053"));  // Thêm tiền tố "tel:" trước số điện thoại
            startActivity(callIntent);
        }

        // Phương thức mở trợ lý AI
        private void openAiAssistant() {
            // Ở đây bạn có thể thêm mã để mở trợ lý AI của bạn
            // Ví dụ: mở một Activity mới hoặc hiển thị dialog
            Toast.makeText(MainActivity.this, "Đang mở trợ lý AI...", Toast.LENGTH_SHORT).show();
            Intent chatAi = new Intent(MainActivity.this, AiChatActivity.class);
            startActivity(chatAi);
        }
    }