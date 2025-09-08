package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.viewModel.CityEditViewModel

@Composable
fun CityEditScreen(navController: NavController) {
    val viewModel: CityEditViewModel = hiltViewModel()
    viewModel.refreshCities()
    Scaffold(
        topBar = {
            CityEditTopBar(
                cancelClick = { navController.popBackStack() },
                saveClick = {
                    viewModel.saveEdit {
                        navController.popBackStack()
                    }
                })
        }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
                .padding(bottom = 20.dp)
                .fillMaxSize(),
            colors = CardColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledContentColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            LoadingContainer(isInit = viewModel.isInit.value) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(viewModel.cityList) {
                        CityCard(it, onClick = {
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun CityEditTopBar(cancelClick: () -> Unit, saveClick: () -> Unit) {
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
                onClick = cancelClick, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Cancel, null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "管理城市",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = saveClick, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Circle, null,
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
            enable = false
        )
    }
}