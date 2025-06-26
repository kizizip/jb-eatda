package com.example.jbeatda.data.repository

import com.example.jbeatda.data.remote.SampleService
import javax.inject.Inject

class SampleRepository @Inject constructor(
    private val sampleService: SampleService
){

}