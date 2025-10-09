import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.qiuqiuqiu.weatherPredicate.R
import com.qiuqiuqiu.weatherPredicate.model.AllNewsItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.ElevatedBaseCard
import com.qiuqiuqiu.weatherPredicate.viewModel.AllNewsViewModel

@Composable
fun NewsCard(col: Int = 5, key: String) {
    val viewModel: AllNewsViewModel = hiltViewModel(key = key)
    val newsList = viewModel.newsResult.collectAsState()

    LaunchedEffect(col) {
        viewModel.fetchAllNews(3, col)
    }

    newsList.value?.result?.newslist?.let {
        ElevatedBaseCard(title = "新闻资讯") {
            Column {
                it.forEach { item ->
                    NewsItem(news = item)
                }
            }
        }
    }
}

@Preview
@Composable
fun NewsItem(news: AllNewsItem = testNewsItem()) {
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .background(if (isPressed) MaterialTheme.colorScheme.surface.copy(alpha = 0.3f) else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .height(90.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                    onTap = {
                        // 打开新闻链接
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                        context.startActivity(intent)
                    }
                )
            }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = if (news.description.isEmpty()) 2 else 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = news.description,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = news.source,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .alpha(0.6f)
                )
                Text(
                    text = news.ctime,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .alpha(0.6f)
                )
            }

        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(vertical = 8.dp)
                .fillMaxHeight()
        ) {
            if (news.picUrl.isEmpty()) {
                Image(
                    painter = painterResource(R.drawable.news),
                    null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    news.picUrl, null, modifier = Modifier
                        .fillMaxSize(), contentScale = ContentScale.Crop
                )
            }
        }

    }
}

fun testNewsItem() = AllNewsItem(
    "1",
    "1",
    "河南女生打110叫外卖？ 接警员听出玄机将其解救",
    "凤凰社会",
    "凤凰社会",
    "http://d.ifengimg.com/w150_h95/p2.ifengimg.com/fck/2018_32/2a0c4f906a7c4cc_w440_h782.jpg",
    "http://news.ifeng.com/a/20180805/59633979_0.shtml"
)