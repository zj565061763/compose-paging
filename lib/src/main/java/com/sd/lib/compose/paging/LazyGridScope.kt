package com.sd.lib.compose.paging

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.CombinedLoadStates
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

fun <T : Any> LazyGridScope.fPagingItems(
   /** Item数据 */
   items: LazyPagingItems<T>,
   /** 获取Item的Key */
   itemKey: ((index: Int) -> Any)? = items.itemKey(),
   /** 获取Item的ContentType */
   itemContentType: (index: Int) -> Any? = items.itemContentType(),
   itemSpan: (LazyGridItemSpanScope.(index: Int) -> GridItemSpan)? = null,
   /** Item内容 */
   itemContent: @Composable LazyGridItemScope.(index: Int, item: T) -> Unit,
) {
   items(
      count = items.itemCount,
      key = itemKey,
      contentType = itemContentType,
      span = itemSpan,
   ) { index ->
      items[index]?.let { item ->
         itemContent(index, item)
      }
   }
}

fun LazyGridScope.fPagingAppend(
   /** Item数据 */
   items: LazyPagingItems<*>,
   key: Any? = "paging append ui state",
   contentType: Any? = "paging append ui state",
   span: (LazyGridItemSpanScope.() -> GridItemSpan)? = { GridItemSpan(maxLineSpan) },
   /** [CombinedLoadStates.append]状态UI */
   content: @Composable LazyGridItemScope.() -> Unit = { FPagingAppend(items) },
) {
   if (items.fShowUIStateAppend()) {
      item(
         key = key,
         contentType = contentType,
         span = span,
         content = content,
      )
   }
}

fun LazyGridScope.fPagingPrepend(
   /** Item数据 */
   items: LazyPagingItems<*>,
   key: Any? = "paging prepend ui state",
   contentType: Any? = "paging prepend ui state",
   span: (LazyGridItemSpanScope.() -> GridItemSpan)? = { GridItemSpan(maxLineSpan) },
   /** [CombinedLoadStates.prepend]状态UI */
   content: @Composable LazyGridItemScope.() -> Unit = { FPagingPrepend(items) },
) {
   if (items.fShowUIStatePrepend()) {
      item(
         key = key,
         contentType = contentType,
         span = span,
         content = content,
      )
   }
}