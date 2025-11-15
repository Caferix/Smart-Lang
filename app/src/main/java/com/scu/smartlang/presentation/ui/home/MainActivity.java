package com.scu.smartlang.presentation.ui.home;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.scu.smartlang.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Not: Oturum kontrolü (FirebaseAuth.getCurrentUser() != null) ve AuthActivity'ye
        // yönlendirme burada yapılmalıdır. Ancak şimdilik BottomNav kurulumuna odaklanalım.

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. BottomNavigationView'i bul
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // 2. NavController'ı al (activity_main.xml içindeki Fragment adını kullanır)
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // 3. BottomNavigationView'i NavController ile bağla
        NavigationUI.setupWithNavController(navView, navController);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            WindowInsetsCompat windowInsets = ViewCompat.getRootWindowInsets(v);
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}