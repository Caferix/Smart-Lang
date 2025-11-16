package com.scu.smartlang.presentation.ui.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.scu.smartlang.R;

public class CheckEmailDialogFragment extends DialogFragment {

    public static final String TAG = "CheckEmailDialogFragment";

    private NavController navController;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_check_email_dialog, null);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        TextView tvMessage = view.findViewById(R.id.tv_dialog_message);
        MaterialButton btnGoToSignIn = view.findViewById(R.id.btn_go_to_signin);

        tvTitle.setText("Kayıt Başarılı!");
        tvMessage.setText("Hesabınızı etkinleştirmek için size bir doğrulama e-postası gönderdik. Lütfen e-postanızı kontrol edin ve ardından giriş yapmak için aşağıya tıklayın.");

        // Giriş Yap Butonu
        btnGoToSignIn.setOnClickListener(v -> {
            // Dialog'u kapat
            dismiss();
            // Giriş ekranına git
            if (navController != null) {
                // SignUpFragment'tan çağrıldığı için, popBackStack ile önceki ekranı kaldırıp
                // giriş ekranına yönlendiriyoruz.
                navController.popBackStack();
                navController.navigate(R.id.signInFragment);
            }
        });

        navController = NavHostFragment.findNavController(this);


        // Dialog'u oluşturduk
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        builder.setView(view);

        // Kullanıcının dialog dışında tıklayarak kapatmasını engelle
        setCancelable(false);

        return builder.create();
    }
}