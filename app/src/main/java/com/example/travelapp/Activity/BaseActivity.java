package com.example.travelapp.Activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import android.content.SharedPreferences;
import android.widget.ImageView;
import com.example.travelapp.R;

public class BaseActivity extends AppCompatActivity {
    FirebaseDatabase database;

    protected ImageView fabMain, fabZalo, imgAi;
    protected com.google.android.material.floatingactionbutton.FloatingActionButton fabFacebook, fabCall;
    protected android.view.animation.Animation fabOpen, fabClose;
    protected boolean isFabMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        Window v = getWindow();
        v.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void setupBottomNavigation() {
        com.ismaeldivita.chipnavigation.ChipNavigationBar chipNavigationBar = findViewById(R.id.chipNavigationBar);
        if (chipNavigationBar != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            chipNavigationBar.setOnItemSelectedListener(new com.ismaeldivita.chipnavigation.ChipNavigationBar.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int id) {
                    if (id == R.id.profile) {
                        if (!(BaseActivity.this instanceof ProfileActivity)) {
                            android.content.Intent intent = new android.content.Intent(BaseActivity.this, ProfileActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                    } else if (id == R.id.cart) {
                        if (!(BaseActivity.this instanceof BookMarkActivity)) {
                            android.content.Intent intent = new android.content.Intent(BaseActivity.this, BookMarkActivity.class);
                            startActivity(intent);
                        }
                    } else if (id == R.id.favorites) {
                        if (!(BaseActivity.this instanceof ExplorerActivity)) {
                            android.content.Intent intent = new android.content.Intent(BaseActivity.this, ExplorerActivity.class);
                            startActivity(intent);
                        }
                    } else if (id == R.id.explorer) {
                        if (!(BaseActivity.this instanceof MainActivity)) {
                            android.content.Intent intent = new android.content.Intent(BaseActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });
            
            // Highlight current item
            if (this instanceof MainActivity) {
                chipNavigationBar.setItemSelected(R.id.explorer, true);
            } else if (this instanceof ProfileActivity) {
                chipNavigationBar.setItemSelected(R.id.profile, true);
            } else if (this instanceof BookMarkActivity) {
                chipNavigationBar.setItemSelected(R.id.cart, true);
            } else if (this instanceof ExplorerActivity) {
                chipNavigationBar.setItemSelected(R.id.favorites, true);
            }
        }

        // FAB logic
        fabMain = findViewById(R.id.fab_main);
        if (fabMain != null) {
            fabFacebook = findViewById(R.id.fab_facebook);
            fabZalo = findViewById(R.id.fab_zalo);
            fabCall = findViewById(R.id.fab_call);
            imgAi = findViewById(R.id.img_ai);

            try {
                fabOpen = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fab_open);
                fabClose = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fab_close);
            } catch (Exception e) {}

            fabMain.setOnClickListener(view -> toggleFabMenu());
            fabFacebook.setOnClickListener(view -> {
                openFacebook();
                toggleFabMenu();
            });
            fabZalo.setOnClickListener(view -> {
                openZalo();
                toggleFabMenu();
            });
            fabCall.setOnClickListener(view -> {
                makePhoneCall();
                toggleFabMenu();
            });
            imgAi.setOnClickListener(view -> {
                openAiAssistant();
                toggleFabMenu();
            });
        }
    }

    private void toggleFabMenu() {
        if (fabOpen == null || fabClose == null) return;
        if (isFabMenuOpen) {
            fabFacebook.startAnimation(fabClose);
            fabZalo.startAnimation(fabClose);
            fabCall.startAnimation(fabClose);
            imgAi.startAnimation(fabClose);

            fabFacebook.setClickable(false);
            fabZalo.setClickable(false);
            fabCall.setClickable(false);
            imgAi.setClickable(false);

            fabFacebook.setVisibility(android.view.View.INVISIBLE);
            fabZalo.setVisibility(android.view.View.INVISIBLE);
            fabCall.setVisibility(android.view.View.INVISIBLE);
            imgAi.setVisibility(android.view.View.INVISIBLE);

            isFabMenuOpen = false;
        } else {
            fabFacebook.startAnimation(fabOpen);
            fabZalo.startAnimation(fabOpen);
            fabCall.startAnimation(fabOpen);
            imgAi.startAnimation(fabOpen);

            fabFacebook.setClickable(true);
            fabZalo.setClickable(true);
            fabCall.setClickable(true);
            imgAi.setClickable(true);

            fabFacebook.setVisibility(android.view.View.VISIBLE);
            fabZalo.setVisibility(android.view.View.VISIBLE);
            fabCall.setVisibility(android.view.View.VISIBLE);
            imgAi.setVisibility(android.view.View.VISIBLE);

            isFabMenuOpen = true;
        }
    }

    private void openFacebook() {
        try {
            android.content.Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
            if (intent != null) {
                startActivity(intent);
            } else {
                intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.facebook.com/nhtrieu04"));
                startActivity(intent);
            }
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Không thể mở Facebook", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void openZalo() {
        try {
            android.content.Intent intent = getPackageManager().getLaunchIntentForPackage("com.zing.zalo");
            if (intent != null) {
                startActivity(intent);
            } else {
                intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://zalo.me/0978983504"));
                startActivity(intent);
            }
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Không thể mở Zalo", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void makePhoneCall() {
        android.content.Intent callIntent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
        callIntent.setData(android.net.Uri.parse("tel:0978983504"));
        startActivity(callIntent);
    }

    private void openAiAssistant() {
        android.widget.Toast.makeText(this, "Đang mở trợ lý AI...", android.widget.Toast.LENGTH_SHORT).show();
        android.content.Intent chatAi = new android.content.Intent(this, AiChatActivity.class);
        startActivity(chatAi);
    }
}