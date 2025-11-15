package com.scu.smartlang.presentation.ui.auth;

import android.content.Intent; // Yeni import
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
import com.scu.smartlang.presentation.ui.home.MainActivity; // Yeni import (Ana Aktivite)
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


        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            } else {

                userViewModel.loginUser(email, password);
            }
        });


        btnGoToSignUp.setOnClickListener(v -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });


        userViewModel.getAuthResult().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult instanceof AuthResultState.Loading) {
                Toast.makeText(getContext(), "Giriş yapılıyor...", Toast.LENGTH_SHORT).show();
            } else if (authResult instanceof AuthResultState.Success) {
                AuthResultState.Success success = (AuthResultState.Success) authResult;
                String userName = success.getUser().getUserName() != null ? success.getUser().getUserName() : "Kullanıcı";
                Toast.makeText(getContext(), "Hoş geldin, " + userName + "!", Toast.LENGTH_LONG).show();

                // DÜZELTME: Navigation yerine Activity geçişi yapılıyor
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();

            } else if (authResult instanceof AuthResultState.Error) {
                AuthResultState.Error error = (AuthResultState.Error) authResult;
                Toast.makeText(getContext(), "Hata: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}