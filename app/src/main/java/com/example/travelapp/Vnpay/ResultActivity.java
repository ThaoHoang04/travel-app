package com.example.travelapp.Vnpay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelapp.Activity.Detailactivity;
import com.example.travelapp.Activity.MainActivity;
import com.example.travelapp.Activity.TicketActivity;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "VNPAY_RESULT";
    private ItemDomain object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView txtResult = findViewById(R.id.txtResult);
        TextView txtAmount = findViewById(R.id.txtAmount);
        TextView txttitle = findViewById(R.id.title);
        ImageView imgStatusLogo = findViewById(R.id.icon);
        Button btnBack = findViewById(R.id.btnBack);
        object = (ItemDomain) getIntent().getSerializableExtra("object");

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String amount = intent.getStringExtra("amount");
        String txnRef = intent.getStringExtra("txnRef");
        String responseUrl = intent.getStringExtra("response_url");

        // Log thông tin để debug
        Log.d(TAG, "Payment result: " + (result != null ? result : "null"));
        if (responseUrl != null) {
            Log.d(TAG, "Response URL: " + responseUrl);
        }

        // Xử lý kết quả thanh toán
        String resultText;
        String Amount = "";
        String title = "";
        if (result != null) {
            switch (result) {
                case "payment.success":
                    if (amount == null || txnRef == null) {
                        title ="Thanh toán thành công!";
                        Amount = "";
                        resultText = "Dữ liệu giao dịch không đầy đủ.";
                        Log.w(TAG, "Missing amount or txnRef for successful payment");
                        imgStatusLogo.setImageResource (R.drawable.completed);

                    } else {
                        title ="Thanh toán thành công!";
                        Amount = formatAmount(amount) + " VNĐ";
                        resultText = "Mã giao dịch: " + txnRef;
                        imgStatusLogo.setImageResource (R.drawable.completed);
//                        resultText = "Thanh toán thành công!\n\n" +
//                                "Số tiền: " + formatAmount(amount) + " VNĐ\n" +
//                                "Mã giao dịch: " + txnRef;
                    }
                    break;
                case "payment.cancelled":
                    title ="Thanh toán không thành công!";
                    Amount = "";
                    resultText = "Bạn đã hủy giao dịch";
                    imgStatusLogo.setImageResource(R.drawable.err);
                    break;
                case "payment.error":
                    title ="Giao dịch thất bại!";
                    Amount = "";
                    resultText ="Vui lòng thử lại sau.";
                    imgStatusLogo.setImageResource(R.drawable.err);
                    break;
                default:
                    title ="Giao dịch thất bại!";
                    Amount = "";
                    resultText ="Kết quả không xác định";
                    imgStatusLogo.setImageResource(R.drawable.err);
                    Log.w(TAG, "Unknown result code: " + result);
                    break;
            }
        } else {
            title ="Giao dịch thất bại!";
            Amount = "";
            resultText = "Không nhận được kết quả thanh toán.";
            imgStatusLogo.setImageResource(R.drawable.err);
            Log.e(TAG, "Result is null");
            Toast.makeText(this, "Không nhận được kết quả từ VNPay", Toast.LENGTH_SHORT).show();
        }
        txttitle.setText(title);
        txtAmount.setText(Amount);
        txtResult.setText(resultText);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> {
            Intent intent1 = new Intent(ResultActivity.this, TicketActivity.class);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent1.putExtra("object", object);

            startActivity(intent1);
            finish();
        });
    }

    // Định dạng số tiền
    private String formatAmount(String amountStr) {
        if (amountStr == null) {
            return "N/A";
        }
        try {
            long amount = Long.parseLong(amountStr);
            return String.format(Locale.getDefault(), "%,d", amount).replace(',', '.');
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid amount format: " + amountStr, e);
            return amountStr;
        }
    }

    // Xử lý nút back của thiết bị
    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(mainIntent);
        finish();
    }
}