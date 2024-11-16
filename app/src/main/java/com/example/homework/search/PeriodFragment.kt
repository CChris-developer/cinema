package com.example.homework.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.FRAGMENT_NAME
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Consts.YEAR_FROM
import com.example.homework.api.Consts.YEAR_TO
import com.example.homework.api.Consts.periodList
import com.example.homework.databinding.FragmentPeriodBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class PeriodFragment : Fragment() {

    private var selectedYear1 = 0
    private var selectedYear2 = 0
    private lateinit var selectedRadioBtn1: RadioButton
    private lateinit var selectedRadioGr1: RadioGroup
    private var _binding: FragmentPeriodBinding? = null
    private val binding: FragmentPeriodBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeriodBinding.inflate(inflater)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility =
            View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            selectedYear1 = it.getInt(YEAR_FROM)
            selectedYear2 = it.getInt(YEAR_TO)
        }
        var digit1: Int
        var digit2: Int
        val grid1List = listOf(
            binding.grid1RadioBtn1,
            binding.grid1RadioBtn2,
            binding.grid1RadioBtn3,
            binding.grid1RadioBtn4,
            binding.grid1RadioBtn5,
            binding.grid1RadioBtn6,
            binding.grid1RadioBtn7,
            binding.grid1RadioBtn8,
            binding.grid1RadioBtn9,
            binding.grid1RadioBtn10,
            binding.grid1RadioBtn11,
            binding.grid1RadioBtn12
        )
        val grid2List = listOf(
            binding.grid2RadioBtn1,
            binding.grid2RadioBtn2,
            binding.grid2RadioBtn3,
            binding.grid2RadioBtn4,
            binding.grid2RadioBtn5,
            binding.grid2RadioBtn6,
            binding.grid2RadioBtn7,
            binding.grid2RadioBtn8,
            binding.grid2RadioBtn9,
            binding.grid2RadioBtn10,
            binding.grid2RadioBtn11,
            binding.grid2RadioBtn12
        )

        if (selectedYear1 == 0 && selectedYear2 == 0) {
            binding.periodTextview1.text = getString(R.string.years, periodList[0][0], periodList[0][1])//"${periodList[0][0]}-${periodList[0][1]}"
            binding.periodTextview2.text = getString(R.string.years, periodList[0][0], periodList[0][1])//"${periodList[0][0]}-${periodList[0][1]}"
            digit1 = periodList[0][0]
            digit2 = periodList[0][0]
        } else if (selectedYear1 == 1998 && selectedYear2 <= 2009) {
            binding.periodTextview1.text = getString(R.string.years, periodList[1][0], periodList[1][1])//"${periodList[1][0]}-${periodList[1][1]}"
            binding.periodTextview2.text = getString(R.string.years, periodList[1][0], periodList[1][1])//"${periodList[1][0]}-${periodList[1][1]}"
            digit1 = periodList[1][0]
            digit2 = periodList[1][0]
        } else {
            val grid1 = getGrid(selectedYear1)
            val grid2 = getGrid(selectedYear2)
            if (grid1[0] == grid2[0] && grid1[1] == grid2[1]) {
                binding.periodTextview1.text = getString(R.string.years, grid1[0], grid1[1])//"${grid1[0]}-${grid1[1]}"
                binding.periodTextview2.text = getString(R.string.years, grid1[0], grid1[1])//"${grid1[0]}-${grid1[1]}"
                digit1 = grid1[0]
                digit2 = grid1[0]
            } else {
                binding.periodTextview1.text = getString(R.string.years, grid1[0], grid1[1])//"${grid1[0]}-${grid1[1]}"
                binding.periodTextview2.text = getString(R.string.years, grid2[0], grid2[1])//"${grid2[0]}-${grid2[1]}"
                digit1 = grid1[0]
                digit2 = grid2[0]
            }
        }
        var i = 0
        grid1List.forEach {
            it.text = digit1.toString()
            if (digit1 == selectedYear1)
                it.isChecked = true
            i++
            digit1 += 1
        }
        i = 0
        grid2List.forEach {
            it.text = digit2.toString()
            if (digit2 == selectedYear2)
                it.isChecked = true
            i++
            digit2 += 1
        }
        buttonForwardClick(binding.buttonForward1, binding.periodTextview1, grid1List)
        buttonForwardClick(binding.buttonForward2, binding.periodTextview2, grid2List)
        buttonBackClick(binding.buttonBack1, binding.periodTextview1, grid1List)
        buttonBackClick(binding.buttonBack2, binding.periodTextview2, grid2List)
        radioButtonCheckedChange(
            binding.grid1RadioGr1,
            binding.grid1RadioGr2,
            binding.grid1RadioGr3,
            binding.grid1RadioGr4,
            1
        )
        radioButtonCheckedChange(
            binding.grid1RadioGr2,
            binding.grid1RadioGr1,
            binding.grid1RadioGr3,
            binding.grid1RadioGr4,
            1
        )
        radioButtonCheckedChange(
            binding.grid1RadioGr3,
            binding.grid1RadioGr1,
            binding.grid1RadioGr2,
            binding.grid1RadioGr4,
            1
        )
        radioButtonCheckedChange(
            binding.grid1RadioGr4,
            binding.grid1RadioGr1,
            binding.grid1RadioGr2,
            binding.grid1RadioGr3,
            1
        )
        radioButtonCheckedChange(
            binding.grid2RadioGr1,
            binding.grid2RadioGr2,
            binding.grid2RadioGr3,
            binding.grid2RadioGr4,
            2
        )
        radioButtonCheckedChange(
            binding.grid2RadioGr2,
            binding.grid2RadioGr1,
            binding.grid2RadioGr3,
            binding.grid2RadioGr4,
            2
        )
        radioButtonCheckedChange(
            binding.grid2RadioGr3,
            binding.grid2RadioGr1,
            binding.grid2RadioGr2,
            binding.grid2RadioGr4,
            2
        )
        radioButtonCheckedChange(
            binding.grid2RadioGr4,
            binding.grid2RadioGr1,
            binding.grid2RadioGr2,
            binding.grid2RadioGr3,
            2
        )

        binding.submitButton.setOnClickListener {
            if (selectedYear1 > selectedYear2)
                ShowAlert(getString(R.string.period_is_wrong)).show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
            else {
                val periodBundle = Bundle().apply {
                    putInt(YEAR_FROM, selectedYear1)
                    putInt(YEAR_TO, selectedYear2)
                    putString(FRAGMENT_NAME, "fromPeriodFragment")
                }
                findNavController().navigate(
                    R.id.action_periodFragment_to_searchSettingsFragment,
                    periodBundle
                )
            }
        }
        binding.back.setOnClickListener {
            val periodBundle = Bundle().apply {
               putString(FRAGMENT_NAME, "")
            }
            findNavController().navigate(
                R.id.action_periodFragment_to_searchSettingsFragment,
                periodBundle
            )
        }
    }

    private fun radioButtonCheckedChange(
        radioGroupChecked: RadioGroup,
        radioGroup1: RadioGroup,
        radioGroup2: RadioGroup,
        radioGroup3: RadioGroup,
        grid: Int
    ) {
        radioGroupChecked.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                if (radioGroupChecked.findViewById<RadioButton>(checkedId).isChecked) {
                    if (grid == 1)
                        selectedYear1 =
                            radioGroupChecked.findViewById<RadioButton>(checkedId).text.toString()
                                .toInt()
                    else if (grid == 2)
                        selectedYear2 =
                            radioGroupChecked.findViewById<RadioButton>(checkedId).text.toString()
                                .toInt()
                    selectedRadioBtn1 = radioGroupChecked.findViewById(checkedId)
                    selectedRadioGr1 = radioGroupChecked
                    if (radioGroup1.checkedRadioButtonId != -1) {
                        radioGroup1.clearCheck()

                    }
                    if (radioGroup2.checkedRadioButtonId != -1) {
                        radioGroup2.clearCheck()

                    }
                    if (radioGroup3.checkedRadioButtonId != -1) {
                        radioGroup3.clearCheck()

                    }
                }
            }
        }
    }

    private fun buttonForwardClick(
        buttonForward: ImageButton,
        periodTextView: TextView,
        gridList: List<RadioButton>
    ) {
        buttonForward.setOnClickListener {
            val periodDigitList = periodTextView.text.toString().split("-").toList()
            for (index in periodList.indices) {
                if (periodList[index][0] == periodDigitList[0].toInt() && periodList[index][1] == periodDigitList[1].toInt()) {
                    if (index < (periodList.size - 1)) {
                        var i = 0
                        val nextPeriod = periodList[index + 1]
                        periodTextView.text = getString(R.string.years, nextPeriod[0], nextPeriod[1])//"${nextPeriod[0]}-${nextPeriod[1]}"
                        var changingDigit = nextPeriod[0]
                        gridList.forEach {
                            it.text = changingDigit.toString()
                            it.isChecked = false
                            if (::selectedRadioBtn1.isInitialized && ::selectedRadioGr1.isInitialized) {
                                if (it == selectedRadioBtn1 && it.text.toString() == selectedYear1.toString()) {
                                    selectedRadioGr1.clearCheck()
                                    selectedRadioBtn1.isChecked = true
                                }
                            }
                            i++
                            changingDigit += 1
                        }
                    }
                }
            }
        }
    }

    private fun buttonBackClick(
        buttonBack: ImageButton,
        periodTextView: TextView,
        gridList: List<RadioButton>
    ) {
        buttonBack.setOnClickListener {
            val periodDigitList = periodTextView.text.toString().split("-")
            for (index in periodList.indices) {
                if (periodList[index][0] == periodDigitList[0].toInt() && periodList[index][1] == periodDigitList[1].toInt()) {
                    if ((index - 1) >= 0) {
                        var i = 0
                        val lastPeriod = periodList[index - 1]
                        periodTextView.text = getString(R.string.years, lastPeriod[0], lastPeriod[1])//"${lastPeriod[0]}-${lastPeriod[1]}"
                        var changingDigit = lastPeriod[0]
                        gridList.forEach {
                            it.text = changingDigit.toString()
                            it.isChecked = false
                            if (::selectedRadioBtn1.isInitialized && ::selectedRadioGr1.isInitialized) {
                                if (it == selectedRadioBtn1 && it.text.toString() == selectedYear1.toString()) {
                                    selectedRadioGr1.clearCheck()
                                    selectedRadioBtn1.isChecked = true
                                }
                            }
                            i++
                            changingDigit += 1
                        }
                    }
                }
            }
        }
    }

    private fun getGrid(selectedYear: Int): List<Int> {
        var years = listOf<Int>()
        for (i in 0..<periodList.size) {
            if (selectedYear <= periodList[i][1]) {
                years = periodList[i]
                break
            }
        }
        return years
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}