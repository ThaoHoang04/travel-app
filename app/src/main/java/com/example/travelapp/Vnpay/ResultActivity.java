package com.example.travelapp.Vnpay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelapp.Activity.Detailactivity;
import com.example.travelapp.Activity.MainActivity;
import com.example.travelapp.Domain.Order;
import com.example.travelapp.Activity.TicketActivity;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "VNPAY_RESULT";
    private ItemDomain object;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
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
        String orderId;
        int itemId;
       itemId = object.getItemsId();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

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
                        database = FirebaseDatabase.getInstance();
                        databaseReference = database.getReference("Order");
                        generateNewOrderIdAndSave(username, txnRef, object.getItemsId(), amount);
//
//                        String name = username;
//                        String total = formatAmount(amount);
//                        itemId = object.getItemsId();
//                        String paymentID = txnRef;
//                        Date date = new Date();
//                        // Định dạng hiển thị (ví dụ: 13/04/2025 15:23:08)
//                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                        String formattedDate = sdf.format(date);
//                        orderId = "#ORD-" + orderidnumber; // set kieu #ORD-7246
//                        orderidnumber++;
//                        int key = 0;
//                        key++;
//                        String processed = "processing";
//                        Order order = new Order(orderId, name, paymentID, itemId,total,formattedDate,processed);
//                        databaseReference.child(String.valueOf(key)).setValue(order);

                        btnBack.setOnClickListener(v -> {
                            Intent intent1 = new Intent(ResultActivity.this, TicketActivity.class);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent1.putExtra("object", object);
                          startActivity(intent1);
                            finish();
                        });
                    }
                    break;
                case "payment.cancelled":
                    title ="Thanh toán không thành công!";
                    Amount = "";
                    resultText = "Bạn đã hủy giao dịch";
                    imgStatusLogo.setImageResource(R.drawable.err);
                    btnBack.setText("Quay trở lại màn hình chi tiết");
                    btnBack.setOnClickListener(v -> {
                        Intent intent1 = new Intent(ResultActivity.this, Detailactivity.class);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent1.putExtra("object", object);

                        startActivity(intent1);
                        finish();
                    });
                    break;
                case "payment.error":
                    title ="Giao dịch thất bại!";
                    Amount = "";
                    resultText ="Vui lòng thử lại sau.";
                    imgStatusLogo.setImageResource(R.drawable.err);
                    btnBack.setText("Quay trở lại màn hình chi tiết");
                    btnBack.setOnClickListener(v -> {
                        Intent intent1 = new Intent(ResultActivity.this, Detailactivity.class);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent1.putExtra("object", object);

                        startActivity(intent1);
                        finish();
                    });
                    break;
                default:
                    title ="Giao dịch thất bại!";
                    Amount = "";
                    resultText ="Kết quả không xác định";
                    imgStatusLogo.setImageResource(R.drawable.err);
                    Log.w(TAG, "Unknown result code: " + result);
                    btnBack.setText("Quay trở lại màn hình chi tiết");
                    btnBack.setOnClickListener(v -> {
                        Intent intent1 = new Intent(ResultActivity.this, Detailactivity.class);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent1.putExtra("object", object);

                        startActivity(intent1);
                        finish();
                    });
                    break;
            }
        } else {
            title ="Giao dịch thất bại!";
            Amount = "";
            resultText = "Không nhận được kết quả thanh toán.";
            imgStatusLogo.setImageResource(R.drawable.err);
            Log.e(TAG, "Result is null");
            Toast.makeText(this, "Không nhận được kết quả từ VNPay", Toast.LENGTH_SHORT).show();
            btnBack.setText("Quay trở lại màn hình chi tiết");
            btnBack.setOnClickListener(v -> {
                Intent intent1 = new Intent(ResultActivity.this, Detailactivity.class);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.putExtra("object", object);

                startActivity(intent1);
                finish();
            });
        }
        txttitle.setText(title);
        txtAmount.setText(Amount);
        txtResult.setText(resultText);

        // Xử lý nút xem chi tiết

    }

    private void generateNewOrderIdAndSave(String username, String txnRef, int itemsId, String amount) {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int maxOrderId = 1000;
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null && order.getOrderId() != null) {
                        String orderIdStr = order.getOrderId().replace("#ORD-", "");
                        try {
                            int id = Integer.parseInt(orderIdStr);
                            if (id > maxOrderId) {
                                maxOrderId = id;
                            }
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Invalid order ID format: " + orderIdStr, e);
                        }
                    }
                }
                maxOrderId++;
                String newOrderId = "#ORD-" + maxOrderId;
                Log.d(TAG, "Generated new order ID: " + newOrderId);

                saveOrderToFirebase(newOrderId, username, txnRef, itemsId, amount);
            } else {
                Log.e(TAG, "Failed to read Orders from Firebase", task.getException());
            }
        });
    }

    private void saveOrderToFirebase(String orderId, String username, String txnRef, int itemId, String amount) {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int key = 1; // mặc định nếu chưa có Order nào thì key = 1

                if (task.getResult() != null) {
                    key = (int) task.getResult().getChildrenCount() + 1;
                    // Đếm tổng số node Order hiện có rồi +1
                }

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String formattedDate = sdf.format(date);

                String total = formatAmount(amount);
                String status = "processing";

                Order order = new Order(orderId, username, txnRef, itemId, total, formattedDate, status);

                databaseReference.child(String.valueOf(key)).setValue(order)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Order saved successfully with key: " + orderId))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to save Order", e));
            } else {
                Log.e(TAG, "Failed to read Orders from Firebase", task.getException());
            }
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
private  void handleDatabase(){

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