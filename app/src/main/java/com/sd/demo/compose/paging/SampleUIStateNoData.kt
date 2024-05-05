package com.sd.demo.compose.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sd.demo.compose.paging.source.EmptyUserPagingSource
import com.sd.demo.compose.paging.theme.AppTheme
import com.sd.lib.compose.paging.FPagingLazyColumn
import com.sd.lib.compose.paging.FUIStateRefresh
import kotlinx.coroutines.flow.Flow

class SampleUIStateNoData : ComponentActivity() {

    private val _flow: Flow<PagingData<UserModel>> = Pager(
        config = PagingConfig(pageSize = 20)
    ) { EmptyUserPagingSource() }.flow.cachedIn(lifecycleScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val items = _flow.collectAsLazyPagingItems()
                Content(items = items)
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UserModel>,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        FPagingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            items = items,
        ) {

        }

        items.FUIStateRefresh(
            stateLoading = {
                CircularProgressIndicator()
            },
            stateError = {
                Text(text = it.toString())
            },
            stateNoData = {
                Text(text = "暂无数据")
            }
        )
    }
}