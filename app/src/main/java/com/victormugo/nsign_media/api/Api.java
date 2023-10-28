package com.victormugo.nsign_media.api;

import com.victormugo.nsign_media.activities.Core;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {

    @GET(Core.FILE_NAME)
    Call<ResponseBody> loadFile();
}
