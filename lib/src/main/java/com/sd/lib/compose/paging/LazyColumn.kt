package com.sd.lib.compose.paging

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun <T : Any> FPagingLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,

    /** Item数据 */
    items: LazyPagingItems<T>,
    /** 获取Item的Key */
    itemKey: ((index: Int) -> Any)? = items.itemKey(),
    /** 获取Item的ContentType */
    itemContentType: (index: Int) -> Any? = items.itemContentType(),

    /** [CombinedLoadStates.prepend]状态UI */
    pagingPrepend: (@Composable () -> Unit)? = { FPagingPrepend(items) },
    /** [CombinedLoadStates.append]状态UI */
    pagingAppend: (@Composable () -> Unit)? = { FPagingAppend(items) },

    /** 列表之前作用域 */
    beforeItems: (LazyListScope.() -> Unit)? = null,
    /** 列表之后作用域 */
    afterItems: (LazyListScope.() -> Unit)? = null,

    /** Item内容 */
    itemContent: @Composable (item: T) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
    ) {
        beforeItems?.invoke(this)

        if (pagingPrepend != null && items.fShowUIStatePrepend()) {
            item(
                key = "paging prepend ui state",
                contentType = "paging prepend ui state",
            ) {
                pagingPrepend()
            }
        }

        items(
            count = items.itemCount,
            key = itemKey,
            contentType = itemContentType,
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
            ) {
                pagingAppend()
            }
        }
    }
}