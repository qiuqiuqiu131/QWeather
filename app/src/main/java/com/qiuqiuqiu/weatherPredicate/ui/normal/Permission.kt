package com.qiuqiuqiu.weatherPredicate.ui.normal

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@SuppressLint("LaunchDuringComposition")
@Composable
fun PermissionInterceptor(
    permission: String,
    modifier: Modifier = Modifier,
    noPermission: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current

    var isGranted by remember { mutableStateOf<Boolean?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isGranted == true ||
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            content()
        } else if (isGranted == null) {
            LaunchedEffect(permissionLauncher) {
                permissionLauncher.launch(permission)
            }
        } else {
            noPermission()
        }
    }
}

@Composable
fun MultiplePermissionsInterceptor(
    permissions: Array<String>,
    modifier: Modifier = Modifier,
    noPermission: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    var isGranted by remember { mutableStateOf<Boolean?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        isGranted = granted.all { it.value }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isGranted == true ||
            permissions.all {
                ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            content()
        } else if (isGranted == null) {
            LaunchedEffect(permissionLauncher) {
                permissionLauncher.launch(permissions)
            }
        } else {
            noPermission()
        }
    }
}