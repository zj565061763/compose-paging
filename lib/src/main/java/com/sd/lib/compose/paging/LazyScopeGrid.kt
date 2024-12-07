package com.sd.lib.compose.paging

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

fun <T : Any> LazyGridScope.fPagingItems(
  items: LazyPagingItems<T>,
  key: ((index: Int) -> Any)? = items.itemKey(),
  contentType: (index: Int) -> Any? = items.itemContentType(),
  span: (LazyGridItemSpanScope.(index: Int) -> GridItemSpan)? = null,
  content: @Composable LazyGridItemScope.(index: Int, item: T) -> Unit,
) {
  items(
    count = items.itemCount,
    key = key,
    contentType = contentType,
    span = span,
  ) { index ->
    items[index]?.let { item ->
      content(index, item)
    }
  }
}

fun LazyGridScope.fPagingAppend(
  items: LazyPagingItems<*>,
  key: Any? = "paging append ui state",
  contentType: Any? = "paging append ui state",
  span: (LazyGridItemSpanScope.() -> GridItemSpan)? = { GridItemSpan(maxLineSpan) },
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
  items: LazyPagingItems<*>,
  key: Any? = "paging prepend ui state",
  contentType: Any? = "paging prepend ui state",
  span: (LazyGridItemSpanScope.() -> GridItemSpan)? = { GridItemSpan(maxLineSpan) },
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