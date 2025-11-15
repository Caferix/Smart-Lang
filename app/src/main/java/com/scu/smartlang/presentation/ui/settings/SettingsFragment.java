package com.scu.smartlang.presentation.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences; // Yeni Import
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate; // Yeni Import
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.ui.auth.AuthActivity;
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

    // SharedPreferences için sabitler
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

        // View Bağlantıları
        btnSignOut = view.findViewById(R.id.btn_sign_out);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotifications = view.findViewById(R.id.switch_notifications);

        // --- 1. Siyah Tema Başlangıç Durumu ---
        // Uygulamanın şu anki gece modunu kontrol et
        int defaultMode = AppCompatDelegate.getDefaultNightMode();
        boolean isDarkModeEnabled = defaultMode == AppCompatDelegate.MODE_NIGHT_YES ||
                defaultMode == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY ||
                defaultMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

        // Eğer SharedPreferences'ta kayıtlı bir değer varsa onu kullan, yoksa sistem varsayılanını kullan.
        boolean savedState = prefs.getBoolean(KEY_DARK_MODE, isDarkModeEnabled);
        switchDarkMode.setChecked(savedState);

        // Listener'ları Kur
        setupListeners(prefs);
    }

    private void setupListeners(SharedPreferences prefs) {
        // --- Koyu Tema Anahtarı ---
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int newMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;

            // 1. Temayı Uygula
            AppCompatDelegate.setDefaultNightMode(newMode);

            // 2. Tercihi Kaydet
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();

            String status = isChecked ? "Koyu Tema Açık" : "Açık Tema Açık";
            Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
        });

        // --- Diğer Listener'lar ---

        // Profil Düzenle
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Profil Düzenleme Fragment'ına Navigasyon Yapılacak.", Toast.LENGTH_SHORT).show();
        });

        // Şifre Değiştir
        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Şifre Değiştirme Akışı Başlatılıyor.", Toast.LENGTH_SHORT).show();
        });

        // Bildirim Anahtarı (Placeholder)
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Bildirimler AÇIK" : "Bildirimler KAPALI";
            Toast.makeText(getContext(), status + " (Placeholder)", Toast.LENGTH_SHORT).show();
        });

        // Çıkış Yap (Çalışan akış)
        btnSignOut.setOnClickListener(v -> {
            userViewModel.signOut();

            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish();
            }
            Toast.makeText(getContext(), "Başarıyla çıkış yapıldı.", Toast.LENGTH_SHORT).show();
        });
    }
}