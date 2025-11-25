package com.scu.smartlang.presentation.ui.settings;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.scu.smartlang.R;
import com.scu.smartlang.notifications.AlarmScheduler;
import com.scu.smartlang.presentation.viewmodel.AuthViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.Locale;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private AuthViewModel authViewModel;
    private MaterialButton btnEditProfile, btnChangePassword, btnSignOut;
    private SwitchMaterial switchDarkMode, switchNotifications;
    private LinearLayout layoutStartTime, layoutEndTime;
    private TextView tvStartTime, tvEndTime;

    private static final String PREFS_NAME = "SmartLangPrefs";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_START_TIME_HOUR = "start_time_hour";
    private static final String KEY_START_TIME_MINUTE = "start_time_minute";
    private static final String KEY_END_TIME_HOUR = "end_time_hour";
    private static final String KEY_END_TIME_MINUTE = "end_time_minute";

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // İzin isteme launcher'ını hazırla
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // İzin verildi - alarmı kur
                        AlarmScheduler.scheduleNext(requireContext());
                        Toast.makeText(getContext(), "Bildirimler Açıldı", Toast.LENGTH_SHORT).show();
                    } else {
                        // İzin reddedildi
                        switchNotifications.setChecked(false);
                        Toast.makeText(getContext(), "Bildirim izni gerekli", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        NavController navController = NavHostFragment.findNavController(this);

        // View Bağlantıları
        btnSignOut = view.findViewById(R.id.btn_sign_out);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotifications = view.findViewById(R.id.switch_notifications);

        layoutStartTime = view.findViewById(R.id.layout_start_time);
        layoutEndTime = view.findViewById(R.id.layout_end_time);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);

        // Kayıtlı Ayarları Yükle
        boolean savedIsDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        switchDarkMode.setChecked(savedIsDarkMode);

        boolean savedIsNotifications = prefs.getBoolean(KEY_NOTIFICATIONS, true);
        switchNotifications.setChecked(savedIsNotifications);
        updateTimePickersState(savedIsNotifications);

        loadTimeSettings(prefs);

        // Listener'ları Kur
        setupListeners(prefs, navController);
    }

    private void loadTimeSettings(SharedPreferences prefs) {
        int startH = prefs.getInt(KEY_START_TIME_HOUR, 9);
        int startM = prefs.getInt(KEY_START_TIME_MINUTE, 0);
        int endH = prefs.getInt(KEY_END_TIME_HOUR, 20);
        int endM = prefs.getInt(KEY_END_TIME_MINUTE, 0);

        tvStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", startH, startM));
        tvEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", endH, endM));
    }

    private void updateTimePickersState(boolean isEnabled) {
        layoutStartTime.setEnabled(isEnabled);
        layoutEndTime.setEnabled(isEnabled);
        layoutStartTime.setAlpha(isEnabled ? 1.0f : 0.5f);
        layoutEndTime.setAlpha(isEnabled ? 1.0f : 0.5f);
    }

    private void setupListeners(SharedPreferences prefs, NavController navController) {
        // Koyu Tema
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            int newMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(newMode);
        });

        // Bildirimler
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply();
            updateTimePickersState(isChecked);

            if (isChecked) {
                // Android 13+ için izin kontrolü
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {
                        // İzin iste
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        return; // Launcher'dan sonuç gelince devam edecek
                    }
                }
                // İzin var veya Android 12 ve altı
                AlarmScheduler.scheduleNext(requireContext());
                Toast.makeText(getContext(), "Bildirimler Açıldı", Toast.LENGTH_SHORT).show();
            } else {
                AlarmScheduler.cancel(requireContext());
                Toast.makeText(getContext(), "Bildirimler Kapatıldı", Toast.LENGTH_SHORT).show();
            }
        });

        // Saat Seçiciler
        layoutStartTime.setOnClickListener(v -> showTimePicker(true, prefs));
        layoutEndTime.setOnClickListener(v -> showTimePicker(false, prefs));

        // Navigasyonlar
        btnEditProfile.setOnClickListener(v -> navController.navigate(R.id.action_navigation_settings_to_editProfileFragment));
        btnChangePassword.setOnClickListener(v -> navController.navigate(R.id.action_navigation_settings_to_changePasswordFragment));

        // Çıkış Yap
        btnSignOut.setOnClickListener(v -> {
            authViewModel.signOut();
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build();
            navController.navigate(R.id.signInFragment, null, navOptions);
        });
    }

    private void showTimePicker(boolean isStartTime, SharedPreferences prefs) {
        String timeString = isStartTime ? tvStartTime.getText().toString() : tvEndTime.getText().toString();
        String[] parts = timeString.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        TimePickerDialog picker = new TimePickerDialog(requireContext(), (view, h, m) -> {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", h, m);
            SharedPreferences.Editor editor = prefs.edit();

            if (isStartTime) {
                tvStartTime.setText(formattedTime);
                editor.putInt(KEY_START_TIME_HOUR, h);
                editor.putInt(KEY_START_TIME_MINUTE, m);
            } else {
                tvEndTime.setText(formattedTime);
                editor.putInt(KEY_END_TIME_HOUR, h);
                editor.putInt(KEY_END_TIME_MINUTE, m);
            }
            editor.apply();

            // 🆕 Saatler değiştiğinde alarmı yeniden kur
            AlarmScheduler.scheduleNext(requireContext());
        }, hour, minute, true);

        picker.show();
    }
}