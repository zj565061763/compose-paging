package com.sd.demo.compose.paging.source

import androidx.paging.PagingState
import com.sd.demo.compose.paging.UserModel
import com.sd.demo.compose.paging.logMsg
import com.sd.lib.compose.paging.FIntPagingSource
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.UUID

class UserPagingSource : FIntPagingSource<UserModel>() {

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

        val list = mutableListOf<UserModel>()
        repeat(20) { index ->
            list.add(
                UserModel(
                    id = UUID.randomUUID().toString(),
                    name = index.toString(),
                )
            )
        }
        return list
    }
}