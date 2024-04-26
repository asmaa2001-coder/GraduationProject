package com.example.lazyeye

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.example.traningforproject.Data
import com.example.traningforproject.PageAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

// TODO: Rename parameter arguments, choose names that match
lateinit var dot: WormDotsIndicator
lateinit var pager2: ViewPager2
lateinit var next: Button
lateinit var skip: Button
lateinit var start: Button
lateinit var bnv: BottomNavigationView

class OnBoardScreen : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_on_board_screen , container , false)
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        callViews()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        var firstTime: Boolean? = sharedPref?.getBoolean("check" , false)
        val editor = sharedPref?.edit()
        ////////////check the first Time Usage
        if (firstTime == true) {
            Navigation.findNavController(view).navigate(R.id.action_onBoardScreen_to_cameraFragment)
        }
////////put the data
        val views = listOf(
            Data(
                1 ,
                "في هذا التطبيق سوف نتعرف على مجموعة من المزايا التي تساعد المستخدم على  التشخيص الأولي من خلال قراءة مجموعة من المقالات المتعلقة بأمراض العين." ,
                "onboarding_images/img1.jpg"
            ) ,
            Data(
                2 ,
                "يتيح هذا التطبيق إمكانية التعرف على الحالات الغير طبيعية للعين ولاسيما الحول من خلال استخدام الذكاء الاصطناعي" ,
                "onboarding_images/img2.jpg"
            ) ,
            Data(
                3 ,
                "بعد ذلك يتم إتاحة مجموعة من التمرينات التي تساعدك على استرخاء عضلات العين في حالات الإرهاق والتعب، وإجراء بعض التمارين الخاصة بالحول." ,
                "onboarding_images/imag4.jpg"
            )
        )
        val myAdapter = PageAdapter(views , requireActivity() , R.layout.view_pager_items)
        pager2.adapter = myAdapter
        dot.setViewPager2(pager2)

        pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 2) {
                    next.visibility = View.INVISIBLE
                    skip.visibility = View.INVISIBLE
                    start.visibility = View.VISIBLE

                    start.setOnClickListener {
                        editor?.putBoolean("check" , true)
                        editor?.apply()
                        Navigation.findNavController(view)
                                .navigate(R.id.action_onBoardScreen_to_cameraFragment)

                    }
                } else {
                    next.visibility = View.VISIBLE
                    skip.visibility = View.VISIBLE
                    start.visibility = View.INVISIBLE
                }
            }
        })
        bnv.visibility = View.INVISIBLE
        next.setOnClickListener {
            pager2.currentItem = pager2.currentItem + 1
        }
        skip.setOnClickListener()
        {
            editor?.putBoolean("check" , true)
            editor?.apply()
            Navigation.findNavController(view).navigate(R.id.action_onBoardScreen_to_cameraFragment)

        }
    }


    fun callViews() {
        dot = view?.findViewById(R.id.dot_ind)!!
        pager2 = view?.findViewById(R.id.viewpager3)!!
        skip = view?.findViewById(R.id.skip_btn)!!
        next = view?.findViewById(R.id.next_btn_pager)!!
        start = view?.findViewById(R.id.getStart_btn)!!
        bnv =
            requireActivity().findViewById<BottomNavigationView>(R.id.my_bottom_navigation)
    }

}


