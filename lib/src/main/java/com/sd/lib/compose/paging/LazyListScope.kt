package com.sd.lib.compose.paging

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.CombinedLoadStates
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

fun <T : Any> LazyListScope.fPagingItems(
   /** Item数据 */
   items: LazyPagingItems<T>,
   /** 获取Item的Key */
   itemKey: ((index: Int) -> Any)? = items.itemKey(),
   /** 获取Item的ContentType */
   itemContentType: (index: Int) -> Any? = items.itemContentType(),
   /** Item内容 */
   itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit,
) {
   items(
      count = items.itemCount,
      key = itemKey,
      contentType = itemContentType,
   ) { index ->
      items[index]?.let { item ->
         itemContent(index, item)
      }
   }
}

fun LazyListScope.fPagingAppend(
   /** Item数据 */
   items: LazyPagingItems<*>,
   key: Any? = "paging append ui state",
   contentType: Any? = "paging append ui state",
   /** [CombinedLoadStates.append]状态UI */
   content: @Composable LazyItemScope.() -> Unit = { FPagingAppend(items) },
) {
   if (items.fShowUIStateAppend()) {
      item(
         key = key,
         contentType = contentType,
         content = content,
      )
   }
}

fun LazyListScope.fPagingPrepend(
   /** Item数据 */
   items: LazyPagingItems<*>,
   key: Any? = "paging prepend ui state",
   contentType: Any? = "paging prepend ui state",
   /** [CombinedLoadStates.prepend]状态UI */
   content: @Composable LazyItemScope.() -> Unit = { FPagingPrepend(items) },
) {
   if (items.fShowUIStatePrepend()) {
      item(
         key = key,
         contentType = contentType,
         content = content,
      )
   }
}