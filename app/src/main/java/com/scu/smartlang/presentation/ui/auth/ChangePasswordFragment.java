package com.scu.smartlang.presentation.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.PasswordResetViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangePasswordFragment extends Fragment {

    private PasswordResetViewModel viewModel;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button changePasswordButton;
    //private ImageView backButton;
    private ProgressBar progressBar;
    private EditText currentPasswordEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PasswordResetViewModel.class);
        initViews(view);
        setupObservers();
        setupListeners();
    }

    private void initViews(View view) {
        currentPasswordEditText = view.findViewById(R.id.et_current_password);
        newPasswordEditText = view.findViewById(R.id.et_new_password);
        confirmPasswordEditText = view.findViewById(R.id.et_confirm_password);
        changePasswordButton = view.findViewById(R.id.btn_change_password);
        // backButton = view.findViewById(R.id.btn_back);
        progressBar = view.findViewById(R.id.pb_loading);
    }

    private void setupObservers() {
        // Loading durumu
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            changePasswordButton.setEnabled(!isLoading);
        });

        // Başarı durumu - şifre değiştirildi
        viewModel.successMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                // Settings veya Profile ekranına geri dön
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        // Hata durumu
        viewModel.errorMessage.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        changePasswordButton.setOnClickListener(v -> {
            // Her 3 EditText'ten şifreleri al
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Validasyonları kontrol et

            //Mevcut şifre validasyonu
            if (currentPassword.isEmpty()) {
                currentPasswordEditText.setError("Mevcut şifreniz gerekli");
                currentPasswordEditText.requestFocus();
                return;
            }

            // Yeni şifre boş mu?
            if (newPassword.isEmpty()) {
                newPasswordEditText.setError("Yeni şifre gerekli");
                newPasswordEditText.requestFocus();
                return;
            }

            // Şifre en az 6 karakter mi?
            if (newPassword.length() < 6) {
                newPasswordEditText.setError("Şifre en az 6 karakter olmalı");
                newPasswordEditText.requestFocus();
                return;
            }

            // Onay şifresi boş mu?
            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.setError("Şifre onayı gerekli");
                confirmPasswordEditText.requestFocus();
                return;
            }

            // Şifreler eşleşiyor mu?
            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordEditText.setError("Şifreler eşleşmiyor");
                confirmPasswordEditText.requestFocus();
                return;
            }

            // Tüm validasyonlar geçildi - ViewModel'e yeni şifreyi gönder
            viewModel.updatePasswordWithReauthentication(currentPassword, newPassword);
        });

//        backButton.setOnClickListener(v ->
//                Navigation.findNavController(v).navigateUp()
//        );
    }
}