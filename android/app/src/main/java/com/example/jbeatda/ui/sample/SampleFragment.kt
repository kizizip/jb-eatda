package com.example.jbeatda.ui.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.jbeatda.R
import com.example.jbeatda.base.BaseFragment
import com.example.jbeatda.databinding.FragmentSampleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SampleFragment : BaseFragment<FragmentSampleBinding>(
    FragmentSampleBinding::bind,
    R.layout.fragment_sample
) {
    private val viewModel: SampleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}