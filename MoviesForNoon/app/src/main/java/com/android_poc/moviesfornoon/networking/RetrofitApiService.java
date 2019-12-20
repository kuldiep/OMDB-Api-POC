package com.android_poc.moviesfornoon.networking;

import com.android_poc.moviesfornoon.pojo.MovieRespTO;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApiService {

    @GET(".")
    Single<MovieRespTO> getShowOrMovies(@Query("s") String searchQuery);
}
