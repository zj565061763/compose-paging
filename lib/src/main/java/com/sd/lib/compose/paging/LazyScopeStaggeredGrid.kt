package com.sd.lib.compose.paging

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

fun <T : Any> LazyStaggeredGridScope.fPagingItems(
   items: LazyPagingItems<T>,
   key: ((index: Int) -> Any)? = items.itemKey(),
   contentType: (index: Int) -> Any? = items.itemContentType(),
   span: ((index: Int) -> StaggeredGridItemSpan)? = null,
   content: @Composable LazyStaggeredGridItemScope.(index: Int, item: T) -> Unit,
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

fun LazyStaggeredGridScope.fPagingAppend(
   items: LazyPagingItems<*>,
   key: Any? = "paging append ui state",
   contentType: Any? = "paging append ui state",
   span: StaggeredGridItemSpan? = StaggeredGridItemSpan.FullLine,
   content: @Composable LazyStaggeredGridItemScope.() -> Unit = { FPagingAppend(items) },
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

fun LazyStaggeredGridScope.fPagingPrepend(
   items: LazyPagingItems<*>,
   key: Any? = "paging prepend ui state",
   contentType: Any? = "paging prepend ui state",
   span: StaggeredGridItemSpan? = StaggeredGridItemSpan.FullLine,
   content: @Composable LazyStaggeredGridItemScope.() -> Unit = { FPagingPrepend(items) },
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