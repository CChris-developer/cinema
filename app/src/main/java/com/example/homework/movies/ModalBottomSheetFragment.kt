package com.example.homework.movies

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.BUNDLE_KEY
import com.example.homework.api.Consts.REQUEST_KEY
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.databinding.FragmentModalBottomSheetBinding
import com.example.homework.db.App
import com.example.homework.db.Collections
import com.example.homework.viewmodel.MovieViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModalBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentModalBottomSheetBinding
    private var listCollections = listOf<Collections>()
    private var listForNewLayout = mutableMapOf<Int, Int>()
    private val viewModelDB: MovieViewModel by viewModels()
    private var isNewLayoutReady = false
    var textview = 10
    private val iconsColorStates = ColorStateList(
        arrayOf(
            (intArrayOf(-android.R.attr.state_checked)),
            (intArrayOf(android.R.attr.state_checked))
        ),
        intArrayOf(R.color.purple_700, R.color.purple_700)
    )

    private fun newCollectionLayout(
        collection: String,
        checkboxId: Int,
        textViewId: Int,
        isNewCollection: Boolean
    ) {
        val margin40inDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics
        ).toInt()
        val margin24inDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics
        ).toInt()
        val padding5inDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics
        ).toInt()
        val linearLayout = binding.checkboxGroup
        val relativeLayout = RelativeLayout(requireContext())
        val textView = TextView(requireContext())
        textView.setTextSize(16F)
        val checkBox = CheckBox(requireContext())
        checkBox.setTextSize(16F)
        val layoutParamsCheckBox = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val layoutParamsTextView = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParamsCheckBox.setMargins(margin40inDp, 0, 0, 0)
        layoutParamsTextView.setMargins(0, 0, margin24inDp, 0)
        layoutParamsTextView.addRule(RelativeLayout.ALIGN_PARENT_END)
        checkBox.layoutParams = layoutParamsCheckBox
        checkBox.minHeight = 0
        checkBox.minWidth = 0
        if (isNewCollection)
            checkBox.isChecked = true
        checkBox.buttonTintList = iconsColorStates
        checkBox.id = checkboxId
        textView.id = textViewId
        textView.layoutParams = layoutParamsTextView
        textView.setPadding(0, padding5inDp, 0, padding5inDp)
        relativeLayout.addView(checkBox)
        relativeLayout.addView(textView)
        linearLayout.addView(relativeLayout)
        linearLayout.findViewById<CheckBox>(checkboxId).text = collection
        linearLayout.findViewById<TextView>(textViewId).text = "0"
        isNewLayoutReady = true
    }

    private fun fillCollectionsMap(
        list: List<String>,
        map: MutableMap<String, Int>,
        isStarted: Boolean
    ) {
        list.forEach {
            val currentListResult = it.split("&")
            currentListResult.forEach {
                if (it != "Любимые" && it != "Хочу посмотреть" && it != "Просмотренные") {
                    if (isStarted)
                        map[it] = 0
                    else
                        map[it] = map[it]!! + 1
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentModalBottomSheetBinding.bind(
            inflater.inflate(
                R.layout.fragment_modal_bottom_sheet,
                container
            )
        )
        val data = arguments?.getStringArrayList("list")
        val movieId = data!![5].toInt()
        viewModelDB.markedCollection(movieId)
        val checkingMoviesNumber = mapOf(
            binding.favouriteCheckbox to binding.favouriteMoviesNumber,
            binding.wantToSeeCheckbox to binding.wantToSeeMoviesNumber
        )
        val linearLayout = binding.checkboxGroup
        lifecycleScope.launch {
            listCollections = viewModelDB.testGetAllFromCollection()
            if (listCollections.isNotEmpty()) {
                listCollections.onEach { listCollectionItems ->
                    listForNewLayout[listCollectionItems.checkboxId] =
                        listCollectionItems.textviewId
                    newCollectionLayout(
                        listCollectionItems.collection,
                        listCollectionItems.checkboxId,
                        listCollectionItems.textviewId,
                        false
                    )
                }
            }
        }

        childFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY)!!.trim()
            isNewLayoutReady = false
            val regex = "^[A-Za-z-А-Яа-я ]*$".toRegex()
            if (!regex.matches(result) || result == "")
                ShowAlert(getString(R.string.collection_not_added)).show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
            else {
                lifecycleScope.launch {
                    var mapSize = viewModelDB.getCollectionSize()
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
                        viewModelDB.addToCollection(movieId, result)
                        newCollectionLayout(
                            result,
                            mapSize,
                            mapSize * 10,
                            true
                        )
                        listForNewLayout[mapSize] = mapSize * 10
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

        var isReadyList = true
        viewLifecycleOwner.lifecycleScope.launch {
            while (listForNewLayout.isEmpty() || listForNewLayout.size < listCollections.size) {
                delay(300)
                isReadyList = false
                if (viewModelDB.isListEmpty.value)
                    break
                if (listForNewLayout.size == listCollections.size)
                    isReadyList = true
            }
            if (isReadyList) {
                for ((key, value) in listForNewLayout) {
                    linearLayout.findViewById<CheckBox>(key).buttonTintList = iconsColorStates
                    lifecycleScope.launch {
                        viewModelDB.movieCollections.collect { name ->

                            if (name != null) {
                                Log.d(
                                    "Название чекбокса",
                                    key.toString()
                                )
                                if (name.contains(linearLayout.findViewById<CheckBox>(key).text))
                                    linearLayout.findViewById<CheckBox>(key).isChecked =
                                        true
                            }
                        }
                    }
                }
            }
        }
        var collectionsList = viewModelDB.testGetAllFromCollection()
        var lastResult = emptyList<String>()
        val currentCollectionsMap = mutableMapOf<String, Int>()
        val lastCollectionsMap = mutableMapOf<String, Int>()
        viewLifecycleOwner.lifecycleScope.launch {
            while (!isNewLayoutReady)
                delay(300)
            viewModelDB.listenCollection.collect { list ->
                var currentResult = list.filter { it != "" }
                currentResult = currentResult.filter { it != " " }
                fillCollectionsMap(currentResult, currentCollectionsMap, true)
                if (lastResult.isNotEmpty()) {
                    fillCollectionsMap(lastResult, lastCollectionsMap, true)
                }
                if (lastResult.isEmpty()) {
                    fillCollectionsMap(currentResult, currentCollectionsMap, false)
                    if (currentCollectionsMap.size > collectionsList.size)
                        collectionsList = viewModelDB.testGetAllFromCollection()
                    currentCollectionsMap.forEach { key, value ->
                        collectionsList.forEach {
                            if (it.collection == key) {
                                linearLayout.findViewById<TextView>(it.textviewId).text =
                                    value.toString()
                            }
                        }
                    }
                } else if (currentResult.size > lastResult.size) {
                    fillCollectionsMap(currentResult, currentCollectionsMap, false)
                    fillCollectionsMap(lastResult, lastCollectionsMap, false)
                    if (currentCollectionsMap.size > collectionsList.size)
                        collectionsList = viewModelDB.testGetAllFromCollection()
                    currentCollectionsMap.forEach { key, value ->
                        if (key != "Любимые" && key != "Хочу посмотреть" && key != "Просмотренные") {
                            if (lastCollectionsMap[key] != value) {
                                collectionsList.forEach {
                                    if (it.collection == key) {
                                        linearLayout.findViewById<TextView>(it.textviewId).text =
                                            value.toString()
                                    }
                                }
                            }
                        }
                    }
                } else if (currentResult.size < lastResult.size) {
                    fillCollectionsMap(currentResult, currentCollectionsMap, false)
                    fillCollectionsMap(lastResult, lastCollectionsMap, false)
                    lastCollectionsMap.forEach { key, value ->
                        if (key != "Любимые" && key != "Хочу посмотреть" && key != "Просмотренные") {
                            if (currentCollectionsMap[key] != value) {
                                collectionsList.forEach {
                                    if (it.collection == key) {
                                        linearLayout.findViewById<TextView>(it.textviewId).text =
                                            currentCollectionsMap[key].toString()
                                    }
                                }
                            }
                        }
                    }
                } else if (currentResult.size == lastResult.size) {
                    var i = 0
                    fillCollectionsMap(currentResult, currentCollectionsMap, false)
                    fillCollectionsMap(lastResult, lastCollectionsMap, false)
                    if (currentCollectionsMap.size > collectionsList.size)
                        collectionsList = viewModelDB.testGetAllFromCollection()
                    currentCollectionsMap.forEach { key, value ->
                        if (key != "Любимые" && key != "Хочу посмотреть" && key != "Просмотренные") {
                            if (lastCollectionsMap[key] != value) {
                                collectionsList.forEach {
                                    if (it.collection == key) {
                                        linearLayout.findViewById<TextView>(it.textviewId).text =
                                            value.toString()
                                    }
                                }
                            }
                        }
                    }
                }
                lastResult = currentResult
                collectionsList = viewModelDB.testGetAllFromCollection()
                collectionsList.forEach {
                    linearLayout.findViewById<CheckBox>(it.checkboxId)
                        .setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                viewModelDB.addToCollection(
                                    movieId,
                                    linearLayout.findViewById<CheckBox>(it.checkboxId).text.toString()
                                )
                            } else {
                                viewModelDB.deleteFromCollection(
                                    movieId,
                                    linearLayout.findViewById<CheckBox>(it.checkboxId).text.toString()
                                )
                            }
                        }
                }
            }
        }
        binding.rating.text = data!![0].toString()
        Glide
            .with(requireContext())
            .load(data!![1])
            .into(binding.poster)
        binding.movieName.text = data!![2].toString()
        binding.movieDateGenre.text = getString(
            R.string.year_genre_in_bottom_sheet,
            data!![3].toString(),
            data!![4].toString()
        )

        for ((key) in checkingMoviesNumber) {
            key.buttonTintList = iconsColorStates
            lifecycleScope.launch {
                viewModelDB.movieCollections.collect {
                    if (it != null) {
                        if (it.contains(key.text))
                            key.isChecked = true
                    }
                }
            }
            key.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModelDB.addToCollection(movieId, key.text.toString())
                } else {
                    viewModelDB.deleteFromCollection(movieId, key.text.toString())
                }
            }
        }
        for ((key, value) in checkingMoviesNumber) {
            lifecycleScope.launch {
                viewModelDB.observingMoviesNumber1(key.text.toString()).collect {
                    value.text = it.size.toString()
                }
            }
        }
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY)
            if (result != null) {
                Log.d("SET FRAGMENT LISTENER", result)
            } else
                Log.d("SET FRAGMENT LISTENER", "0")
        }
        binding.addToCollectionButton.setOnClickListener {
            binding.checkboxGroup.visibility = View.VISIBLE
        }
        binding.cross.setOnClickListener {
            dialog?.cancel()
        }
        binding.newCollectionButton.setOnClickListener {
            NewCollectionDialogFragment()
                .show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.cancel()
    }
}