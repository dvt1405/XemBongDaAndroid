package com.kt.apps.xembongda.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.kt.apps.xembongda.base.R
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    @BaseScope
    fun providesRemoteConfig(): FirebaseRemoteConfig {
        val config = Firebase.remoteConfig
        config.setDefaultsAsync(R.xml.remote_config_default_values)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 200 else 3600
        }
        config.setConfigSettingsAsync(configSettings)
        return config
    }

    @Provides
    @BaseScope
    fun providesFirebaseDataBase(): FirebaseDatabase {
        return Firebase.database
    }

    @Provides
    @BaseScope
    fun providesFirebaseUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    @Provides
    @BaseScope
    fun providesFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }


    @Provides
    @BaseScope
    fun providesFireStore(): FirebaseFirestore {
        return Firebase.firestore
    }
}
