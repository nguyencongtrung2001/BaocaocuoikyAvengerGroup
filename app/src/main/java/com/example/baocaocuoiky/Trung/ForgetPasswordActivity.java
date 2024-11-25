package com.example.baocaocuoiky.Trung;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baocaocuoiky.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextView text_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        text_sign = findViewById(R.id.textsign);
        text_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgetPasswordActivity.this , SignupActivity.class );
                startActivity(intent);
            }
        });

    }
}