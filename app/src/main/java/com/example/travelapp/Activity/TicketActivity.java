package com.example.travelapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.example.travelapp.databinding.ActivityTicketBinding;

public class TicketActivity extends BaseActivity {
    ActivityTicketBinding binding;
    private ItemDomain object;

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
        binding.messBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String phone = object.getTourGuidePhone();
              Intent intent = new Intent(Intent.ACTION_DIAL,Uri.fromParts("tel",phone,null));
            startActivity(intent);
            }
        });

    }

    private void getIntentExtra() {
        object = (ItemDomain) getIntent().getSerializableExtra("object"); 
    }
}