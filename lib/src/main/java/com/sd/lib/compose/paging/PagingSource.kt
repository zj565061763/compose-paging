package com.sd.lib.compose.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException

/**
 * Key为[Int]的数据源
 */
abstract class FIntPagingSource<Value : Any>(
  /** 起始页码 */
  val initialKey: Int = 1,
) : FPagingSource<Int, Value>() {
  override suspend fun loadImpl(params: LoadParams<Int>): LoadResult<Int, Value> {
    val key = params.key ?: initialKey
    val data = loadImpl(params, key) ?: return LoadResult.Invalid()
    return LoadResult.Page(
      data = data,
      prevKey = if (key == initialKey) null else key - 1,
      nextKey = if (data.isEmpty()) null else key + 1,
    )
  }

  override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
  }

  /**
   * 加载数据
   *
   * @param key 页码
   * @return 加载的数据列表，null表示当前PagingSource失效；空列表表示没有下一页数据
   */
  protected abstract suspend fun loadImpl(params: LoadParams<Int>, key: Int): List<Value>?
}

abstract class FPagingSource<Key : Any, Value : Any> : PagingSource<Key, Value>() {
  final override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value> {
    return runCatching {
      loadImpl(params)
    }.getOrElse { error ->
      if (error is CancellationException) throw error
      onLoadError(error)
    }
  }

  override fun getRefreshKey(state: PagingState<Key, Value>): Key? = null

  /**
   * 加载数据
   */
  protected abstract suspend fun loadImpl(params: LoadParams<Key>): LoadResult<Key, Value>

  /**
   * 加载错误回调
   */
  protected open fun onLoadError(error: Throwable): LoadResult<Key, Value> = LoadResult.Error(error)
}