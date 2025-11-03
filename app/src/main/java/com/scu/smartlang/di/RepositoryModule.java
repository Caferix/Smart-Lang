package com.scu.smartlang.di;

import com.scu.smartlang.data.mapper.UserDataMapper;
import com.scu.smartlang.data.repository.FirebaseRepositoryImpl;
import com.scu.smartlang.domain.repository.FirebaseRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract FirebaseRepository bindFirebaseRepository(FirebaseRepositoryImpl repositoryImpl);

    @Provides
    public static UserDataMapper provideUserDataMapper() {
        return new UserDataMapper();
    }
}
