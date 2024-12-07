package com.sd.demo.compose.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.sd.demo.compose.paging.theme.AppTheme
import com.sd.lib.compose.paging.FIntPagingSource
import com.sd.lib.compose.paging.FPagingDataModifier
import com.sd.lib.compose.paging.fIsRefreshing
import com.sd.lib.compose.paging.fPagerFlow
import com.sd.lib.compose.paging.fPagingItems
import com.sd.lib.compose.paging.modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class SampleModifierActivity : ComponentActivity() {

  /** 数据修改对象 */
  private val _modifier: FPagingDataModifier<ModifyModel> =
    fPagerFlow { ModifyModelPagingSource() }
      .cachedIn(lifecycleScope)
      .modifier { it.id }

  /** 数据流 */
  private val _flow: Flow<PagingData<ModifyModel>> = _modifier.flow

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        val items = _flow.collectAsLazyPagingItems()
        Content(
          items = items,
          onClickItem = { item ->
            // 点击修改Item
            _modifier.update(item.copy(count = item.count + 1))
          },
          onLongClickItem = { item ->
            // 长按删除Item
            _modifier.remove(item.id)
          }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun Content(
  modifier: Modifier = Modifier,
  items: LazyPagingItems<ModifyModel>,
  onClickItem: (ModifyModel) -> Unit,
  onLongClickItem: (ModifyModel) -> Unit,
) {
  PullToRefreshBox(
    isRefreshing = items.fIsRefreshing(),
    onRefresh = { items.refresh() },
    modifier = modifier.fillMaxSize(),
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      fPagingItems(
        items = items,
        key = items.itemKey { it.id },
        contentType = items.itemContentType(),
      ) { _, item ->
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .animateItem()
            .combinedClickable(
              onClick = { onClickItem(item) },
              onLongClick = { onLongClickItem(item) },
            )
            .padding(5.dp),
        ) {
          Text("id: ${item.id}")
          Text(
            text = "count: ${item.count}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
          )
        }
      }
    }
  }
}

private class ModifyModelPagingSource : FIntPagingSource<ModifyModel>() {
  override suspend fun loadImpl(params: LoadParams<Int>, key: Int): List<ModifyModel> {
    delay(500)
    return if (key == initialKey) {
      List(5) { index ->
        ModifyModel(
          id = (index + 1).toString(),
          count = 0,
        )
      }
    } else emptyList()
  }
}

private data class ModifyModel(
  val id: String,
  val count: Int,
)