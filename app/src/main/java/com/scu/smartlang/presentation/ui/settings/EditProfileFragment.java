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
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;
import com.scu.smartlang.presentation.viewmodel.UserViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {
    private ProfileViewModel profileViewModel;
    private EditText etUsername;
    private MaterialButton btnSave;
    private User currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        etUsername = view.findViewById(R.id.et_username);
        btnSave = view.findViewById(R.id.btn_save);

        loadUserData();

        btnSave.setOnClickListener(v -> saveChanges(view));
    }

    private void loadUserData() {
        // Mevcut kullanıcı verisini çek ve EditText'e yaz
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof AuthResultState.Success) {
                currentUser = ((AuthResultState.Success) state).getUser();
                // Kullanıcı adı daha önce set edilmediyse, veritabanından gelen değeri set et
                if (etUsername.getText().toString().isEmpty()) {
                    etUsername.setText(currentUser.getUserName());
                }
            }
        });
        profileViewModel.fetchUserProfile();
    }

    private void saveChanges(View view) {
        String newName = etUsername.getText().toString().trim();

        if (newName.isEmpty()) {
            etUsername.setError("Kullanıcı adı boş olamaz");
            return;
        }

        if (currentUser != null) {
            // Sadece ismi güncelle
            currentUser.setUserName(newName);
            profileViewModel.updateUserProfile(currentUser);
            Toast.makeText(getContext(), "Profil başarıyla güncellendi!", Toast.LENGTH_SHORT).show();
            // Kayıttan sonra bir önceki ekrana (Ayarlar) dön
            Navigation.findNavController(view).navigateUp();
        }
    }
}