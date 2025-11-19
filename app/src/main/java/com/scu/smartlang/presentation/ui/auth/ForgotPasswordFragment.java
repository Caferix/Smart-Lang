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
public class ForgotPasswordFragment extends Fragment {

    // ViewModel
    private PasswordResetViewModel viewModel;

    // UI bileşenleri
    private EditText emailEditText;
    private Button sendResetEmailButton;
    //private ImageView backButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Layout'u inflate et ve return et
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel'i initialize et - Hilt otomatik inject eder
        viewModel = new ViewModelProvider(this).get(PasswordResetViewModel.class);

        // UI bileşenlerini findViewById ile bul
        initViews(view);

        // ViewModel'deki LiveData'ları dinle
        setupObservers();

        // Kullanıcı etkileşimlerini dinle (button click vs)
        setupListeners();
    }

    private void initViews(View view) {
        emailEditText = view.findViewById(R.id.et_email);
        sendResetEmailButton = view.findViewById(R.id.btn_send_reset_email);
        //backButton = view.findViewById(R.id.btn_back);
        progressBar = view.findViewById(R.id.pb_loading);
    }

    private void setupObservers() {
        // Loading durumunu izle
        // true ise -> ProgressBar göster, button'u disable et
        // false ise -> ProgressBar gizle, button'u enable et
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                sendResetEmailButton.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                sendResetEmailButton.setEnabled(true);
            }
        });

        // Başarı mesajını izle
        // Email başarıyla gönderildiyse kullanıcıya bildir ve geri dön
        viewModel.successMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                // Login ekranına geri dön
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        // Hata mesajını izle
        // Hata varsa kullanıcıya Toast ile göster
        viewModel.errorMessage.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        // "Şifre Sıfırlama Maili Gönder" butonuna tıklanınca
        sendResetEmailButton.setOnClickListener(v -> {
            // EditText'ten email'i al ve boşlukları temizle
            String email = emailEditText.getText().toString().trim();

            // Email boş mu kontrol et
            if (email.isEmpty()) {
                emailEditText.setError("Email adresi gerekli");
                emailEditText.requestFocus();
                return;
            }

            // Email formatı doğru mu basit kontrol
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Geçerli bir email adresi girin");
                emailEditText.requestFocus();
                return;
            }

            // ViewModel'e email'i gönder - Firebase işlemi başlasın
            viewModel.sendPasswordResetEmail(email);
        });

        // "Geri" butonuna tıklanınca login ekranına dön
//        backButton.setOnClickListener(v ->
//                Navigation.findNavController(v).navigateUp()
//        );
    }
}