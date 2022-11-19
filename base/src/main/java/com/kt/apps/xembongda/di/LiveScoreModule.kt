package com.kt.apps.xembongda.di

import com.kt.apps.xembongda.repository.ILiveScoresRepository
import com.kt.apps.xembongda.repository.livescores.BongDa24hLiveScores
import dagger.Module
import dagger.Provides

@Module
class LiveScoreModule {

    @Provides
    @BaseScope
    fun providesLiveScore(liveScores: BongDa24hLiveScores): ILiveScoresRepository = liveScores
}