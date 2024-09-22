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
   getID: (T) -> Any,
): FPagingDataModifier<T> {
   return FPagingDataModifier(this, getID)
}

class FPagingDataModifier<T : Any>(
   flow: Flow<PagingData<T>>,
   private val getID: (T) -> Any,
) {
   private val _removeFlow = MutableStateFlow<Set<Any>>(emptySet())
   private val _updateFlow = MutableStateFlow<Map<Any, T>>(emptyMap())

   /** 数据流 */
   val flow = flow
      .onEach {
         _removeFlow.update { emptySet() }
         _updateFlow.update { emptyMap() }
      }.modify()

   /**
    * 移除ID为[id]的项
    */
   fun remove(id: Any) {
      _removeFlow.update { value ->
         if (value.contains(id)) {
            value
         } else {
            value + id
         }
      }
   }

   /**
    * 更新项
    */
   fun update(item: T) {
      _updateFlow.update { value ->
         val id = getID(item)
         if (value[id] == item) {
            value
         } else {
            value + (id to item)
         }
      }
   }

   private fun Flow<PagingData<T>>.modify(): Flow<PagingData<T>> {
      return combine(_removeFlow) { data, holder ->
         data.takeIf { holder.isEmpty() } ?: data.filter { item ->
            !holder.contains(getID(item))
         }
      }.combine(_updateFlow) { data, holder ->
         data.takeIf { holder.isEmpty() } ?: data.map { item ->
            holder[getID(item)] ?: item
         }
      }
   }
}