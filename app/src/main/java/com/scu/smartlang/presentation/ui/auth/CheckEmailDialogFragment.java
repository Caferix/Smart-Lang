package com.scu.smartlang.presentation.ui.auth;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider; // Eklendi
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.UserViewModel; // Eklendi

/**
 * Kayıt başarılı olduktan sonra e-posta doğrulama talimatını gösteren sade iletişim kutusu.
 * Harici XML dosyasına ihtiyaç duymaz, standart AlertDialog kullanır.
 */
public class CheckEmailDialogFragment extends DialogFragment {

    public static final String TAG = "CheckEmailDialogFragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Standart AlertDialog.Builder kullanılarak XML bağımlılığı kaldırıldı.
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Başlık ve Mesajı ayarla
        builder.setTitle("Kayıt Başarılı!")
                .setMessage("Hesabınızı etkinleştirmek için size bir doğrulama e-postası gönderdik. Lütfen e-postanızı kontrol edin ve ardından giriş yapmak için aşağıdaki butona tıklayın.");

        // Pozitif Buton (Giriş Ekranına Gitme Aksiyonu)
        builder.setPositiveButton("GİRİŞ EKRANINA GİT", (dialog, id) -> {
            // Dialog'u kapat
            dialog.dismiss();

            try {
                // ViewModel'e Activity üzerinden erişim (Fragment'lar arası paylaşılan instance)
                UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
                // ÖNCEKİ Auth sonucunu temizle (EmailNotVerified durumunu null'a çek)
                userViewModel.clearAuthResultState();

                // Navigasyonu gerçekleştirmek için NavController'ı doğrudan Fragment'tan bul
                NavController navController = NavHostFragment.findNavController(this);

                // Önce SignUp Fragment'ı Nav Stack'ten çıkar
                navController.popBackStack();

                // Sonra SignIn Fragment'a git (R.id.signInFragment, NavGraph'ta tanımlı olmalı)
                navController.navigate(R.id.signInFragment);
            } catch (IllegalStateException e) {
                // NavController bulunamazsa veya navigasyon hatası oluşursa kullanıcı bilgilendirilebilir.
                // Log.e(TAG, "Navigasyon hatası: " + e.getMessage());
            }
        });

        // Kullanıcının dialog dışında tıklayarak kapatmasını engelle
        setCancelable(false);

        return builder.create();
    }
}