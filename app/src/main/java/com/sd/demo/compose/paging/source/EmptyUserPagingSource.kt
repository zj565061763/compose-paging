package com.sd.demo.compose.paging.source

import com.sd.demo.compose.paging.UserModel
import com.sd.lib.compose.paging.FIntPagingSource
import kotlinx.coroutines.delay

class EmptyUserPagingSource : FIntPagingSource<UserModel>() {
    override suspend fun loadImpl(params: LoadParams<Int>, key: Int): List<UserModel> {
        delay(1_000)
        return emptyList()
    }
}