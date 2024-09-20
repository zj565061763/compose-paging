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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.paging.filter
import androidx.paging.map
import com.sd.demo.compose.paging.theme.AppTheme
import com.sd.lib.compose.paging.FIntPagingSource
import com.sd.lib.compose.paging.fIsRefreshing
import com.sd.lib.compose.paging.fPagerFlow
import com.sd.lib.compose.paging.fPagingItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.UUID

class SampleModifierActivity : ComponentActivity() {

   private val _modifier = fPagerFlow { UserPagingSource() }
      .cachedIn(lifecycleScope)
      .modifier { it.id }

   private val _flow = _modifier.flow

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         AppTheme {
            val items = _flow.collectAsLazyPagingItems()
            Content(
               items = items,
               onClickItem = { item ->
                  _modifier.update(item.copy(name = "change"))
               },
               onLongClickItem = { item ->
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
   items: LazyPagingItems<UserModel>,
   onClickItem: (UserModel) -> Unit,
   onLongClickItem: (UserModel) -> Unit,
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
                  .combinedClickable(
                     onClick = { onClickItem(item) },
                     onLongClick = { onLongClickItem(item) },
                  )
                  .padding(5.dp)
            ) {
               Text(text = item.id)
               Text(text = item.name)
            }
         }
      }
   }
}

private class UserPagingSource : FIntPagingSource<UserModel>() {
   override suspend fun loadImpl(params: LoadParams<Int>, key: Int): List<UserModel> {
      delay(1_000)
      return List(20) { index ->
         UserModel(
            id = UUID.randomUUID().toString(),
            name = index.toString(),
         )
      }
   }
}

private fun <T : Any> Flow<PagingData<T>>.modifier(
   getID: (T) -> Any,
): FPagingModifier<T> {
   return FPagingModifier(this, getID)
}

private class FPagingModifier<T : Any>(
   flow: Flow<PagingData<T>>,
   private val getID: (T) -> Any,
) {
   private var _currentPagingData: PagingData<T>? = null
   private val _removeFlow: MutableStateFlow<Map<PagingData<T>, Set<Any>>> = MutableStateFlow(mutableMapOf())
   private val _updateFlow: MutableStateFlow<Map<PagingData<T>, Map<Any, T>>> = MutableStateFlow(mutableMapOf())

   val flow: Flow<PagingData<T>> = flow
      .onEach { _currentPagingData = it }
      .combine(_removeFlow) { data, remove ->
         remove[data]?.let { holder ->
            data.filter { item ->
               val id = getID(item)
               !holder.contains(id)
            }
         } ?: data
      }
      .combine(_updateFlow) { data, update ->
         update[data]?.let { holder ->
            data.map { item ->
               val id = getID(item)
               holder[id] ?: item
            }
         } ?: data
      }

   fun update(item: T) {
      val pagingData = _currentPagingData ?: return
      val id = getID(item)
      _updateFlow.update { value ->
         val holder = value[pagingData]
         if (holder == null) {
            value + (pagingData to mapOf(id to item))
         } else {
            value + (pagingData to (holder + (id to item)))
         }
      }
   }

   fun remove(id: Any) {
      val pagingData = _currentPagingData ?: return
      _removeFlow.update { value ->
         val holder = value[pagingData]
         if (holder == null) {
            value + (pagingData to setOf(id))
         } else {
            value + (pagingData to (holder + id))
         }
      }
   }
}