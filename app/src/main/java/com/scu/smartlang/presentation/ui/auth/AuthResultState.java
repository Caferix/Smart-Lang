package com.scu.smartlang.presentation.ui.auth;

import com.scu.smartlang.domain.model.User;

import javax.annotation.Nullable;
public interface AuthResultState {
    // Loading
    class Loading implements AuthResultState {
        public Loading() {}
    }

    // Auth result: successfull
    class Success implements AuthResultState {
        private final User user;
        public Success(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    // Auth result: Error
    class Error implements AuthResultState {
        private final String message;
        @Nullable
        private final Exception exception;
        public Error(String message, @Nullable Exception exception) {
            this.message = message;
            this.exception = exception;
        }

        public Error(String message){
            this(message,null);
        }

        public String getMessage() {

            return message;
        }
        @Nullable
        public Exception getException() {
            return exception;
        }
    }



    // Email couldn't verified hatası
    public static final class EmailNotVerified implements AuthResultState {

    }

    // E-posta gönderildiğini bildirmek için
    public static final class ResendEmailSuccess implements AuthResultState {

    }
    public static class SignedOut implements AuthResultState {
        // Oturumun kapalı olduğunu belirtir.
    }
}
