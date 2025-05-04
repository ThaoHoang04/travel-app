package com.example.travelapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    TextView profileName, profileEmail, profileUsername, profilePassword;
    TextView titleName, titleUsername;
    Button editProfile, logoutButton,detailTicket;
    String userUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các view
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
//        profilePassword = findViewById(R.id.profilePassword);
        detailTicket = findViewById(R.id.myTicket);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);
        logoutButton = findViewById(R.id.logout);


        // Lấy username từ Intent hoặc FirebaseAuth
        userUsername = getIntent().getStringExtra("username");
        if (userUsername == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userUsername = user.getDisplayName(); // Hoặc lấy từ database nếu có
            }
        }

        // Kiểm tra username
        if (userUsername != null) {
            showUserData(); // Hiển thị thông tin người dùng
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy username!", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện khi nhấn nút đăng xuất
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa dữ liệu SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Xóa toàn bộ dữ liệu trong SharedPreferences
                editor.apply(); // Hoặc editor.commit();

                // Đăng xuất Firebase
                FirebaseAuth.getInstance().signOut();

                // Điều hướng về màn hình đăng nhập và xóa lịch sử Activity
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        // Xử lý sự kiện khi nhấn nút "Sửa thông tin"
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });
        detailTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyTicketActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userUsername != null) {
            showUserData(); // Load lại dữ liệu khi mở ProfileActivity
        }
    }

    // ✅ Hàm lấy dữ liệu từ Firebase và hiển thị lên giao diện
    public void showUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Log.d("ProfileDebug", "Lấy dữ liệu user: " + userUsername);

        reference.child(userUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy dữ liệu từ Firebase
                    String nameFromDB = snapshot.child("name").getValue(String.class);
                    String emailFromDB = snapshot.child("email").getValue(String.class);
                    String passwordFromDB = snapshot.child("password").getValue(String.class);

                    // Hiển thị dữ liệu lên giao diện
                    titleName.setText(nameFromDB);
                    titleUsername.setText(userUsername);
                    profileName.setText(nameFromDB);
                    profileEmail.setText(emailFromDB);
                    profileUsername.setText(userUsername);
//                    profilePassword.setText(passwordFromDB);

                    Log.d("ProfileDebug", "Dữ liệu user " + userUsername + " lấy thành công!");

                } else {
                    Log.e("ProfileDebug", "Không tìm thấy dữ liệu user " + userUsername);
                    Toast.makeText(ProfileActivity.this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileDebug", "Lỗi Firebase: " + error.getMessage());
            }
        });
    }

    // ✅ Hàm chuyển dữ liệu sang màn hình chỉnh sửa thông tin
    public void passUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(userUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy dữ liệu từ Firebase
                    String nameFromDB = snapshot.child("name").getValue(String.class);
                    String emailFromDB = snapshot.child("email").getValue(String.class);
                    String passwordFromDB = snapshot.child("password").getValue(String.class);

                    // Chuyển dữ liệu sang màn hình chỉnh sửa
                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("username", userUsername); // Username là key
                    intent.putExtra("password", passwordFromDB);
                    startActivity(intent);
                } else {
                    Log.e("ProfileDebug", "Không tìm thấy user " + userUsername);
                    Toast.makeText(ProfileActivity.this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileDebug", "Lỗi Firebase: " + error.getMessage());
            }
        });
    }
}
