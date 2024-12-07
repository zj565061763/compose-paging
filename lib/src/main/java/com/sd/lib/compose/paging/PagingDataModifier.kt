package com.sd.lib.compose.paging

import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

fun <T : Any> Flow<PagingData<T>>.modifier(
  onEach: FPagingDataModifier<T>.(PagingData<T>) -> Unit = { clearModify() },
  getID: (T) -> Any,
): FPagingDataModifier<T> {
  return FPagingDataModifier(
    flow = this,
    onEach = onEach,
    getID = getID,
  )
}

class FPagingDataModifier<T : Any> internal constructor(
  flow: Flow<PagingData<T>>,
  private val onEach: FPagingDataModifier<T>.(PagingData<T>) -> Unit,
  private val getID: (T) -> Any,
) {
  private val _stateFlow = MutableStateFlow<State<T>>(State())

  /** 数据流 */
  val flow = flow
    .onEach { onEach(it) }
    .modify()

  /** 移除ID为[id]的项 */
  fun remove(id: Any) {
    _stateFlow.update { state ->
      state.copy(
        remove = state.remove.add(id),
        update = state.update.removeWithID(id),
      )
    }
  }

  /** 更新项 */
  fun update(item: T) {
    _stateFlow.update { state ->
      val id = getID(item)
      when {
        state.remove.containsID(id) -> state
        else -> state.copy(
          update = state.update.add(id, item)
        )
      }
    }
  }

  /** 清除修改 */
  fun clearModify() {
    _stateFlow.update { State() }
  }

  private fun Flow<PagingData<T>>.modify(): Flow<PagingData<T>> {
    return combine(_stateFlow) { data, state ->
      data.let { state.remove.transform(it, getID) }
        .let { state.update.transform(it, getID) }
    }
  }

  private data class State<T : Any>(
    val remove: RemoveState<T> = RemoveState(),
    val update: UpdateState<T> = UpdateState(),
  )

  private data class RemoveState<T : Any>(
    private val holder: Set<Any> = emptySet(),
  ) {
    fun add(id: Any): RemoveState<T> = copy(holder = holder + id)
    fun containsID(id: Any): Boolean = holder.contains(id)
    fun transform(
      data: PagingData<T>,
      getID: (T) -> Any,
    ): PagingData<T> {
      if (holder.isEmpty()) return data
      return data.filter { item ->
        !holder.contains(getID(item))
      }
    }
  }

  private data class UpdateState<T : Any>(
    private val holder: Map<Any, T> = emptyMap(),
  ) {
    fun add(id: Any, item: T): UpdateState<T> = copy(holder = holder + (id to item))
    fun removeWithID(id: Any): UpdateState<T> = if (holder.containsKey(id)) copy(holder = holder - id) else this
    fun transform(
      data: PagingData<T>,
      getID: (T) -> Any,
    ): PagingData<T> {
      if (holder.isEmpty()) return data
      return data.map { item ->
        holder[getID(item)] ?: item
      }
    }
  }
}