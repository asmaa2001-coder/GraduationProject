package com.example.lazyeye.exercies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.example.lazyeye.R
import com.example.traningforproject.Data
import com.example.traningforproject.ExerciseDB
import com.example.traningforproject.PageAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class ExerciseContent : Fragment() {
    lateinit var dotIndicator: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_content , container , false)
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        val bnv: BottomNavigationView = requireActivity().findViewById(R.id.my_bottom_navigation)
        val pager2: ViewPager2 = view.findViewById(R.id.viewpager2)
        dotIndicator = view.findViewById(R.id.dot_indiciat)!!
        bnv.visibility = View.INVISIBLE

        val items =arguments?.getSerializable("items") as List<Data>
        val tableName=arguments?.getString("name")

        /*
        for db of exercise
         */

        val db: ExerciseDB? = ExerciseDB.getInstance(requireActivity())
        val dataDao = db?.DataDao()
        if(dataDao?.getAllExercise()?.size!=0)dataDao?.delete(dataDao.getAllExercise())
        dataDao?.addAllExercise(items)
        val exercises: List<Data> =dataDao?.getAllExercise()!!
        /*
        for viewing data in vp2
         */
        val myAdapter = PageAdapter(exercises,requireActivity(),R.layout.view_pager_items)
        pager2.adapter = myAdapter
        pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDot(position)
            }

        })
        /////////////
        createDot(exercises.size)
        val backButton: Button = view.findViewById(R.id.back)
        backButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.exerciseFragment)
        }

        val nextButton: Button = view.findViewById(R.id.next)
        nextButton.setOnClickListener {
            pager2.currentItem=pager2.currentItem+1

        }
    }
    /*
    for dot update
     */

    private fun createDot(size: Int) {
        for (i in 0 until size) {
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.dot_selected_state)
            dotIndicator.addView(dot)
        }
    }

    fun updateDot(position: Int) {
        for (i in 0 until dotIndicator.childCount) {
            val dot = dotIndicator.getChildAt(i) as ImageView
            dot.isSelected = i == position
        }
    }

}