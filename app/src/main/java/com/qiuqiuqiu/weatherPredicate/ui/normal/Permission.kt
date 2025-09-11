package com.qiuqiuqiu.weatherPredicate.ui.normal

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted
            ->
            isGranted = granted
        }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isGranted == true ||
            ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            content()
        } else if (isGranted == null) {
            LaunchedEffect(permissionLauncher) { permissionLauncher.launch(permission) }
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
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { granted -> isGranted = granted.all { it.value } }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isGranted == true ||
            permissions.all {
                ContextCompat.checkSelfPermission(context, it) ==
                        PackageManager.PERMISSION_GRANTED
            }
        ) {
            content()
        } else if (isGranted == null) {
            LaunchedEffect(permissionLauncher) { permissionLauncher.launch(permissions) }
        } else {
            noPermission()
        }
    }
}

@Composable
fun showPermissionSettingDialog(onDismiss: () -> Unit, context: android.content.Context) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("位置授权提醒") },
        text = { Text("请前往设置页面开启定位权限，否则可能无法正常刷新您的位置信息。") },
        confirmButton = {
            TextButton(
                onClick = {
                    // 跳转到应用设置页面
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:" + context.packageName)
                        }
                    context.startActivity(intent)
                    onDismiss()
                }
            ) { Text("去设置") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
