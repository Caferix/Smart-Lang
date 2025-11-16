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

        // TEMİZLEME DÜZELTMESİ
        // Fragment yüklendiğinde, ViewModel'deki son kayıt sonucunu (EmailNotVerified gibi)
        // hemen ve koşulsuz olarak temizle. Bu, observerın sadece kullanıcının
        // Giriş yap butonuna bastıktan sonraki yeni durumları görmesini sağlar.
        Log.d(TAG, "Zorunlu Temizleme: SignInFragment yüklendi, AuthResult durumu sıfırlanıyor.");
        userViewModel.clearAuthResultState();
        // TEMİZLEME SONU


        // Başlangıçta Formu Gizle, Oturum Kontrolü Metnini Göster
        setLoadingState(true, true);

        // OTURUM KONTROLÜ
        userViewModel.fetchUserProfile();

        // Açılışta Oturum Kontrolü Sonucunu Dinle (Sadece oturum var mı/doğrulanmış mı kontrolü)
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), authResult -> {
            // If initial loading message is visible, ignore Loading state as before
            if (authResult instanceof AuthResultState.Loading && tvInitialLoading.getVisibility() == View.VISIBLE) {
                return;
            }
            if (authResult instanceof AuthResultState.Success) {
                // If a manual signin was requested, suppress the automatic-login toast/navigation
                if (isManualSignIn) {
                    // Let the manual flow (getAuthResult()) handle toasts/navigation
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
                // DURUM: Oturum vardı ama E-posta DOĞRULANMAMIŞ. ViewModel oturumu kapattı.
                Log.d(TAG, "Açılış oturum kontrolü: E-posta doğrulanmamış. Form gösteriliyor.");
                setLoadingState(false, false); // Formu görünür yap
                tvError.setText("Hesabınız doğrulanmamış. Lütfen e-postanızı kontrol edin.");
                tvError.setVisibility(View.VISIBLE);
                if (btnResendEmail != null) btnResendEmail.setVisibility(View.VISIBLE);
            } else {
                // DURUM: Oturum yok (Error, SignedOut veya genel hata). Formu göster.
                Log.d(TAG, "Açılış oturum kontrolü: Oturum yok veya geçersiz. Giriş formu gösteriliyor.");
                setLoadingState(false, false); // Formu görünür yap
            }
        });

        // Manuel Giriş/Kayıt Sonuçlarını Dinle (loginUser()'dan gelen)
        userViewModel.getAuthResult().observe(getViewLifecycleOwner(), authResult -> {
            // Temizleme sinyalini (null) yakala. Bu, manuel clearAuthResultState() çağrımızdan sonraki durumdur.
            if (authResult == null) {
                isManualSignIn = false;
                // UI'ı temiz ve sıfırlanmış halde tut.
                setLoadingState(false, false);
                tvError.setVisibility(View.GONE);
                if (btnResendEmail != null) btnResendEmail.setVisibility(View.GONE);
                return;
            }

            // Yeni bir işlem sonucu geldi, hata metnini sıfırla.
            tvError.setVisibility(View.GONE);

            // Loading
            if (authResult instanceof AuthResultState.Loading) {
                setLoadingState(true, false);
            }
            // Başarılı Giriş (E-posta zaten doğrulanmış varsayılır)
            else if (authResult instanceof AuthResultState.Success) {
                setLoadingState(false, false);
                Toast.makeText(getContext(), "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                // NAVİGASYON: Ana Sayfaya git ve geri yığını temizle.
                navController.navigate(R.id.navigation_home, null,
                        new NavOptions.Builder()
                                .setPopUpTo(R.id.signInFragment, true)
                                .build());
                isManualSignIn = false;
            }
            // E-posta Doğrulaması Gerekli (YALNIZCA KULLANICI GİRİŞ YAPMAYI DENEYİNCE GÖSTERİLMELİ)
            else if (authResult instanceof AuthResultState.EmailNotVerified) {
                setLoadingState(false, false);
                tvError.setText("Hesabınız doğrulanmamış. Lütfen e-postanızı kontrol edin.");
                tvError.setVisibility(View.VISIBLE);
                if (btnResendEmail != null) btnResendEmail.setVisibility(View.VISIBLE);
                isManualSignIn = false;
            }
            // E-posta Tekrar Gönderim Başarısı
            else if (authResult instanceof AuthResultState.ResendEmailSuccess) {
                setLoadingState(false, false);
                Toast.makeText(getContext(), "Doğrulama e-postası tekrar gönderildi. Lütfen kontrol edin.", Toast.LENGTH_LONG).show();
                tvError.setVisibility(View.GONE);
                if (btnResendEmail != null) btnResendEmail.setVisibility(View.GONE);
                isManualSignIn = false;
            }
            // Hata
            else if (authResult instanceof AuthResultState.Error) {
                setLoadingState(false, false);
                AuthResultState.Error error = (AuthResultState.Error) authResult;
                tvError.setText("Hata: " + error.getMessage());
                tvError.setVisibility(View.VISIBLE);
                isManualSignIn = false;
            }
        });

        // BUTON CLICK LISTENERLAR

        // E-posta Tekrar Gönderme
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

            // Giriş denemesi başladığında hata metnini kapat.
            tvError.setVisibility(View.GONE);

            userViewModel.loginUser(email, password);
        });

        btnGoToSignUp.setOnClickListener(v -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });
    }


     // UI durumunu ayarlar (Yükleniyor/Form).
    private void setLoadingState(boolean isLoading, boolean isInitialLoading) {
        // Oturum kontrolü devam ederken form gizli olmalı
        int formVisibility = (isLoading && isInitialLoading) ? View.GONE : View.VISIBLE;

        // Oturum kontrolü bitene kadar merkezi yükleniyor göster (tvInitialLoading)
        int initialLoadingVisibility = isInitialLoading ? View.VISIBLE : View.GONE;

        // Genel progress bar görünürlüğü (hem initial hem de form submission için)
        int progressBarVisibility = isLoading && !isInitialLoading ? View.VISIBLE : View.GONE;

        // Form Bileşenlerini Kontrol Et
        if (etEmail != null) etEmail.setVisibility(formVisibility);
        if (etPassword != null) etPassword.setVisibility(formVisibility);
        if (btnSignIn != null) btnSignIn.setVisibility(formVisibility);
        if (btnGoToSignUp != null) btnGoToSignUp.setVisibility(formVisibility);

        // Açılış Yükleniyor Metni
        if (tvInitialLoading != null) tvInitialLoading.setVisibility(initialLoadingVisibility);

        // Progress Bar'ı göster (sadece manuel yükleme için, initial için değil)
        if (progressBar != null) progressBar.setVisibility(progressBarVisibility);

        // Hata metnini ve tekrar gönder butonunu (görünürse) initial loading bitene kadar gizle
        if (isInitialLoading) {
            if (tvError != null) tvError.setVisibility(View.GONE);
        }
    }
}