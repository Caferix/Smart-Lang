package com.scu.smartlang.di;

import com.scu.smartlang.data.mapper.UserDataMapper;
import com.scu.smartlang.data.repository.AuthRepositoryImpl;
import com.scu.smartlang.data.repository.SocialRepositoryImpl;
import com.scu.smartlang.data.repository.UserProfileRepositoryImpl;
import com.scu.smartlang.domain.repository.AuthRepository;
import com.scu.smartlang.domain.repository.SocialRepository;
import com.scu.smartlang.domain.repository.UserProfileRepository;

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
    public abstract AuthRepository bindAuthRepository(AuthRepositoryImpl authRepositoryImpl);

    @Binds
    @Singleton
    public abstract UserProfileRepository bindUserProfileRepository(UserProfileRepositoryImpl userProfileRepositoryImpl);

    @Binds
    @Singleton
    public abstract SocialRepository bindSocialRepository(SocialRepositoryImpl socialRepositoryImpl);

    @Provides
    public static UserDataMapper provideUserDataMapper() {
        return new UserDataMapper();
    }
}
