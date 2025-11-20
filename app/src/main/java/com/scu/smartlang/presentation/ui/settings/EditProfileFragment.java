package com.scu.smartlang.presentation.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.UserViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private EditText etUsername;
    private MaterialButton btnSave;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        etUsername = view.findViewById(R.id.et_username);
        btnSave = view.findViewById(R.id.btn_save);

        // Mevcut kullanıcı verisini çek ve göster
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof AuthResultState.Success) {
                String currentName = ((AuthResultState.Success) state).getUser().getUserName();
                if (etUsername.getText().toString().isEmpty()) {
                    etUsername.setText(currentName);
                }
            }
        });
        userViewModel.fetchUserProfile();

        // Kaydet Butonu
        btnSave.setOnClickListener(v -> {
            // Burada güncelleme işlemi yapılabilir (ViewModel'e metod eklenecek)
            Toast.makeText(getContext(), "Değişiklikler kaydedildi (Demo)", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp(); // Geri dön
        });
    }
}