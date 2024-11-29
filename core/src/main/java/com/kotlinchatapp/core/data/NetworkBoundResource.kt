package com.kotlinchatapp.core.data

import com.kotlinchatapp.core.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

abstract class NetworkBoundResource<ResultType, InspectType, RequestType> {
    private var result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        prepare()
        val dbInspect = loadFromDBFirst().first()
        val dbSource = loadFromDB().first()
        val isFetch = shouldFetch(dbSource, dbInspect)
        if (isFetch) {
            prepareForFetch(dbSource, dbInspect)
            emit(Resource.Loading())
            when (val apiResponse = createCall(dbSource, dbInspect).first()) {
                is ApiResponse.Success -> {
                    saveCallResult((apiResponse.data))
                    emitAll(loadFromDB().map {
                        Resource.Success(it)
                    })
                }
                is ApiResponse.Empty -> {
                    emitAll(loadFromDB().map {
                        Resource.Success(it)
                    })
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    emit(Resource.Error(apiResponse.errorMessage, dbSource))
                }
            }
        } else {
            emitAll(loadFromDB().map {
                Resource.Success(it)
            })
        }
    }

    protected open suspend fun prepare(){}
    protected open fun onFetchFailed() {}
    protected abstract fun loadFromDBFirst(): Flow<InspectType>
    protected abstract fun loadFromDB(): Flow<ResultType>
    protected abstract fun shouldFetch(data: ResultType, inspect: InspectType): Boolean
    protected open suspend fun prepareForFetch(data: ResultType, inspect: InspectType){}
    protected abstract fun createCall(data: ResultType, inspect: InspectType): Flow<ApiResponse<RequestType>>
    protected abstract suspend fun saveCallResult(data: RequestType)
    fun asFlow(): Flow<Resource<ResultType>> = result
}