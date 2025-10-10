package com.qiuqiuqiu.weatherPredicate.ui.screen.time

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.JieQiType
import com.qiuqiuqiu.weatherPredicate.viewModel.JieQiViewModel

@Composable
fun JieQiSideScreen(navController: NavHostController) {
    val appViewModel = LocalAppViewModel.current
    val viewModel: JieQiViewModel = hiltViewModel()

    val jieQi = appViewModel.jieqi.value
    jieQi?.let {
        val type = JieQiType.entries.firstOrNull { t -> t.text == it.name } ?: JieQiType.LiChun
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            JieQiSideScreenTopBar(it.name, navController)
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // JieQiBackground(it.name, 0.5f)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painterResource(type.image), null, modifier = Modifier.width(300.dp),
                        contentScale = ContentScale.FillWidth
                    )

                    // 内容卡片
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = type.backgroundColor.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("诗句", color = type.backgroundColor, fontSize = 16.sp)
                            Text(
                                it.shiju,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text("简介", color = type.backgroundColor, fontSize = 16.sp)
                            Text(
                                it.jieshao,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text("宜忌", color = type.backgroundColor, fontSize = 16.sp)
                            Text(
                                it.yiji,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text("习俗", color = type.backgroundColor, fontSize = 16.sp)
                            Text(
                                it.xishu,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text("美食", color = type.backgroundColor, fontSize = 16.sp)
                            Text(
                                it.meishi,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JieQiSideScreenTopBar(title: String, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(50.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            ),
            modifier = Modifier.align(
                Alignment.Center
            )
        )

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(8.dp)
                .clickable {}
                .align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}