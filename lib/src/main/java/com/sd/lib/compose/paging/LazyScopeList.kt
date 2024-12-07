package com.sd.lib.compose.paging

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

fun <T : Any> LazyListScope.fPagingItems(
  items: LazyPagingItems<T>,
  key: ((index: Int) -> Any)? = items.itemKey(),
  contentType: (index: Int) -> Any? = items.itemContentType(),
  content: @Composable LazyItemScope.(index: Int, item: T) -> Unit,
) {
  items(
    count = items.itemCount,
    key = key,
    contentType = contentType,
  ) { index ->
    items[index]?.let { item ->
      content(index, item)
    }
  }
}

fun LazyListScope.fPagingAppend(
  items: LazyPagingItems<*>,
  key: Any? = "paging append ui state",
  contentType: Any? = "paging append ui state",
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
  items: LazyPagingItems<*>,
  key: Any? = "paging prepend ui state",
  contentType: Any? = "paging prepend ui state",
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