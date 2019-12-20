package com.android_poc.moviesfornoon.ui;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android_poc.moviesfornoon.R;
import com.android_poc.moviesfornoon.networking.RetrofitApiClient;
import com.android_poc.moviesfornoon.networking.RetrofitApiService;
import com.android_poc.moviesfornoon.pojo.MovieRespTO;
import com.android_poc.moviesfornoon.utils.AppConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /*RetrofitApiService retrofitApiService = RetrofitApiClient.getInstance().create(RetrofitApiService.class);
        retrofitApiService.getShowOrMovies("batman").enqueue(new Callback<MovieRespTO>() {
            @Override
            public void onResponse(Call<MovieRespTO> call, Response<MovieRespTO> Response) {
                Log.d(AppConstants.Companion.getLOG(), "onResponse: resp is = "+Response.body());
            }

            @Override
            public void onFailure(Call<MovieRespTO> call, Throwable t) {

            }
        });*/
    }
}
