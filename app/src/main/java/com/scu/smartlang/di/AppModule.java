package com.scu.smartlang.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    @Provides
    @Singleton
    public Context provideContext(@ApplicationContext Context context) {
        return context;
    }

    // TODO: Database, SharedPreferences vb. eklenecek
}
