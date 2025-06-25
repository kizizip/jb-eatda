package com.example.jbeatda.ui.sample

import androidx.lifecycle.ViewModel
import com.example.jbeatda.data.repository.SampleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val sampleRepository: SampleRepository
) : ViewModel() {

}