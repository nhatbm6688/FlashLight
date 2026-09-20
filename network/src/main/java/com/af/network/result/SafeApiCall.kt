package com.af.network.result

import com.af.network.model.BaseResponse
import com.google.gson.JsonParser

suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) ApiResult.Success(body)
            else ApiResult.Error(response.code(), "Empty response body")
        } else {
            ApiResult.Error(response.code(), response.message())
        }
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}

suspend fun <T> safeBaseApiCallWithRefresh(
    onRefresh: suspend () -> Boolean,
    call: suspend () -> retrofit2.Response<BaseResponse<T>>
): ApiResult<T> {
    val result = safeBaseApiCall(call)
    if (result is ApiResult.Error &&
        (result.code == NetworkErrorCode.INVALID_TOKEN_CODE || result.code == NetworkErrorCode.TOKEN_NOT_FOUND_CODE)
    ) {
        val refreshed = try { onRefresh() } catch (_: Exception) { false }
        if (refreshed) return safeBaseApiCall(call)
    }
    return result
}

suspend fun <T> safeBaseApiCall(call: suspend () -> retrofit2.Response<BaseResponse<T>>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            when {
                body == null -> ApiResult.Error(response.code(), "Empty response body")
                body.success == 1 -> ApiResult.Success(body.data!!)
                else -> ApiResult.Error(
                    code = body.statusCode ?: response.code(),
                    message = body.message ?: "Unknown error",
                    statusCode = body.statusCode
                )
            }
        } else {
            val json = try {
                response.errorBody()?.string()
            } catch (_: Exception) {
                null
            }
            val errorObj = try {
                if (json != null) JsonParser.parseString(json).asJsonObject else null
            } catch (_: Exception) {
                null
            }
            val errorMessage = errorObj?.get("message")?.takeIf { !it.isJsonNull }?.asString ?: response.message()
            val errorStatusCode = errorObj?.get("statusCode")?.takeIf { !it.isJsonNull }?.asInt
            ApiResult.Error(
                code = errorStatusCode ?: response.code(),
                message = errorMessage,
                statusCode = errorStatusCode
            )
        }
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}
