package com.sd.demo.compose.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.paging.map
import com.sd.demo.compose.paging.source.SinglePageUserPagingSource
import com.sd.demo.compose.paging.theme.AppTheme
import com.sd.lib.compose.paging.FPagingLazyColumn
import com.sd.lib.compose.paging.fPagingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SampleUpdateActivity : ComponentActivity() {
    private val _flow = Pager(fPagingConfig()) { SinglePageUserPagingSource() }
        .flow
        .cachedIn(lifecycleScope)

    private val _updater = _flow.updater { it.id }
    private val _updateFlow = _updater.updateFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val items = _updateFlow.collectAsLazyPagingItems()
                Content(
                    items = items,
                    onClickItem = { item ->
                        _updater.update(item.copy(name = "change"))
                    },
                )
            }
        }
    }
}

private fun <T : Any> Flow<PagingData<T>>.updater(
    getID: (T) -> Any,
): FPagingUpdater<T> {
    return FPagingUpdater(this, getID)
}

private class FPagingUpdater<T : Any>(
    flow: Flow<PagingData<T>>,
    private val getID: (T) -> Any,
) {
    private var _currentPagingData: PagingData<T>? = null
    private val _updateFlow: MutableStateFlow<Map<PagingData<T>, Map<Any, T>>> = MutableStateFlow(mutableMapOf())

    val updateFlow = flow
        .map { it.also { setCurrentPagingData(it) } }
        .combine(_updateFlow) { data, update ->
            update[data]?.let { map ->
                data.map { item ->
                    val id = getID(item)
                    map[id] ?: item
                }
            } ?: data
        }

    fun update(item: T) {
        val pagingData = _currentPagingData ?: return
        val id = getID(item)
        _updateFlow.update { value ->
            val map = value[pagingData]
            if (map == null) {
                value + (pagingData to mapOf(id to item))
            } else {
                value + (pagingData to (map + (id to item)))
            }
        }
    }

    private fun setCurrentPagingData(pagingData: PagingData<T>) {
        if (_currentPagingData != pagingData) {
            _currentPagingData = pagingData
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UserModel>,
    onClickItem: (UserModel) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { items.refresh() }) {
            Text(text = "refresh")
        }

        FPagingLazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            items = items,
            itemKey = items.itemKey { it.id },
            itemContentType = items.itemContentType(),
        ) { item ->
            Card(
                modifier = Modifier.padding(10.dp),
                onClick = { onClickItem(item) },
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = item.id)
                    Text(text = item.name)
                }
            }
        }
    }
}