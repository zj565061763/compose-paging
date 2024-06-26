package com.sd.lib.compose.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

/**
 * 是否刷新中
 */
fun LazyPagingItems<*>.fIsRefreshing(): Boolean {
    return loadState.refresh == LoadState.Loading
}

/**
 * 数据是否为空
 */
@Composable
fun LazyPagingItems<*>.fIsEmpty(): Boolean {
    val items = this
    return remember(items) {
        derivedStateOf { items.itemCount == 0 }
    }.value
}

/**
 * [CombinedLoadStates.refresh]状态
 */
@Composable
inline fun LazyPagingItems<*>.FUIStateRefresh(
    /** 是否检查数据为空 */
    checkEmpty: Boolean = true,
    /** 加载中 */
    stateLoading: @Composable () -> Unit = {},
    /** 加载错误 */
    stateError: @Composable (Throwable) -> Unit = {},
    /** 无数据 */
    stateNoData: @Composable () -> Unit = {},
) {
    if (checkEmpty && !fIsEmpty()) {
        return
    }
    when (val loadState = loadState.refresh) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(loadState.error)
        is LoadState.NotLoading -> stateNoData()
    }
}

/**
 * 是否显示[CombinedLoadStates.append]状态
 */
fun LazyPagingItems<*>.fShowUIStateAppend(): Boolean {
    return itemCount > 0
}

/**
 * [CombinedLoadStates.append]状态
 */
@Composable
inline fun LazyPagingItems<*>.FUIStateAppend(
    /** 加载中 */
    stateLoading: @Composable () -> Unit = {},
    /** 加载错误 */
    stateError: @Composable (Throwable) -> Unit = {},
    /** 没有更多数据 */
    stateNoMoreData: @Composable () -> Unit = {},
) {
    when (val loadState = loadState.append) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(loadState.error)
        is LoadState.NotLoading -> {
            if (loadState.endOfPaginationReached) {
                stateNoMoreData()
            }
        }
    }
}

/**
 * 是否显示[CombinedLoadStates.prepend]状态
 */
fun LazyPagingItems<*>.fShowUIStatePrepend(): Boolean {
    if (itemCount <= 0) return false
    if (fIsRefreshing()) return false
    return !loadState.prepend.endOfPaginationReached
}

/**
 * [CombinedLoadStates.prepend]状态
 */
@Composable
inline fun LazyPagingItems<*>.FUIStatePrepend(
    /** 加载中 */
    stateLoading: @Composable () -> Unit = {},
    /** 加载错误 */
    stateError: @Composable (Throwable) -> Unit = {},
) {
    when (val loadState = loadState.prepend) {
        is LoadState.Loading -> stateLoading()
        is LoadState.Error -> stateError(loadState.error)
        else -> {}
    }
}