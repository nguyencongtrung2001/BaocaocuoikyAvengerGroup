package com.example.baocaocuoiky.Trung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baocaocuoiky.MainActivity;
import com.example.baocaocuoiky.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText txt_email, txt_username, txt_password, txt_confirm;
    private TextView text_login;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_signup);

        // Khởi tạo FirebaseAuth và FirebaseDatabase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Ánh xạ các thành phần từ giao diện
        txt_email = findViewById(R.id.email);
        txt_username = findViewById(R.id.username);
        txt_password = findViewById(R.id.password);
        txt_confirm = findViewById(R.id.confirm);
        text_login = findViewById(R.id.textlogin);
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this , LoginActivity.class);
                startActivity(intent);
            }
        });

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy giá trị từ các trường
                String email = txt_email.getText().toString().trim();
                String username = txt_username.getText().toString().trim();
                String password = txt_password.getText().toString().trim();
                String confirmPassword = txt_confirm.getText().toString().trim();

                // Kiểm tra các điều kiện đầu vào
                if (email.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !isValidPhoneNumber(email)) {
                    Toast.makeText(SignupActivity.this, "Email hoặc số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Vui lòng nhập tên đăng nhập", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!confirmPassword.equals(password)) {
                    Toast.makeText(SignupActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Đăng ký người dùng
                registerUser(email, username, password);
            }
        });
    }

    private void registerUser(String email, String username, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Gửi email xác minh
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this, "Đăng ký thành công! Vui lòng kiểm tra email xác minh.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(SignupActivity.this, "Không thể gửi email xác minh.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                            // Lưu thông tin người dùng vào Realtime Database
                            String userId = firebaseUser.getUid();
                            HelperUser user = new HelperUser(email, username, password);
                            DatabaseReference userRef = database.getReference("users").child(userId);
                            userRef.setValue(user);

                            // Chuyển đến màn hình chính (HomeActivity)
                            Intent intent = new Intent(SignupActivity.this , HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Xử lý lỗi nếu đăng ký thất bại
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại.";
                            Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Kiểm tra số điện thoại hợp lệ
    private boolean isValidPhoneNumber(String phone) {
        // Kiểm tra số điện thoại có 10 chữ số và bắt đầu bằng số 0
        return phone.matches("^0\\d{9}$");
    }
}
