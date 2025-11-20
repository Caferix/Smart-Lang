package com.scu.smartlang.domain.usecase.user;

import com.scu.smartlang.domain.repository.FirebaseRepository;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class ResendVerificationEmailUseCase {
    private final FirebaseRepository repository;

    @Inject
    public ResendVerificationEmailUseCase(FirebaseRepository repository) {
        this.repository = repository;
    }

    // Not: Bu metodun çalışması için FirebaseRepository'ye 'resendVerificationEmail' metodunu eklemeniz gerekebilir.
    // Eğer yoksa şimdilik boş dönebiliriz veya repository'yi güncelleyebiliriz.
    public CompletableFuture<Void> execute() {
        // Repository'nizde bu metod henüz yoksa, geçici olarak null döner.
        // Eğer repository'ye eklediyseniz: return repository.resendVerificationEmail();
        return CompletableFuture.completedFuture(null);
    }
}