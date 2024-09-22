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
   private var _realPagingData: PagingData<T>? = null
   private val _removeFlow = MutableStateFlow<Map<PagingData<T>, Set<Any>>>(emptyMap())
   private val _updateFlow = MutableStateFlow<Map<PagingData<T>, Map<Any, T>>>(emptyMap())

   /** 数据流 */
   val flow = flow
      .onEach {
         _realPagingData = it
         _removeFlow.update { emptyMap() }
         _updateFlow.update { emptyMap() }
      }.modify()

   /**
    * 移除ID为[id]的项
    */
   fun remove(id: Any) {
      _removeFlow.update { value ->
         val key = _realPagingData ?: return
         val holder = value[key] ?: emptySet()

         if (holder.contains(id)) {
            value
         } else {
            value + (key to (holder + id))
         }
      }
   }

   /**
    * 更新项
    */
   fun update(item: T) {
      _updateFlow.update { value ->
         val key = _realPagingData ?: return
         val holder = value[key] ?: emptyMap()

         val id = getID(item)
         if (holder[id] == item) {
            value
         } else {
            value + (key to (holder + (id to item)))
         }
      }
   }

   private fun Flow<PagingData<T>>.modify(): Flow<PagingData<T>> {
      return combine(_removeFlow) { data, map ->
         map[_realPagingData]?.let { holder ->
            data.filter { item ->
               !holder.contains(getID(item))
            }
         } ?: data
      }.combine(_updateFlow) { data, map ->
         map[_realPagingData]?.let { holder ->
            data.map { item ->
               holder[getID(item)] ?: item
            }
         } ?: data
      }
   }
}