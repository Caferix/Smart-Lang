package com.scu.smartlang.presentation.ui.home;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.scu.smartlang.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. NavHostController başlatılıyor
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment == null) {
            return;
        }

        navController = navHostFragment.getNavController();
        navView = findViewById(R.id.nav_view);

        // 2. NavGraph'ı yükle - DÜZELTME BURADA: R.id yerine R.navigation kullanılmalı
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.main_nav_graph);

        // 3. OTURUM KONTROLÜ: Başlangıç noktasını dinamik olarak ayarla
        if (firebaseAuth.getCurrentUser() != null) {
            // Oturum açıksa, Ana Sayfa'dan başla
            navGraph.setStartDestination(R.id.navigation_home);
        } else {
            // Oturum kapalıysa, Giriş Ekranı'ndan başla
            navGraph.setStartDestination(R.id.signInFragment);
        }

        // 4. NavController'a güncel NavGraph'ı ata
        navController.setGraph(navGraph);

        // 5. Bottom Navigation'ı NavController ile bağla
        NavigationUI.setupWithNavController(navView, navController);

        // 6. BottomNav Görünürlük Mantığı
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.navigation_home ||
                    destinationId == R.id.navigation_leaderboard ||
                    destinationId == R.id.navigation_settings) {
                navView.setVisibility(View.VISIBLE);
            } else {
                navView.setVisibility(View.GONE);
            }
        });

        // 7. Insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            WindowInsetsCompat windowInsets = ViewCompat.getRootWindowInsets(v);
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}