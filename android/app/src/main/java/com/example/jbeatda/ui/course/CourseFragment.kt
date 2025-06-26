package com.example.jbeatda.ui.course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jbeatda.R
import com.example.jbeatda.base.BaseFragment
import com.example.jbeatda.databinding.FragmentCourseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseFragment : BaseFragment<FragmentCourseBinding>(
    FragmentCourseBinding::bind,
    R.layout.fragment_course
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}