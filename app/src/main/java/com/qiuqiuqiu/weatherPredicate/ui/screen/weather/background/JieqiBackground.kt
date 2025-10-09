package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.R

@Composable
fun JieQiBackground(name: String, alpha: Float = 0f) {
    val jieqi = JieQiType.entries.firstOrNull { it.text == name } ?: JieQiType.LiChun
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(jieqi.backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(1 - 0.75f * alpha)
        ) {
            Image(
                painterResource(jieqi.backgroundImage), null, modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                jieqi.backgroundColor.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                jieqi.backgroundColor,
                                jieqi.backgroundColor
                            ),
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }

    }
}


enum class JieQiType(
    val text: String,
    val backgroundColor: Color,
    val backgroundImage: Int
) {
    // 春天：深蓝绿
    LiChun("立春", Color(0xFF439D3A), R.drawable.lichun),
    YuShui("雨水", Color(0xFF439D3A), R.drawable.yushui),
    JingZhe("惊蛰", Color(0xFF439D3A), R.drawable.jingzhe),
    ChunFen("春分", Color(0xFF439D3A), R.drawable.chunfen),
    QingMing("清明", Color(0xFF439D3A), R.drawable.qingming),
    GuYu("谷雨", Color(0xFF439D3A), R.drawable.guyu),

    // 夏天：深绿色
    LiXia("立夏", Color(0xFF439D3A), R.drawable.lixia),
    XiaoMan("小满", Color(0xFFDC8746), R.drawable.xiaoman),
    MangZhong("芒种", Color(0xFFC28C30), R.drawable.mangzhong),
    XiaZhi("夏至", Color(0xFF439D3A), R.drawable.xiazhi),
    XiaoShu("小暑", Color(0xFF439D3A), R.drawable.xiaoshu),
    DaShu("大暑", Color(0xFF439D3A), R.drawable.dashu),

    // 秋天：深橙黄
    LiQiu("立秋", Color(0xFFDC8E46), R.drawable.liqiu),
    ChuShu("处暑", Color(0xFFDC8E46), R.drawable.chushu),
    BaiLu("白露", Color(0xFFDC8E46), R.drawable.bailu),
    QiuFen("秋分", Color(0xFFDC8E46), R.drawable.qiufen),
    HanLu("寒露", Color(0xFFDC8E46), R.drawable.hanlu),
    ShuangJiang("霜降", Color(0xFF4878C5), R.drawable.shuangjiang),

    // 冬天：深蓝
    LiDong("立冬", Color(0xFF4878C5), R.drawable.lidong),
    XiaoXue("小雪", Color(0xFF7BA3E3), R.drawable.xiaoxue),
    DaXue("大雪", Color(0xFF739DE1), R.drawable.daxue),
    DongZhi("冬至", Color(0xFF6BB3FF), R.drawable.dongzhi),
    XiaoHan("小寒", Color(0xFF739DE1), R.drawable.xiaohan),
    DaHan("大寒", Color(0xFF739DE1), R.drawable.dahan);
}