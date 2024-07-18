package com.sd.lib.compose.paging

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

/**
 * 通用列表
 */
@Composable
fun <T : Any> FPagingLazyVerticalStaggeredGrid(
    columns: StaggeredGridCells,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalItemSpacing: Dp = 0.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(0.dp),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,

    /** Item数据 */
    items: LazyPagingItems<T>,
    /** 获取Item的Key */
    itemKey: ((index: Int) -> Any)? = items.itemKey(),
    /** 获取Item的ContentType */
    itemContentType: (index: Int) -> Any? = items.itemContentType(),
    itemSpan: ((index: Int) -> StaggeredGridItemSpan)? = null,

    /** [CombinedLoadStates.prepend]状态UI */
    pagingPrepend: (@Composable () -> Unit)? = { FPagingPrepend(items) },
    /** [CombinedLoadStates.append]状态UI */
    pagingAppend: (@Composable () -> Unit)? = { FPagingAppend(items) },

    /** 列表之前作用域 */
    beforeItems: (LazyStaggeredGridScope.() -> Unit)? = null,
    /** 列表之后作用域 */
    afterItems: (LazyStaggeredGridScope.() -> Unit)? = null,

    /** Item内容 */
    itemContent: @Composable (item: T) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = columns,
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalItemSpacing = verticalItemSpacing,
        horizontalArrangement = horizontalArrangement,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
    ) {
        beforeItems?.invoke(this)

        if (pagingPrepend != null && items.fShowUIStatePrepend()) {
            item(
                key = "paging prepend ui state",
                contentType = "paging prepend ui state",
                span = StaggeredGridItemSpan.FullLine,
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
                itemContent(item)
            }
        }

        afterItems?.invoke(this)

        if (pagingAppend != null && items.fShowUIStateAppend()) {
            item(
                key = "paging append ui state",
                contentType = "paging append ui state",
                span = StaggeredGridItemSpan.FullLine,
            ) {
                pagingAppend()
            }
        }
    }
}