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

        // === KRİTİK DÜZELTME BAŞLANGICI ===
        // Bu blok sadece uygulama "soğuk başlatma" ile açıldığında çalışır.
        // Tema değişimi gibi Activity'nin yeniden oluşturulduğu durumlarda (savedInstanceState != null)
        // bu blok atlanır ve Navigation Component kullanıcının kaldığı son sayfayı (Ayarlar) hatırlar.
        if (savedInstanceState == null) {
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.main_nav_graph);

            if (firebaseAuth.getCurrentUser() != null) {
                // Oturum açıksa Ana Sayfa
                navGraph.setStartDestination(R.id.navigation_home);
            } else {
                // Değilse Giriş Yap
                navGraph.setStartDestination(R.id.signInFragment);
            }

            navController.setGraph(navGraph);
        }
        // === KRİTİK DÜZELTME SONU ===

        // 5. Bottom Navigation'ı NavController ile bağla
        NavigationUI.setupWithNavController(navView, navController);

        // 6. BottomNav Görünürlük Mantığı
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.navigation_home ||
                    destinationId == R.id.navigation_leaderboard ||
                    destinationId == R.id.navigation_profile ||
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