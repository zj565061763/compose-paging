package com.sd.demo.compose.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sd.demo.compose.paging.theme.AppTheme
import com.sd.lib.compose.paging.FIntPagingSource
import com.sd.lib.compose.paging.fPagerFlow
import com.sd.lib.compose.paging.fPagingAppend
import com.sd.lib.compose.paging.fPagingItems
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.UUID

class SampleActivity : ComponentActivity() {

   private val _flow = fPagerFlow { UserPagingSource() }
      .cachedIn(lifecycleScope)

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         AppTheme {
            val items = _flow.collectAsLazyPagingItems()
            Content(items = items)
         }
      }
   }
}

@Composable
private fun Content(
   modifier: Modifier = Modifier,
   items: LazyPagingItems<UserModel>,
) {
   Column(
      modifier = modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
   ) {
      Button(onClick = { items.refresh() }) {
         Text(text = "refresh")
      }

      LazyColumn(
         modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
      ) {
         fPagingItems(
            items = items,
            itemKey = items.itemKey { it.id },
         ) { _, item ->
            Card(modifier = Modifier.padding(10.dp)) {
               Column(modifier = Modifier.fillMaxWidth()) {
                  Text(text = item.id)
                  Text(text = item.name)
               }
            }
         }

         fPagingAppend(items)
      }
   }
}

private class UserPagingSource : FIntPagingSource<UserModel>() {
   private val _maxPage = 5
   private val _errorPage = 3
   private var _hasLoadError = false

   override suspend fun loadImpl(params: LoadParams<Int>, key: Int): List<UserModel> {
      logMsg { "load key:$key params:$params $this" }
      delay(1_000)

      if (key >= _maxPage) {
         return emptyList()
      }

      if (key == _errorPage) {
         if (!_hasLoadError) {
            _hasLoadError = true
            throw IOException("load key:$key error")
         }
      }

      return List(20) { index ->
         UserModel(
            id = UUID.randomUUID().toString(),
            name = index.toString(),
         )
      }
   }
}