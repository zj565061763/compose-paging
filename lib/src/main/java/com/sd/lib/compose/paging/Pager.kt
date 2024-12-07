package com.sd.lib.compose.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page.Companion.COUNT_UNDEFINED
import kotlinx.coroutines.flow.Flow

fun <Key : Any, Value : Any> fPagerFlow(
  pageSize: Int = 20,
  prefetchDistance: Int = pageSize,
  enablePlaceholders: Boolean = false,
  initialLoadSize: Int = pageSize,
  maxSize: Int = MAX_SIZE_UNBOUNDED,
  jumpThreshold: Int = COUNT_UNDEFINED,
  initialKey: Key? = null,
  pagingSourceFactory: () -> PagingSource<Key, Value>,
): Flow<PagingData<Value>> {
  return Pager(
    config = PagingConfig(
      pageSize = pageSize,
      prefetchDistance = prefetchDistance,
      enablePlaceholders = enablePlaceholders,
      initialLoadSize = initialLoadSize,
      maxSize = maxSize,
      jumpThreshold = jumpThreshold,
    ),
    initialKey = initialKey,
    pagingSourceFactory = pagingSourceFactory,
  ).flow
}