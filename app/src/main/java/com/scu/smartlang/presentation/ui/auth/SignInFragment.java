package com.scu.smartlang.presentation.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class SignInFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnSignIn;
    private MaterialButton btnGoToSignUp;
    private NavController navController;

    // Anlaşılır UI Bileşen İsimleri:
    private TextView errorTextView;
    private MaterialButton resendEmailButton;
    private ProgressBar loadingProgressBar;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);


        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnSignIn = view.findViewById(R.id.btn_sign_in);
        btnGoToSignUp = view.findViewById(R.id.btn_go_to_sign_up);
        errorTextView = view.findViewById(R.id.tv_error);
        resendEmailButton = view.findViewById(R.id.btn_resend_email);
        loadingProgressBar = view.findViewById(R.id.pb_loading);

        // Başlangıç durumları (Gerekli UI elementlerini sıfırla/gizle)
        errorTextView.setVisibility(View.GONE);
        resendEmailButton.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.GONE);


        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            } else {
                // Her yeni denemede hata ve tekrar gönder butonlarını sıfırla
                errorTextView.setVisibility(View.GONE);
                resendEmailButton.setVisibility(View.GONE);

                userViewModel.loginUser(email, password);
            }
        });

        // Tekrar Gönder butonu tıklandığında ViewModel metodunu çağır.
        resendEmailButton.setOnClickListener(v -> {
            userViewModel.resendVerificationEmail();
        });

        // Hesap Oluştur butonuna navigasyon
        btnGoToSignUp.setOnClickListener(v -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });


        userViewModel.getAuthResult().observe(getViewLifecycleOwner(), authResult -> {
            // Her durumda UI temizliği yap
            errorTextView.setVisibility(View.GONE);
            resendEmailButton.setVisibility(View.GONE);
            loadingProgressBar.setVisibility(View.GONE);

            if (authResult instanceof AuthResultState.Loading) {
                loadingProgressBar.setVisibility(View.VISIBLE);

            } else if (authResult instanceof AuthResultState.Success) {
                // Başarılı Giriş (E-posta DOĞRULANMIŞ)
                AuthResultState.Success success = (AuthResultState.Success) authResult;
                String userName = success.getUser().getUserName() != null ? success.getUser().getUserName() : "Kullanıcı";
                Toast.makeText(getContext(), "Hoş geldin, " + userName + "!", Toast.LENGTH_LONG).show();

                navController.navigate(R.id.action_signInFragment_to_navigation_home);

            } else if (authResult instanceof AuthResultState.Error) {
                // GENEL HATA (Yanlış şifre, ağ hatası vb.)
                AuthResultState.Error error = (AuthResultState.Error) authResult;
                errorTextView.setText("Giriş Başarısız: " + error.getMessage());  // Hata mesajlarını daha sonra AuthResultState'de özelleştireceğim (cafer)
                errorTextView.setVisibility(View.VISIBLE);

            } else if (authResult instanceof AuthResultState.EmailNotVerified) {
                // YENİ DURUM: E-posta DOĞRULANMAMIŞ sinyali geldi.
                errorTextView.setText("Hesabınız doğrulanmamış. Lütfen e-posta gelen kutunuzu kontrol edin.");
                errorTextView.setVisibility(View.VISIBLE);
                resendEmailButton.setVisibility(View.VISIBLE); // Tekrar gönder butonunu göster

            } else if (authResult instanceof AuthResultState.ResendEmailSuccess) {
                // YENİ DURUM: Tekrar gönderme başarılı sinyali geldi.
                Toast.makeText(getContext(), "Doğrulama e-postası başarıyla gönderildi.", Toast.LENGTH_LONG).show();
            }
        });
    }
}