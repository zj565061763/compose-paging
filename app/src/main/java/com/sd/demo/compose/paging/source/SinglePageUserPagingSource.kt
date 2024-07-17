package com.sd.demo.compose.paging.source

import com.sd.demo.compose.paging.UserModel
import com.sd.lib.compose.paging.FIntPagingSource
import java.util.UUID

class SinglePageUserPagingSource : FIntPagingSource<UserModel>() {
    override suspend fun loadImpl(params: LoadParams<Int>, key: Int): List<UserModel> {
        return if (key == 1) {
            List(20) { index ->
                UserModel(
                    id = index.toString(),
                    name = UUID.randomUUID().toString(),
                )
            }
        } else {
            emptyList()
        }
    }
}