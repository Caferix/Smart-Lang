package com.scu.smartlang.presentation.ui.auth;

import com.scu.smartlang.domain.model.User;

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

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
