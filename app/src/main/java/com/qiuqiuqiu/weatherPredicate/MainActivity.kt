/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qiuqiuqiu.weatherPredicate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.qiuqiuqiu.weatherPredicate.ui.theme.LunchTrayTheme
import com.qiuqiuqiu.weatherPredicate.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            enableEdgeToEdge(statusBarStyle = SystemBarStyle.auto(5, 5))
            LunchTrayTheme {

                CompositionLocalProvider(LocalAppViewModel provides appViewModel) {
                    MainApp(
                        Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

val LocalAppViewModel = staticCompositionLocalOf<AppViewModel> {
    Throwable("No AppViewModel provided").printStackTrace()
    error("No AppViewModel provided")
}