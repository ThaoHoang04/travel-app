package com.example.travelapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupEmail, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                if (name.isEmpty()) {
                    signupName.setError("Tên không được để trống");
                    signupName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    signupEmail.setError("Email không được để trống");
                    signupEmail.requestFocus();
                    return;
                }
                else if (!isValidEmail(email)) {
                        signupEmail.setError("Email không hợp lệ");
                        signupEmail.requestFocus();
                        return;

                }
                if (username.isEmpty()) {
                    signupUsername.setError("Tên đăng nhập không được để trống");
                    signupUsername.requestFocus();
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    signupPassword.setError("Password không được để trống và trên 6 ký tự");
                    signupPassword.requestFocus();
                    return;
                }
                reference.child(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // Username đã tồn tại
                            signupUsername.setError("Tên người dùng đã tồn tại");
                            signupUsername.requestFocus();
                        } else {
                            // Username chưa tồn tại, tiến hành tạo tài khoản
//                            String cartId = UUID.randomUUID().toString();
                            String cartId = username;
                            HelperClass helperClass = new HelperClass(name, email, username, password, cartId);
                            reference.child(username).setValue(helperClass);
                            Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Lỗi kết nối cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });

    }
    public boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    }