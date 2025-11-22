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
public class ChangePasswordFragment extends Fragment {

    private PasswordResetViewModel viewModel;
    private EditText etCurrentPassword; // ID'leri etCurrentPassword olarak düzelttik
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button changePasswordButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PasswordResetViewModel.class);

        // View ID'leri düzeltildi
        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        changePasswordButton = view.findViewById(R.id.btn_change_password);
        progressBar = view.findViewById(R.id.pb_loading);

        setupObservers();

        changePasswordButton.setOnClickListener(v -> {
            String current = etCurrentPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(getContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirm)) {
                etConfirmPassword.setError("Şifreler eşleşmiyor");
                etConfirmPassword.requestFocus();
                return;
            }

            // ViewModel'e işlemi başlat
            viewModel.updatePasswordWithReauthentication(current, newPass);
        });
    }

    private void setupObservers() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), loading ->
                progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));

        viewModel.successMessage.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), err -> {
            if (err != null && !err.isEmpty()) {
                Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
            }
        });
    }
}