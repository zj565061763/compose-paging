package com.sd.demo.compose.paging.source

import com.sd.demo.compose.paging.UserModel
import com.sd.lib.compose.paging.FIntPagingSource
import kotlinx.coroutines.delay
import java.io.IOException

class ErrorUserPagingSource : FIntPagingSource<UserModel>() {
   override suspend fun loadImpl(params: LoadParams<Int>, key: Int): List<UserModel> {
      delay(1_000)
      throw IOException("load user error")
   }
}