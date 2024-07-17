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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class SampleUpdateActivity : ComponentActivity() {
    private val _flow = Pager(fPagingConfig()) { SinglePageUserPagingSource() }
        .flow
        .cachedIn(lifecycleScope)

    private val _updateNameFlow = MutableStateFlow(mapOf<String, String>())

    private val _combineFlow = combine(_flow, _updateNameFlow) { data, name ->
        data.map { item ->
            name[item.id]?.let { item.copy(name = it) } ?: item
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val items = _combineFlow.collectAsLazyPagingItems()
                Content(
                    items = items,
                    onClickItem = { item ->
                        _updateNameFlow.update { it + (item.id to "changed") }
                    },
                )
            }
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