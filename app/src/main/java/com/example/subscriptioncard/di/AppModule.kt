package com.example.subscriptioncard.di

import android.app.Application
import androidx.room.Room
import com.example.subscriptioncard.data.datasource.CardDatabase
import com.example.subscriptioncard.data.repository.CardRepositoryImpl
import com.example.subscriptioncard.domain.repository.CardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCardDataBase(app: Application): CardDatabase {
        return Room.databaseBuilder(
            app,
            CardDatabase::class.java,
            CardDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideCardRepository(db: CardDatabase): CardRepository {
        return CardRepositoryImpl(db.cardDao)
    }
}