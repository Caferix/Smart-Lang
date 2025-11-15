package com.scu.smartlang.presentation.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
public class SignUpFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText etUsername;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnSignUp;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this); // NavController başlatıldı

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);


        etUsername = view.findViewById(R.id.et_username_signup);
        etEmail = view.findViewById(R.id.et_email_signup);
        etPassword = view.findViewById(R.id.et_password_signup);
        btnSignUp = view.findViewById(R.id.btn_sign_up);


        btnSignUp.setOnClickListener(v -> {
            String userName = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            } else {

                userViewModel.registerUser(email, password, userName);
            }
        });


        userViewModel.getAuthResult().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult instanceof AuthResultState.Loading) {

                Toast.makeText(getContext(), "Hesap oluşturuluyor...", Toast.LENGTH_SHORT).show();
            } else if (authResult instanceof AuthResultState.Success) {

                AuthResultState.Success success = (AuthResultState.Success) authResult;
                Toast.makeText(getContext(), "Kayıt Başarılı! Hoş geldin, " + success.getUser().getUserName(), Toast.LENGTH_LONG).show();

                // DÜZELTME: Fragment geçişi yapılıyor
                navController.navigate(R.id.action_signUpFragment_to_navigation_home);

            } else if (authResult instanceof AuthResultState.Error) {

                AuthResultState.Error error = (AuthResultState.Error) authResult;
                Toast.makeText(getContext(), "Kayıt Hatası: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}