package com.scu.smartlang.presentation.ui.auth;

import android.os.Bundle;
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
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PasswordResetViewModel.class);

        emailEditText = view.findViewById(R.id.et_email);
        sendResetEmailButton = view.findViewById(R.id.btn_send_reset_email);
        progressBar = view.findViewById(R.id.pb_loading);

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                sendResetEmailButton.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                sendResetEmailButton.setEnabled(true);
            }
        });

        viewModel.successMessage.observe(getViewLifecycleOwner(), message -> {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), error -> {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        sendResetEmailButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                return;
            }
            viewModel.sendPasswordResetEmail(email);
        });
    }
}