package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ModeManagement extends AppCompatActivity {
    RadioButton enc,sign,encsign;
    Button confirm;
    int modecode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        enc=findViewById(R.id.enc_Button);
        sign=findViewById(R.id.sign_Button);
        encsign=findViewById(R.id.encsign_Button);
        confirm=findViewById(R.id.confirm_button);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (enc.isChecked()) {
                    modecode = 1;

                } else if(sign.isChecked()){
                    modecode = 2 ;
                }else {
                    modecode=0;
                }

            }
        });
    }
}
