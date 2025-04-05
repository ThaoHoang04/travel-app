package com.example.travelapp.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.example.travelapp.databinding.ActivityTicketBinding;

import java.io.OutputStream;

public class TicketActivity extends BaseActivity {
    ActivityTicketBinding binding;
    private ItemDomain object;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =ActivityTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        setvariable();

    }

    private void setvariable() {
        Glide.with(TicketActivity.this)
                .load(object.getPic())
                .into(binding.pic);

        Glide.with(TicketActivity.this)
                .load(object.getTourGuidePic())
                .into(binding.profile);
        binding.backBtn.setOnClickListener(v-> finish());
        binding.durationTxt.setText(object.getDuration());
        binding.titleTxt.setText(object.getTitle());
        binding.tourTxt.setText(object.getDateTour());
        binding.timeTxt.setText(object.getTimeTour());
        binding.TourguideTxt.setText(object.getTourGuideName());
        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms" + object.getTourGuidePhone()));
                intent.putExtra("sms_body", "type your message");
                startActivity(intent);

            }
        });
        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy số điện thoại từ đối tượng
                String phone = object.getTourGuidePhone();

                // Tạo Intent để gọi điện thoại
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        binding.messBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy số điện thoại từ đối tượng
                String phone = object.getTourGuidePhone();

                // Tạo Intent để gửi SMS
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
                intent.putExtra("sms_body", "Type your message here"); // Đặt nội dung mặc định cho tin nhắn
                startActivity(intent);
            }
        });

    }

    private void getIntentExtra() {
        object = (ItemDomain) getIntent().getSerializableExtra("object"); 
    }
    public void takeScreenshotAndSave() {
        try {
            // 1. Lấy LinearLayout theo ID
            LinearLayout ticketLayout = findViewById(R.id.tickket);

            // 2. Chụp ảnh phần LinearLayout
            ticketLayout.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(ticketLayout.getDrawingCache());
            ticketLayout.setDrawingCacheEnabled(false);

            // 3. Lưu ảnh vào MediaStore
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "screenshot_" + System.currentTimeMillis() + ".png");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Screenshots");

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (imageUri != null) {
                uri = imageUri;
                OutputStream outputStream = resolver.openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                if (outputStream != null) outputStream.close();
                // Thông báo ảnh đã lưu
                Toast.makeText(this, "Ảnh đã được lưu vào điện thoại!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save screenshot", Toast.LENGTH_SHORT).show();
        }
    }

}