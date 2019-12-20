package com.android_poc.moviesfornoon.ui


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android_poc.moviesfornoon.R
import com.android_poc.moviesfornoon.databinding.ActivityMainBinding
import com.android_poc.moviesfornoon.networking.RetrofitApiClient
import com.android_poc.moviesfornoon.networking.RetrofitApiService
import com.android_poc.moviesfornoon.pojo.MovieRespTO
import com.android_poc.moviesfornoon.pojo.Search
import com.android_poc.moviesfornoon.utils.AppConstants
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null
    var disposable: CompositeDisposable = CompositeDisposable()
    var publishSubject: PublishSubject<String> = PublishSubject.create()
    var retrofitApiService: RetrofitApiService? = null
    var movieShowListAdapter: MovieShowListAdapter? = null
    var movieResponseList: ArrayList<MovieRespTO> = ArrayList<MovieRespTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        movieShowListAdapter = MovieShowListAdapter(this, ArrayList<Search>())
        binding?.rvNewsList?.layoutManager = LinearLayoutManager(this)
        binding?.rvNewsList?.itemAnimator = DefaultItemAnimator()
        binding?.rvNewsList?.adapter = movieShowListAdapter
        retrofitApiService = RetrofitApiClient.getInstance().create(RetrofitApiService::class.java)


    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        CheckInternetTask(this,true).execute()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event:InternetEvent){
        Log.d(AppConstants.LOG,"event id is = "+event.eventId)
        if(event.eventId == InternetEvent.INTERNET_EVENT_ID){
            if(event.eventType == InternetEvent.INTERNET_EVENT_TYPE.AVAILABLE.ordinal){
                disposable.add(
                    publishSubject.debounce(300, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .switchMapSingle(object : Function<String, Single<MovieRespTO>> {
                            override fun apply(s: String): Single<MovieRespTO>? {
                                return retrofitApiService?.getShowOrMovies(s)?.subscribeOn(Schedulers.io())
                                    ?.observeOn(AndroidSchedulers.mainThread())
                            }
                        })
                        .subscribeWith(getMoviesObserver())
                )

                disposable.add(
                    RxTextView.textChangeEvents(binding?.inputSearch!!)
                        .skipInitialValue()
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(searchMoviesObservable())
                )

                disposable.add(getMoviesObserver())
                publishSubject.onNext("Batman")
            }
        }
    }

    fun getMoviesObserver(): DisposableObserver<MovieRespTO> {
        return object : DisposableObserver<MovieRespTO>() {
            override fun onNext(movieRespTO: MovieRespTO) {
                Log.d(AppConstants.LOG, "movie Response is = " + movieRespTO)
                if (movieRespTO.Search == null) {
                    binding?.rvNewsList?.visibility = View.GONE
                    binding?.tvNoResponse?.visibility = View.VISIBLE
                    binding?.tvNoResponse?.text = "No Result found"
                } else {
                    binding?.tvNoResponse?.visibility = View.GONE
                    binding?.rvNewsList?.visibility = View.VISIBLE
                    movieResponseList.clear()
                    movieResponseList.addAll((movieRespTO.Search as? Collection<MovieRespTO>)!!)
                    movieShowListAdapter?.searchList = (movieResponseList as? List<Search>)!!
                    movieShowListAdapter?.notifyDataSetChanged()
                }
            }

            override fun onError(e: Throwable) {
                Log.e(AppConstants.LOG, "Exception .. ", e)
            }

            override fun onComplete() {

            }
        }
    }


    private fun searchMoviesObservable(): DisposableObserver<TextViewTextChangeEvent> {
        return object : DisposableObserver<TextViewTextChangeEvent>() {

            override fun onNext(textViewTextChangeEvent: TextViewTextChangeEvent) {
                Log.d(AppConstants.LOG, "Search query: " + textViewTextChangeEvent.text());
                publishSubject.onNext(textViewTextChangeEvent.text().toString())
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {

            }
        }
    }

    class MovieShowListAdapter(val context: Context, var searchList: List<Search>) :
        RecyclerView.Adapter<MovieShowListAdapter.MovieShowListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieShowListViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.movie_item_row, parent, false)
            return MovieShowListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return searchList.size
        }


        override fun onBindViewHolder(holder: MovieShowListViewHolder, position: Int) {
            if (searchList.get(position).Title != null) {
                holder.name.text = searchList.get(position).Title
            }
            if (searchList.get(position).Poster != null) {
                Glide.with(context).load(searchList.get(position).Poster).into(holder.img)
            }
            if (searchList.get(position).Year != null) {
                holder.date.text = searchList.get(position).Year
            }
        }


        inner class MovieShowListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var img: ImageView = itemView.findViewById(R.id.img)
            var name = itemView.findViewById<TextView>(R.id.name)
            var date = itemView.findViewById<TextView>(R.id.date)

        }
    }
}





