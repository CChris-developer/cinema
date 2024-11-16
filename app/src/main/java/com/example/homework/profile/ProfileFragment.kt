package com.example.homework.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.api.Consts.BUNDLE_KEY
import com.example.homework.api.Consts.REQUEST_KEY
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Utils
import com.example.homework.api.Utils.onMovieItemClick
import com.example.homework.databinding.FragmentProfileBinding
import com.example.homework.db.App
import com.example.homework.models.Movie
import com.example.homework.movies.MovieAdapter
import com.example.homework.movies.ClearMovieHistoryAdapter
import com.example.homework.movies.NewCollectionDialogFragment
import com.example.homework.viewmodel.MovieViewModel
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!
    private val viewModelDB: MovieViewModel by viewModels()

    private val movieAdapter1 = MovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@ProfileFragment,
            R.id.action_profileFragment_to_movieFragment2
        )
    }
    private val movieAdapter2 = MovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@ProfileFragment,
            R.id.action_profileFragment_to_movieFragment2
        )
    }
    private val clearHistoryAdapter1 = ClearMovieHistoryAdapter { clearHistory("viewed") }
    private val concatAdapter1 = ConcatAdapter(movieAdapter1)
    private val clearHistoryAdapter2 = ClearMovieHistoryAdapter { clearHistory("interested") }
    private val concatAdapter2 = ConcatAdapter(movieAdapter2)
    private var viewedMovieList = mutableListOf<Movie>()
    private var interestedMovieList = mutableListOf<Movie>()

    private fun clearedState(
        historyOf: String,
        list: List<Movie>,
        progressBar: ProgressBar,
        moviesCount: TextView,
        moreMovies: ImageView,
        concatAdapter: ConcatAdapter,
        movieAdapter: MovieAdapter,
        clearHistoryAdapter: ClearMovieHistoryAdapter,
        noMovies: TextView
    ) {
        progressBar.visibility = View.VISIBLE
        moviesCount.visibility = View.GONE
        moreMovies.visibility = View.GONE
        concatAdapter.removeAdapter(clearHistoryAdapter)
        movieAdapter.setData(emptyList())
        viewModelDB.clearMoviesHistory(list, historyOf)
        progressBar.visibility = View.GONE
        noMovies.visibility = View.VISIBLE
    }

    private fun clearHistory(historyOf: String) {
        if (historyOf == "viewed")
            clearedState(
                historyOf,
                viewedMovieList,
                binding.viewedProgressBar,
                binding.viewedMoviesCount,
                binding.moreMovies,
                concatAdapter1,
                movieAdapter1,
                clearHistoryAdapter1,
                binding.noViewedMoviesTextview
            )
        else {
            clearedState(
                historyOf,
                interestedMovieList,
                binding.interestedProgressBar,
                binding.interestingCount,
                binding.moreInteresting,
                concatAdapter2,
                movieAdapter2,
                clearHistoryAdapter2,
                binding.noInterestedMoviesTextview
            )
        }
    }

    private fun loadDataToRecycle(
        movieList: List<Movie>,
        concatAdapter: ConcatAdapter,
        clearHistoryAdapter: ClearMovieHistoryAdapter,
        movieAdapter: MovieAdapter,
        buttonCount: Button,
        textViewMore: ImageView,
        str: String,
        textViewNoData: TextView,
        recycle: RecyclerView,
        progressBar: ProgressBar
    ) {
        if (movieList.isNotEmpty()) {
            concatAdapter.addAdapter(clearHistoryAdapter)
            if (movieList.size > 6) {
                movieAdapter.areViewedMovies = true
                buttonCount.visibility = View.VISIBLE
                textViewMore.visibility = View.VISIBLE
                buttonCount.text = movieList.size.toString()
                buttonCount.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(ARG_PARAM1, str)
                    }
                    findNavController().navigate(
                        R.id.action_profileFragment_to_moviesFromProfileFragment,
                        args = bundle
                    )
                }
            } else {
                movieAdapter.areViewedMovies = false
                buttonCount.visibility = View.GONE
                textViewMore.visibility = View.GONE
            }
            textViewNoData.visibility = View.GONE
            recycle.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            movieAdapter.setData(movieList)
        } else {
            textViewNoData.visibility = View.VISIBLE
            recycle.visibility = View.GONE
            progressBar.visibility = View.GONE
            buttonCount.visibility = View.GONE
            textViewMore.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater)
        Utils.fragmentsList = emptyList<String>().toMutableList()
        Utils.movieTransitionsCount = 0
        binding.viewedMoviesCount.visibility = View.GONE
        binding.moreMovies.visibility = View.GONE
        binding.viewedProgressBar.visibility = View.VISIBLE

        binding.interestingCount.visibility = View.GONE
        binding.moreInteresting.visibility = View.GONE
        binding.interestedProgressBar.visibility = View.VISIBLE

        binding.interestingRecycle.adapter = concatAdapter2
        binding.viewedRecycle.adapter = concatAdapter1
        viewedMovieList = viewModelDB.getViewedMoviesInfo()
        interestedMovieList = viewModelDB.getInterestedMovies()

        loadDataToRecycle(
            viewedMovieList,
            concatAdapter1,
            clearHistoryAdapter1,
            movieAdapter1,
            binding.viewedMoviesCount,
            binding.moreMovies,
            getString(R.string.header_of_viewed_movies),
            binding.noViewedMoviesTextview,
            binding.viewedRecycle,
            binding.viewedProgressBar
        )
        loadDataToRecycle(
            interestedMovieList,
            concatAdapter2,
            clearHistoryAdapter2,
            movieAdapter2,
            binding.interestingCount,
            binding.moreInteresting,
            getString(R.string.header_of_interested_movies),
            binding.noInterestedMoviesTextview,
            binding.interestingRecycle,
            binding.interestedProgressBar
        )
        val favouriteMoviesCount = viewModelDB.getMoviesCountInCollection("Любимые")
        binding.favouriteCount.text = favouriteMoviesCount.toString()
        val wantToSeeMoviesCount = viewModelDB.getMoviesCountInCollection("Хочу посмотреть")
        binding.wantToSeeCount.text = wantToSeeMoviesCount.toString()
        binding.favouriteLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ARG_PARAM1, binding.favouriteTextview.text.toString())
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_moviesFromProfileFragment,
                args = bundle
            )
        }
        binding.wantToSeeLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ARG_PARAM1, binding.wantToSeeTextview.text.toString())
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_moviesFromProfileFragment,
                args = bundle
            )
        }

        val collectionsList = viewModelDB.testGetAllFromCollection()
        if (collectionsList.isNotEmpty()) {
            collectionsList.forEach {
                createPersonCollectionView(
                    it.collection,
                    viewModelDB.getMoviesCountInCollection(it.collection),
                    it.profileCardId,
                    it.profileLinearLayoutId,
                    it.profileImageButtonId,
                    it.profileTextView1Id,
                    it.profileTextView2Id
                )
            }
        }

        binding.newCollectionButton.setOnClickListener {
            NewCollectionDialogFragment()
                .show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
        }

        childFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY)!!.trim()
            val regex = "^[A-Za-z-А-Яа-я ]*$".toRegex()
            if (!regex.matches(result) || result == "")
                ShowAlert(getString(R.string.collection_not_added)).show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
            else {
                lifecycleScope.launch {
                    var mapSize = viewModelDB.getCollectionSize()
                    Log.d("mapSize", mapSize.toString())
                    var linearLayout = 3
                    var imageButton = 5
                    var textView1 = 7
                    var textView2 = 11
                    var card = 13
                    if (viewModelDB.getCollection(result).isEmpty()) {
                        if (mapSize % 10 == 0)
                            mapSize += 1
                        if (mapSize != 0) {
                            linearLayout = linearLayout * 2 * mapSize
                            imageButton = imageButton * 2 * mapSize
                            textView1 = textView1 * 2 * mapSize
                            textView2 = textView2 * 2 * mapSize
                            card = card * 2 * mapSize
                        }
                        viewModelDB.addCollection(
                            result,
                            mapSize,
                            mapSize * 10,
                            card,
                            linearLayout,
                            imageButton,
                            textView1,
                            textView2
                        )
                        createPersonCollectionView(
                            result,
                            0,
                            card,
                            linearLayout,
                            imageButton,
                            textView1,
                            textView2
                        )
                    } else
                        Toast.makeText(
                            requireContext(),
                            R.string.collection_exists,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                }
            }
        }
        return binding.root
    }

    private fun createPersonCollectionView(
        collection: String,
        moviesCount: Int,
        cardId: Int,
        linearLayoutId: Int,
        imageButtonId: Int,
        textView1Id: Int,
        textView2Id: Int
    ) {
        binding.gridProgressBar.visibility = View.GONE
        val card = MaterialCardView(context)
        card.id = View.generateViewId()
        val cardSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 170f, resources.displayMetrics
        ).toInt()

        val textView2Width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 35f, resources.displayMetrics
        ).toInt()

        val textView2Height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics
        ).toInt()

        val strokeWidthCard = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics
        ).toInt()

        val cornerRadiusCard = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
        ).toInt()

        val topMargin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics
        ).toInt()

        val endMargin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
        ).toInt()

        val topMarginLinearLayout = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 60f, resources.displayMetrics
        ).toInt()

        val verticalMarginTextView = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics
        ).toInt()

        val marginParams = GridLayout.LayoutParams()
        marginParams.height = cardSize
        marginParams.width = cardSize
        if (binding.collectionGridLayout.childCount % 2 == 0)
            marginParams.setGravity(Gravity.LEFT)
        else
            marginParams.setGravity(Gravity.RIGHT)
        marginParams.setMargins(0, topMargin, 0, 0)

        card.layoutParams = marginParams
        card.strokeWidth = strokeWidthCard
        card.id = cardId
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled)// enabled
        )
        val colors = intArrayOf(
            R.color.black
        )

        val myList = ColorStateList(states, colors)
        card.setStrokeColor(myList)
        card.radius = cornerRadiusCard.toFloat()

        val imageButton = ImageButton(context)
        val layoutParamsImageButton = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParamsImageButton.gravity = Gravity.RIGHT
        layoutParamsImageButton.marginEnd = endMargin
        imageButton.layoutParams = layoutParamsImageButton
        imageButton.setImageResource(R.drawable.close_icon)
        imageButton.setBackgroundColor(resources.getColor(R.color.white))
        imageButton.id = imageButtonId
        card.addView(imageButton)

        val linearLayout = LinearLayout(context)
        val layoutParamsLinearLayout = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.VERTICAL
        layoutParamsLinearLayout.gravity = Gravity.CENTER_HORIZONTAL
        layoutParamsLinearLayout.topMargin = topMarginLinearLayout
        linearLayout.layoutParams = layoutParamsLinearLayout
        linearLayout.gravity = Gravity.CENTER
        linearLayout.background = resources.getDrawable(R.drawable.search_settings_background)
        linearLayout.id = linearLayoutId
        card.addView(linearLayout)

        val imageView = ImageView(context)
        val layoutParamsImageView = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageView.layoutParams = layoutParamsImageView
        imageView.setImageResource(R.drawable.person_collection)
        linearLayout.addView(imageView)

        val textView1 = TextView(context)
        val layoutParamsTextView1 = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParamsTextView1.setMargins(0, verticalMarginTextView, 0, verticalMarginTextView)
        textView1.layoutParams = layoutParamsTextView1
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        textView1.id = textView1Id

        linearLayout.addView(textView1)

        val textView2 = TextView(context)
        val layoutParamsTextView2 = LinearLayout.LayoutParams(textView2Width, textView2Height)
        layoutParamsTextView2.topMargin = verticalMarginTextView
        textView2.layoutParams = layoutParamsTextView2
        textView2.background = resources.getDrawable(R.drawable.textview_rating)
        textView2.id = textView2Id
        textView2.setTextColor(resources.getColor(R.color.white))
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        textView2.gravity = Gravity.CENTER

        linearLayout.addView(textView2)

        binding.collectionGridLayout.addView(card)
        binding.collectionGridLayout.findViewById<TextView>(textView1Id).text = collection
        binding.collectionGridLayout.findViewById<TextView>(textView2Id).text =
            moviesCount.toString()
        binding.collectionGridLayout.findViewById<ImageButton>(imageButtonId).setOnClickListener {
            binding.gridProgressBar.visibility = View.VISIBLE
            viewModelDB.deleteCollection(collection)
            binding.gridProgressBar.visibility = View.GONE
            binding.collectionGridLayout.removeView(binding.collectionGridLayout.findViewById(cardId))
        }
        binding.collectionGridLayout.findViewById<LinearLayout>(linearLayoutId).setOnClickListener {
            val bundle = Bundle().apply {
                putString(
                    ARG_PARAM1,
                    binding.collectionGridLayout.findViewById<TextView>(textView1Id).text.toString()
                )
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_moviesFromProfileFragment,
                args = bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}