package com.sd.demo.compose.paging

import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

fun <T : Any> Flow<PagingData<T>>.modifier(
   getID: (T) -> Any,
): FPagingDataModifier<T> {
   return FPagingDataModifier(this, getID)
}

class FPagingDataModifier<T : Any>(
   flow: Flow<PagingData<T>>,
   private val getID: (T) -> Any,
) {
   @OptIn(ExperimentalCoroutinesApi::class)
   private val _dispatcher = Dispatchers.Default.limitedParallelism(1)
   private var _realPagingData: PagingData<T>? = null

   private val _removeHolder: MutableMap<PagingData<T>, MutableSet<Any>> = mutableMapOf()
   private val _removeFlow = MutableStateFlow(false)

   private val _updateHolder: MutableMap<PagingData<T>, MutableMap<Any, T>> = mutableMapOf()
   private val _updateFlow = MutableStateFlow(false)

   val flow: Flow<PagingData<T>> = flow
      .flowOn(Dispatchers.Main)
      .onEach {
         _realPagingData = it
      }
      .combine(_removeFlow) { data, _ ->
         _removeHolder[_realPagingData]?.let { holder ->
            data.filter { item ->
               val id = getID(item)
               !holder.contains(id)
            }
         } ?: data
      }
      .combine(_updateFlow) { data, _ ->
         _updateHolder[_realPagingData]?.let { holder ->
            data.map { item ->
               val id = getID(item)
               holder[id] ?: item
            }
         } ?: data
      }
      .flowOn(_dispatcher)

   suspend fun remove(id: Any) {
      withContext(_dispatcher) {
         val key = _realPagingData ?: return@withContext
         val holder = _removeHolder.getOrPut(key) { mutableSetOf() }

         if (holder.add(id)) {
            _removeFlow.value = !_removeFlow.value
         }
      }
   }

   suspend fun update(item: T) {
      withContext(_dispatcher) {
         val key = _realPagingData ?: return@withContext
         val holder = _updateHolder.getOrPut(key) { mutableMapOf() }

         val previous = holder.put(getID(item), item)
         if (previous != item) {
            _updateFlow.value = !_updateFlow.value
         }
      }
   }
}