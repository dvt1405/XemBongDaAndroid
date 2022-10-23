package com.kt.apps.xembongda.di.main

import com.kt.apps.xembongda.di.PlayerModule
import dagger.Component

@Component(
    modules = [
        PlayerModule::class
    ]
)
@MainActivityScope
interface MainActivityComponents {
//    fun inject(mainActivity: MainActivity)

//    @Subcomponent.Factory
//    interface Factory {
//        fun create(
//            @BindsInstance main: AppCompatActivity,
//            components: AppComponents,
//            playerModule: PlayerModule
//        ): MainActivityComponents
//    }
}