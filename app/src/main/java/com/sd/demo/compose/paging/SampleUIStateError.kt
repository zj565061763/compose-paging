package com.sd.demo.compose.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sd.demo.compose.paging.theme.AppTheme
import com.sd.lib.compose.paging.FIntPagingSource
import com.sd.lib.compose.paging.FUIStateRefresh
import com.sd.lib.compose.paging.fIsRefreshing
import com.sd.lib.compose.paging.fPagerFlow
import com.sd.lib.compose.paging.fPagingItems
import kotlinx.coroutines.delay
import java.io.IOException

class SampleUIStateError : ComponentActivity() {

   private val _flow = fPagerFlow { ErrorPagingSource() }
      .cachedIn(lifecycleScope)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
   modifier: Modifier = Modifier,
   items: LazyPagingItems<UserModel>,
) {
   PullToRefreshBox(
      isRefreshing = items.fIsRefreshing(),
      onRefresh = { items.refresh() },
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center,
   ) {
      LazyColumn(modifier = modifier.fillMaxSize()) {
         fPagingItems(items) { _, item ->
            Text(item.toString())
         }
      }

      items.FUIStateRefresh(
         stateError = {
            Text("加载失败:$it")
         },
      )
   }
}

private class ErrorPagingSource : FIntPagingSource<UserModel>() {
   override suspend fun loadImpl(params: LoadParams<Int>, key: Int): List<UserModel> {
      delay(1_000)
      throw IOException("load error")
   }
}