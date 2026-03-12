package com.example.travelapp.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.Adapter.MessageAdapter;
import com.example.travelapp.Domain.ChatMessage;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.travelapp.R;

public class AiChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private ImageButton buttonSend;

    private DatabaseReference databaseReference;
    private OkHttpClient httpClient;

    // Thay đổi thành API endpoint của bạn (có thể dùng OpenAI, Gemini, hoặc API khác)
//    private static final String AI_API_URL = "http://192.168.0.66:11434/v1/chat/completions";
//    private static final String API_KEY = "ollama"; // Thay bằng API key thực nếu cần
    private static final String AI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyAcKga8T50JQrB8KyT8nxL8T-0oKQ2ywKg";

    // Lưu trữ lịch sử chat như trong code Python
    private List<ChatMessage> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        initViews();
        initFirebase();
        initHttpClient();
        setupRecyclerView();
        setupClickListeners();

        // Khởi tạo lịch sử chat
        chatHistory = new ArrayList<>();

        // Thêm tin nhắn chào mừng
        addMessage("Xin chào! Tôi có thể giúp bạn tìm tour du lịch hoặc trả lời câu hỏi!", false);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
    }

    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance("https://travel-app-437bb-default-rtdb.firebaseio.com")
                .getReference("Item");
    }

    private void initHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);
    }

    private void setupClickListeners() {
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Cho phép gửi tin nhắn bằng Enter
        editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String userMessage = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(userMessage)) {
            return;
        }

        // Kiểm tra lệnh thoát như trong code Python
        if (userMessage.toLowerCase().equals("exit") || userMessage.toLowerCase().equals("quit")) {
            Toast.makeText(this, "Tạm biệt!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thêm tin nhắn của user
        addMessage(userMessage, true);
        editTextMessage.setText("");

        // Kiểm tra xem có phải tìm kiếm tour không (giống logic Python)
        String normalizedInput = normalizeText(userMessage);
        if (containsTourKeywords(normalizedInput)) {
            searchTours(userMessage);
        } else {
            // Gọi AI API (thay thế Ollama local)
            callAIAPI(userMessage);
        }
    }

    private void addMessage(String content, boolean isUser) {
        Message message = new Message(content, isUser);
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    // Hàm normalize_text giống như trong Python
    private String normalizeText(String text) {
        if (text == null) return "";
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    // Kiểm tra từ khóa tour
    private boolean containsTourKeywords(String normalizedText) {
        String[] keywords = {
                "tour", "bien", "vinh", "du lich", "di lich", "lich su", "bao tang", "dao",
                "phu quoc", "ha long", "thai lan", "phu tho", "ha noi", "ha nam", "phat giao",
                "chua", "den", "van hoa", "thien nhien", "dong nam a", "dong huong tich",
                "cay che", "tay nguyen", "dak lak", "ban don", "cam pu chia", "malaysia",
                "times city", "thuy cung", "lich su quan su", "chien tranh", "di san the gioi",
                "du lich tam linh", "cao nguyen", "buon ma thuot", "di tich"
        };
        for (String keyword : keywords) {
            if (normalizedText.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Hàm tìm tour
    private void searchTours(String userInput) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemDomain> matchedTours = new ArrayList<>();
                String normalizedInput = normalizeText(userInput);

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Object value = snapshot.getValue();

                        if (value instanceof java.util.Map) {
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, Object> tourMap = (java.util.Map<String, Object>) value;

                            String title = (String) tourMap.get("title");
                            String description = (String) tourMap.get("description");
                            String address = (String) tourMap.get("address");
                            String duration = (String) tourMap.get("duration");
                            String timeTour = (String) tourMap.get("timeTour");
                            String tourGuideName = (String) tourMap.get("tourGuideName");
                            String tourGuidePhone = (String) tourMap.get("tourGuidePhone");

                            int price = 0;
                            try {
                                price = ((Number) tourMap.get("price")).intValue();
                            } catch (Exception ignored) {}

                            double score = 0;
                            try {
                                Object scoreObj = tourMap.get("score");
                                if (scoreObj instanceof Long) {
                                    score = ((Long) scoreObj).doubleValue();
                                } else if (scoreObj instanceof Double) {
                                    score = (Double) scoreObj;
                                }
                            } catch (Exception ignored) {}

                            if (title != null && description != null) {
                                String normalizedTitle = normalizeText(title);
                                String normalizedDescription = normalizeText(description);

                                if (normalizedTitle.contains(normalizedInput) ||
                                        normalizedDescription.contains(normalizedInput) ||
                                        normalizedInput.contains(normalizedTitle) ||
                                        normalizedInput.contains(normalizedDescription)) {

                                    ItemDomain tour = new ItemDomain(title, description, price, duration, address);
                                    tour.setTimeTour(timeTour);
                                    tour.setTourGuideName(tourGuideName);
                                    tour.setTourGuidePhone(tourGuidePhone);
                                    tour.setScore(score);
                                    tour.setAddress(address);

                                    matchedTours.add(tour);
                                }
                            }
                        }
                    }
                }

                if (!matchedTours.isEmpty()) {
                    displayTours(matchedTours);
                } else {
                    addMessage("Không tìm thấy tour nào phù hợp v ới yêu cầu của bạn.", false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                addMessage("Lỗi khi tìm kiếm tour: " + databaseError.getMessage(), false);
            }
        });
    }
    // Hiển thị tour
    private void displayTours(List<ItemDomain> tours) {
        StringBuilder response = new StringBuilder("📌 Danh sách tour liên quan:\n\n");

        for (ItemDomain tour : tours) {
            response.append("🧳 ").append(tour.getTitle()).append("\n");
            response.append("📍 Địa điểm: ").append(tour.getAddress()).append("\n");
            response.append("🕒 Thời gian: ").append(tour.getTimeTour()).append(", ").append(tour.getDuration()).append("\n");
            response.append("💰 Giá: ").append(tour.getPrice()).append(" VND\n");
            response.append("📞 Hướng dẫn viên: ").append(tour.getTourGuideName())
                    .append(" (").append(tour.getTourGuidePhone()).append(")\n");
            response.append("⭐ Đánh giá: ").append(tour.getScore()).append("\n");

            String desc = tour.getDescription();
            if (desc.length() > 100) {
                desc = desc.substring(0, 100) + "...";
            }
            response.append("📝 Mô tả: ").append(desc).append("\n\n");
        }

        addMessage(response.toString(), false);
    }


    // Gọi AI API (thay thế Ollama local)
    private void callAIAPI(String userMessage) {
        chatHistory.add(new ChatMessage("user", userMessage));

        try {
            // Tạo mảng nội dung dạng parts
            JSONArray contents = new JSONArray();

            // Bạn có thể thêm lịch sử nếu muốn giữ ngữ cảnh
            for (ChatMessage msg : chatHistory) {
                JSONObject part = new JSONObject();
                part.put("text", msg.getContent());

                JSONObject contentItem = new JSONObject();
                contentItem.put("role", msg.getRole());
                contentItem.put("parts", new JSONArray().put(part));

                contents.put(contentItem);
            }

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("contents", contents);

            RequestBody body = RequestBody.create(
                    MediaType.get("application/json; charset=utf-8"),
                    jsonBody.toString()
            );

            Request request = new Request.Builder()
                    .url(AI_API_URL)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> addMessage("Lỗi kết nối Gemini: " + e.getMessage(), false));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);

                            JSONArray candidates = jsonResponse.getJSONArray("candidates");
                            JSONObject first = candidates.getJSONObject(0);
                            JSONObject content = first.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            String aiReply = parts.getJSONObject(0).getString("text");

                            chatHistory.add(new ChatMessage("assistant", aiReply));

                            runOnUiThread(() -> addMessage(aiReply, false));
                        } catch (JSONException e) {
                            runOnUiThread(() -> addMessage("Lỗi xử lý phản hồi Gemini", false));
                        }
                    } else {
                        runOnUiThread(() -> addMessage("Gemini trả về lỗi: " + response.code(), false));
                    }
                }
            });

        } catch (JSONException e) {
            addMessage("Lỗi tạo yêu cầu Gemini", false);
        }
    }
}