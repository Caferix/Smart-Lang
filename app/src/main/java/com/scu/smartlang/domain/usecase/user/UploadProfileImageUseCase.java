package com.scu.smartlang.domain.usecase.user;

import android.net.Uri;
import com.scu.smartlang.domain.repository.UserProfileRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class UploadProfileImageUseCase {

    private final UserProfileRepository repository;

    @Inject
    public UploadProfileImageUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<String> execute(Uri imageUri) {
        return repository.uploadProfileImage(imageUri);
    }
}
