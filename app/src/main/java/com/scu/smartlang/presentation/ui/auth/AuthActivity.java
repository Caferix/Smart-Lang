package com.scu.smartlang.presentation.ui.auth; // Bu paket adı doğru olmalı!

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.scu.smartlang.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_auth.xml dosyasını yükler
        setContentView(R.layout.activity_auth);
    }
}