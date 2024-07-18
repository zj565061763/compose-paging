package com.sd.lib.compose.paging

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

/**
 * 通用列表
 */
@Composable
fun <T : Any> FPagingLazyVerticalGrid(
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,

    /** Item数据 */
    items: LazyPagingItems<T>,
    /** 获取Item的Key */
    itemKey: ((index: Int) -> Any)? = items.itemKey(),
    /** 获取Item的ContentType */
    itemContentType: (index: Int) -> Any? = items.itemContentType(),
    itemSpan: (LazyGridItemSpanScope.(index: Int) -> GridItemSpan)? = null,

    /** [CombinedLoadStates.prepend]状态UI */
    pagingPrepend: (@Composable () -> Unit)? = { FPagingPrepend(items) },
    /** [CombinedLoadStates.append]状态UI */
    pagingAppend: (@Composable () -> Unit)? = { FPagingAppend(items) },

    /** 列表之前作用域 */
    beforeItems: (LazyGridScope.() -> Unit)? = null,
    /** 列表之后作用域 */
    afterItems: (LazyGridScope.() -> Unit)? = null,

    /** Item内容 */
    itemContent: @Composable (index: Int, item: T) -> Unit,
) {
    LazyVerticalGrid(
        columns = columns,
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
    ) {
        beforeItems?.invoke(this)

        if (pagingPrepend != null && items.fShowUIStatePrepend()) {
            item(
                key = "paging prepend ui state",
                contentType = "paging prepend ui state",
                span = { GridItemSpan(maxLineSpan) },
            ) {
                pagingPrepend()
            }
        }

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

        afterItems?.invoke(this)

        if (pagingAppend != null && items.fShowUIStateAppend()) {
            item(
                key = "paging append ui state",
                contentType = "paging append ui state",
                span = { GridItemSpan(maxLineSpan) },
            ) {
                pagingAppend()
            }
        }
    }
}