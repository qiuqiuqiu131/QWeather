package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CityManageScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CityManagerTopBar(
                navBack = { navController.popBackStack() },
                searchClick = { navController.navigate("WeatherSearch") })
        },
        bottomBar = {
            CityManagerBottomBar(onClick = { navController.navigate("WeatherSearch") })
        }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            }
        }

    }
}

@Composable
fun CityManagerTopBar(
    navBack: () -> Unit,
    searchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                .height(55.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = navBack, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft, null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "管理城市",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {}, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            )
            {
                Icon(
                    imageVector = Icons.Outlined.Settings, null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        SearchButton(
            "搜索城市(中文/拼音)",
            Modifier
                .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                .fillMaxWidth()
                .height(42.dp),
            onClick = searchClick
        )
    }

}

@Composable
fun CityManagerBottomBar(onClick: (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(90.dp)
    ) {
        ElevatedButton(
            onClick = { onClick?.invoke() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(200.dp)
                .padding(bottom = 25.dp)
        ) { Text(text = "添加城市", style = MaterialTheme.typography.titleMedium) }
    }
}

@Composable
fun SearchButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = { onClick?.invoke() })
        ) {
            Icon(
                imageVector = Icons.Default.Search, null,
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(22.dp)
            )

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            {
                Text(
                    label,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .alpha(0.6f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                )
            }
        }
    }
}