package com.scu.smartlang.presentation.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";
    private UserViewModel userViewModel;
    private NavController navController;
    private EditText etEmail, etPassword;
    private Button btnSignIn, btnGoToSignUp;
    private ProgressBar progressBar;
    private TextView tvInitialLoading;
    private TextView tvError;
    private boolean isManualSignIn = false;
    private TextView forgotPasswordTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // UI Elemanlarının Tanımlanması
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnSignIn = view.findViewById(R.id.btn_sign_in);
        btnGoToSignUp = view.findViewById(R.id.btn_go_to_sign_up);
        progressBar = view.findViewById(R.id.pb_loading);
        tvInitialLoading = view.findViewById(R.id.tv_initial_loading);
        tvError = view.findViewById(R.id.tv_error);
        Button btnResendEmail = view.findViewById(R.id.btn_resend_email);
        forgotPasswordTextView = view.findViewById(R.id.tv_forgot_password);

        // --- TEMİZLEME DÜZELTMESİ ---
        // (Test kodları silindi. Burası normal akış.)

        Log.d(TAG, "Zorunlu Temizleme: SignInFragment yüklendi, AuthResult durumu sıfırlanıyor.");
        userViewModel.clearAuthResultState();

        // Başlangıçta Formu Gizle, Oturum Kontrolü Metnini Göster
        setLoadingState(true, true);

        // OTURUM KONTROLÜ
        userViewModel.fetchUserProfile();

        // Açılışta Oturum Kontrolü Sonucunu Dinle
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult instanceof AuthResultState.Loading && tvInitialLoading.getVisibility() == View.VISIBLE) {
                return;
            }
            if (authResult instanceof AuthResultState.Success) {
                if (isManualSignIn) {
                    return;
                }
                AuthResultState.Success success = (AuthResultState.Success) authResult;
                String welcomeName = success.getUser().getUserName();
                setLoadingState(false, false);
                Toast.makeText(getContext(), "Hoş geldiniz! Otomatik giriş yapıldı. Kullanıcı: " + welcomeName, Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.navigation_home, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.signInFragment, true)
                        .build());
            } else if (authResult instanceof AuthResultState.EmailNotVerified) {
                Log.d(TAG, "Açılış oturum kontrolü: E-posta doğrulanmamış. Form gösteriliyor.");
                setLoadingState(false, false);
                tvError.setText("Hesabınız doğrulanmamış. Lütfen e-postanızı kontrol edin.");
                tvError.setVisibility(View.VISIBLE);
                if (btnResendEmail != null) btnResendEmail.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "Açılış oturum kontrolü: Oturum yok veya geçersiz. Giriş formu gösteriliyor.");
                setLoadingState(false, false);
            }
        });

        // Manuel Giriş/Kayıt Sonuçlarını Dinle
        userViewModel.getAuthResult().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult == null) {
                isManualSignIn = false;
                setLoadingState(false, false);
                tvError.setVisibility(View.GONE);
                if (btnResendEmail != null) btnResendEmail.setVisibility(View.GONE);
                return;
            }

            tvError.setVisibility(View.GONE);

            if (authResult instanceof AuthResultState.Loading) {
                setLoadingState(true, false);
            }
            else if (authResult instanceof AuthResultState.Success) {
                setLoadingState(false, false);
                Toast.makeText(getContext(), "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.navigation_home, null,
                        new NavOptions.Builder()
                                .setPopUpTo(R.id.signInFragment, true)
                                .build());
                isManualSignIn = false;
            }
            else if (authResult instanceof AuthResultState.EmailNotVerified) {
                setLoadingState(false, false);
                tvError.setText("Hesabınız doğrulanmamış. Lütfen e-postanızı kontrol edin.");
                tvError.setVisibility(View.VISIBLE);
                if (btnResendEmail != null) btnResendEmail.setVisibility(View.VISIBLE);
                isManualSignIn = false;
            }
            else if (authResult instanceof AuthResultState.ResendEmailSuccess) {
                setLoadingState(false, false);
                Toast.makeText(getContext(), "Doğrulama e-postası tekrar gönderildi. Lütfen kontrol edin.", Toast.LENGTH_LONG).show();
                tvError.setVisibility(View.GONE);
                if (btnResendEmail != null) btnResendEmail.setVisibility(View.GONE);
                isManualSignIn = false;
            }
            else if (authResult instanceof AuthResultState.Error) {
                setLoadingState(false, false);
                AuthResultState.Error error = (AuthResultState.Error) authResult;
                tvError.setText("Hata: " + error.getMessage());
                tvError.setVisibility(View.VISIBLE);
                isManualSignIn = false;
            }
        });

        // BUTON CLICK LISTENERLAR

        if (btnResendEmail != null) {
            btnResendEmail.setOnClickListener(v -> {
                userViewModel.resendVerificationEmail();
            });
        }

        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen e-posta ve şifrenizi giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            isManualSignIn = true;
            tvError.setVisibility(View.GONE);
            userViewModel.loginUser(email, password);
        });

        btnGoToSignUp.setOnClickListener(v -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });

        if (forgotPasswordTextView != null) {
            forgotPasswordTextView.setOnClickListener(v -> {
                navController.navigate(R.id.action_signInFragment_to_forgotPasswordFragment);
            });
        }
    }

    // UI durumunu ayarlar (Yükleniyor/Form).
    private void setLoadingState(boolean isLoading, boolean isInitialLoading) {
        int formVisibility = (isLoading && isInitialLoading) ? View.GONE : View.VISIBLE;
        int initialLoadingVisibility = isInitialLoading ? View.VISIBLE : View.GONE;
        int progressBarVisibility = isLoading && !isInitialLoading ? View.VISIBLE : View.GONE;

        if (etEmail != null) etEmail.setVisibility(formVisibility);
        if (etPassword != null) etPassword.setVisibility(formVisibility);
        if (btnSignIn != null) btnSignIn.setVisibility(formVisibility);
        if (btnGoToSignUp != null) btnGoToSignUp.setVisibility(formVisibility);

        if (tvInitialLoading != null) tvInitialLoading.setVisibility(initialLoadingVisibility);
        if (progressBar != null) progressBar.setVisibility(progressBarVisibility);

        if (isInitialLoading) {
            if (tvError != null) tvError.setVisibility(View.GONE);
        }
    }
}