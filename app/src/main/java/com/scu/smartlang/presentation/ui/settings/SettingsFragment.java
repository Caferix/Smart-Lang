package com.scu.smartlang.presentation.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.UserViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private UserViewModel userViewModel;
    private MaterialButton btnEditProfile;
    private MaterialButton btnChangePassword;
    private MaterialButton btnSignOut;
    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchNotifications;

    private static final String PREFS_NAME = "SmartLangPrefs";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);

        // NavController başlatıldı
        NavController navController = NavHostFragment.findNavController(this);

        // View Bağlantıları
        btnSignOut = view.findViewById(R.id.btn_sign_out);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotifications = view.findViewById(R.id.switch_notifications);

        // Siyah Tema Başlangıç Durumu
        int defaultMode = AppCompatDelegate.getDefaultNightMode();
        boolean isDarkModeEnabled = defaultMode == AppCompatDelegate.MODE_NIGHT_YES ||
                defaultMode == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY ||
                defaultMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

        boolean savedState = prefs.getBoolean(KEY_DARK_MODE, isDarkModeEnabled);
        switchDarkMode.setChecked(savedState);

        // Listener'ları Kur
        setupListeners(prefs, navController);
    }

    private void setupListeners(SharedPreferences prefs, NavController navController) {
        // --- Koyu Tema Anahtarı ---
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int newMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;

            AppCompatDelegate.setDefaultNightMode(newMode);
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();

            String status = isChecked ? "Koyu Tema Açık" : "Açık Tema Açık";
            Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
        });

        // --- Diğer Listener'lar ---

        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Profil Düzenleme Fragment'ına Navigasyon Yapılacak.", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_settings_to_changePasswordFragment);
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Bildirimler AÇIK" : "Bildirimler KAPALI";
            Toast.makeText(getContext(), status + " (Placeholder)", Toast.LENGTH_SHORT).show();
        });

        // Çıkış Yap
        btnSignOut.setOnClickListener(v -> {
            userViewModel.signOut();

            Toast.makeText(getContext(), "Oturum Kapatıldı. Giriş ekranına yönlendiriliyor.", Toast.LENGTH_SHORT).show();

            // DÜZELTME: Fragment geçişi (Tüm yığını temizleyip Giriş Ekranına git)
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.main_nav_graph, true) // Tüm fragment'ları yığından temizle
                    .build();
            navController.navigate(R.id.signInFragment, null, navOptions);
        });
    }
}