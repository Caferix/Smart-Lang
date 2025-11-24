package com.scu.smartlang.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.scu.smartlang.domain.usecase.user.GetCurrentUserProfileUseCase;
import com.scu.smartlang.domain.usecase.user.LoginUserUseCase;
import com.scu.smartlang.domain.usecase.user.RegisterUserUseCase;
import com.scu.smartlang.domain.usecase.user.ResendVerificationEmailUseCase;
import com.scu.smartlang.domain.usecase.user.SignOutUserUseCase;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import java.util.concurrent.CancellationException;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final SignOutUserUseCase signOutUserUseCase;
    private final ResendVerificationEmailUseCase resendVerificationEmailUseCase;
    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;

    private final MutableLiveData<AuthResultState> _authResult = new MutableLiveData<>();
    public LiveData<AuthResultState> getAuthResult() {
        return _authResult;
    }

    @Inject
    public AuthViewModel(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            SignOutUserUseCase signOutUserUseCase,
            ResendVerificationEmailUseCase resendVerificationEmailUseCase,
            GetCurrentUserProfileUseCase getCurrentUserProfileUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.signOutUserUseCase = signOutUserUseCase;
        this.resendVerificationEmailUseCase = resendVerificationEmailUseCase;
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
    }

    public void registerUser(String email, String password, String userName) {
        _authResult.setValue(new AuthResultState.Loading());
        registerUserUseCase.execute(email, password, userName)
                .thenCompose(user -> signOutUserUseCase.execute())
                .thenRun(() -> _authResult.postValue(new AuthResultState.EmailNotVerified()))
                .exceptionally(throwable -> {
                    _authResult.postValue(new AuthResultState.Error(getRootCause(throwable).getLocalizedMessage()));
                    return null;
                });
    }

    public void loginUser(String email, String password) {
        _authResult.setValue(new AuthResultState.Loading());
        loginUserUseCase.execute(email, password)
                .thenCompose(basicUser -> getCurrentUserProfileUseCase.execute())
                .thenAccept(fullUser -> {
                    if (fullUser != null) {
                        _authResult.postValue(new AuthResultState.Success(fullUser));
                    } else {
                        _authResult.postValue(new AuthResultState.Error("User profile not found."));
                    }
                })
                .exceptionally(throwable -> {
                    Throwable cause = getRootCause(throwable);
                    if (cause instanceof IllegalStateException && "Email is not verified".equals(cause.getMessage())) {
                        _authResult.postValue(new AuthResultState.EmailNotVerified());
                    } else {
                        _authResult.postValue(new AuthResultState.Error(cause.getLocalizedMessage()));
                    }
                    return null;
                });
    }

    public void signOut() {
        signOutUserUseCase.execute();
        _authResult.postValue(new AuthResultState.SignedOut());
    }

    public void resendVerificationEmail() {
        resendVerificationEmailUseCase.execute()
                .thenRun(() -> _authResult.postValue(new AuthResultState.ResendEmailSuccess()))
                .exceptionally(throwable -> {
                    _authResult.postValue(new AuthResultState.Error(getRootCause(throwable).getLocalizedMessage()));
                    return null;
                });
    }

    public void clearAuthResultState() {
        _authResult.setValue(null);
    }

    private Throwable getRootCause(Throwable throwable) {
        if (throwable instanceof CancellationException || throwable.getCause() == null) {
            return throwable;
        }
        return getRootCause(throwable.getCause());
    }
}
