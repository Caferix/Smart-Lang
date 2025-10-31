package com.scu.smartlang.data.remote.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirebaseAuth {
    private final com.google.firebase.auth.FirebaseAuth mAuth;

    @Inject
    public FirebaseAuth(){
        this.mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public Task<AuthResult> createUserWithEmailAndPassword(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email,password);
    }

    public Task<AuthResult> signInWithEmailAndPassword(String email, String password){
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public void signOut(){
        mAuth.signOut();
    }

    public Task<Void> sendPasswordResetEmail(String email){
        return mAuth.sendPasswordResetEmail(email);
    }
}
