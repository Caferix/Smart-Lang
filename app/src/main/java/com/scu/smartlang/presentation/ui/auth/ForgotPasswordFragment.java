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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.PasswordResetViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private PasswordResetViewModel viewModel;
    private EditText emailEditText;
    private Button sendResetEmailButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PasswordResetViewModel.class);

        emailEditText = view.findViewById(R.id.et_email);
        sendResetEmailButton = view.findViewById(R.id.btn_send_reset_email);
        progressBar = view.findViewById(R.id.pb_loading);

        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            sendResetEmailButton.setEnabled(!isLoading);
        });

        viewModel.successMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        sendResetEmailButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (email.isEmpty()) {
                emailEditText.setError("Email adresi gerekli");
                emailEditText.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Geçerli bir email adresi girin");
                emailEditText.requestFocus();
                return;
            }

            viewModel.sendPasswordResetEmail(email);
        });
    }
}