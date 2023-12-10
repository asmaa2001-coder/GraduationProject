package com.example.lazyeye.articles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.Navigation
import com.example.lazyeye.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class ArticleContent : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_content , container , false)
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        //call views
        val wv = view.findViewById<WebView>(R.id.article_view)
        var bnv: BottomNavigationView = requireActivity().findViewById(R.id.my_bottom_navigation)
        val image: ImageView = view.findViewById(R.id.image_of_article)
        val btnNext: Button = view.findViewById(R.id.go_to_ex)
        val back: Button = view.findViewById(R.id.back_button)
 bnv.visibility=View.INVISIBLE
        //get the content
        val file = arguments?.getString("name")
        val image_r = arguments?.getInt("image")!!
        image.setImageResource(image_r)

        /*
        webView sitting
        To make the webView fit the view of android
        1-->using *useWideViewPort* / *loadWithOverviewMode*
         */
        wv.settings.builtInZoomControls = true
        wv.settings.setSupportZoom(true)
        wv.setInitialScale(1);
        wv.settings.useWideViewPort = true;
        wv.settings.loadWithOverviewMode = true;
        wv.settings.javaScriptEnabled = true
        wv.loadUrl("file:///android_asset/$file")
        btnNext.setOnClickListener {
            bnv.visibility = view.visibility
            Navigation.findNavController(view).navigate(R.id.exerciseFragment)
        }
        back.setOnClickListener {
            Navigation.findNavController(view)
                    .navigate(R.id.action_articleContent_to_articlesFragment)

        }
    }


}
