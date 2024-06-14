package com.example.lazyeye.camera

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.lazyeye.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class Camera : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        val nav :BottomNavigationView =requireActivity().findViewById(R.id.my_bottom_navigation)
        nav.visibility=View.VISIBLE
        val bt : Button =view.findViewById(R.id.take_photo)
        bt.setOnClickListener {
Navigation.findNavController(view).navigate(R.id.action_cameraFragment_to_detectDiseasse)
            nav.visibility = View.INVISIBLE


        }
    }
}