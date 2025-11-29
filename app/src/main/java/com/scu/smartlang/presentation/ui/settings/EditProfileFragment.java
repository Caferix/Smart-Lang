package com.scu.smartlang.presentation.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {
    private ProfileViewModel profileViewModel;
    private EditText etUsername;
    private MaterialButton btnSave;
    private User currentUser;
    private Uri selectedImageUri;
    private ImageView ivProfileImage;

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
        ivProfileImage = view.findViewById(R.id.iv_profile_image);

        loadUserData();

        ivProfileImage.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveChanges(view));
    }

    private void loadUserData() {
        // Mevcut kullanıcı verisini çek
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof AuthResultState.Success) {
                currentUser = ((AuthResultState.Success) state).getUser();

                if (currentUser != null) {
                    // 1. Kullanıcı adını set et
                    if (etUsername.getText().toString().isEmpty()) {
                        etUsername.setText(currentUser.getUserName());
                    }

                    // 2. Profil Fotoğrafını Yükle (EKSİK OLAN KISIM BUYDU)
                    // Eğer seçili yeni bir resim yoksa, mevcut URL'yi yükle
                    if (selectedImageUri == null) {
                        Glide.with(this)
                                .load(currentUser.getProfileImageUrl()) // URL'den yükle
                                .placeholder(R.drawable.ic_person_24dp) // Yüklenirken göster
                                .error(R.drawable.ic_person_24dp)       // Hata olursa veya URL yoksa göster
                                .circleCrop()                           // Yuvarlak yap
                                .into(ivProfileImage);
                    }
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
            currentUser.setUserName(newName);

            if (selectedImageUri != null) {
                // Önce resmi yükle, sonra URL ile profili güncelle
                profileViewModel.uploadProfileImage(selectedImageUri).thenAccept(imageUrl -> {
                    currentUser.setProfileImageUrl(imageUrl);
                    updateUserAndNavigate(view);
                }).exceptionally(e -> {
                    Toast.makeText(getContext(), "Resim yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return null;
                });
            } else {
                // Sadece kullanıcı adını güncelle
                updateUserAndNavigate(view);
            }
        }
    }

    private void updateUserAndNavigate(View view) {
        profileViewModel.updateUserProfile(currentUser).thenRun(() -> {
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Profil başarıyla güncellendi!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigateUp();
            });
        });
    }


    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).circleCrop().into(ivProfileImage);
                }
            });
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
}