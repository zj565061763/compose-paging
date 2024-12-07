package com.sd.lib.compose.paging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.CombinedLoadStates
import androidx.paging.compose.LazyPagingItems

/**
 *  根据[CombinedLoadStates.append]状态展示UI
 */
@Composable
fun FPagingAppend(
  items: LazyPagingItems<*>,
  modifier: Modifier = Modifier,
  stateLoading: @Composable () -> Unit = { StateLoading() },
  stateError: @Composable (Throwable) -> Unit = {
    StateError(
      items = items,
      text = stringResource(R.string.lib_compose_paging_state_append_load_failure),
    )
  },
  stateNoMoreData: @Composable () -> Unit = {
    Text(
      text = stringResource(R.string.lib_compose_paging_state_append_no_more_data),
      fontSize = 12.sp,
      color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
    )
  },
) {
  StateBox(modifier = modifier) {
    items.FUIStateAppend(
      stateLoading = stateLoading,
      stateError = stateError,
      stateNoMoreData = stateNoMoreData,
    )
  }
}

/**
 *  根据[CombinedLoadStates.prepend]状态展示UI
 */
@Composable
fun FPagingPrepend(
  items: LazyPagingItems<*>,
  modifier: Modifier = Modifier,
  stateLoading: @Composable () -> Unit = { StateLoading() },
  stateError: @Composable (Throwable) -> Unit = {
    StateError(
      items = items,
      text = stringResource(R.string.lib_compose_paging_state_prepend_load_failure)
    )
  },
) {
  StateBox(modifier = modifier) {
    items.FUIStatePrepend(
      stateLoading = stateLoading,
      stateError = stateError,
    )
  }
}

@Composable
private fun StateBox(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .heightIn(48.dp)
      .padding(5.dp),
    contentAlignment = Alignment.Center,
    content = content,
  )
}

@Composable
private fun StateLoading(
  modifier: Modifier = Modifier,
) {
  CircularProgressIndicator(
    modifier = modifier.size(24.dp),
    strokeWidth = 2.dp,
    color = MaterialTheme.colorScheme.onSurface
  )
}

@Composable
private fun StateError(
  modifier: Modifier = Modifier,
  items: LazyPagingItems<*>,
  text: String,
) {
  Text(
    text = text,
    fontSize = 12.sp,
    color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
    textAlign = TextAlign.Center,
    modifier = modifier
      .clickable { items.retry() }
      .defaultMinSize(60.dp, 30.dp)
      .padding(horizontal = 10.dp)
  )
}