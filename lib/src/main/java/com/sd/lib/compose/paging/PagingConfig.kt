package com.sd.lib.compose.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED
import androidx.paging.PagingSource.LoadResult.Page.Companion.COUNT_UNDEFINED

fun fPagingConfig(
    pageSize: Int = 20,
    prefetchDistance: Int = pageSize,
    enablePlaceholders: Boolean = false,
    initialLoadSize: Int = pageSize,
    maxSize: Int = MAX_SIZE_UNBOUNDED,
    jumpThreshold: Int = COUNT_UNDEFINED,
): PagingConfig {
    return PagingConfig(
        pageSize = pageSize,
        prefetchDistance = prefetchDistance,
        enablePlaceholders = enablePlaceholders,
        initialLoadSize = initialLoadSize,
        maxSize = maxSize,
        jumpThreshold = jumpThreshold,
    )
}