package com.example.lazyeye.articles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.example.lazyeye.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Articles : Fragment() {
    private lateinit var kidArticle: CardView
    private lateinit var adultArticle: CardView
    private lateinit var lazyArticle: CardView
    private lateinit var differanceArticle: CardView
    private lateinit var protectionArticle: CardView
    private lateinit var twice_sight_article: CardView
    private lateinit var bnv: BottomNavigationView
    private lateinit var article_link: String
    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles , container , false)
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        callViews()
        kidArticle.setOnClickListener {

            navigateToArticleContent(it , "articleKid.html" , R.drawable.article_kid_image)

        }
        adultArticle.setOnClickListener {
            navigateToArticleContent(it , "adult.html" , R.drawable.adult_image)


        }
        lazyArticle.setOnClickListener {

            navigateToArticleContent(it , "ghamash.html" , R.drawable.gamash)

        }
        differanceArticle.setOnClickListener {

            navigateToArticleContent(it , "differance_lazy_gamsh.html" , R.drawable.lazy_gamash)
        }
        protectionArticle.setOnClickListener {

            navigateToArticleContent(it , "protection.html" , R.drawable.protection)
        }
        twice_sight_article.setOnClickListener {

            navigateToArticleContent(it , "twice_sight.html" , R.drawable.twice_sight)
        }

        bnv.visibility = View.VISIBLE
    }

    private fun callViews() {
        kidArticle = view?.findViewById(R.id.kid_hawel)!!
        adultArticle = view?.findViewById(R.id.adult_hawel)!!
        lazyArticle = view?.findViewById(R.id.lazy_article)!!
        differanceArticle = view?.findViewById(R.id.differance_lazy_hawel)!!
        protectionArticle = view?.findViewById(R.id.protectin)!!
        twice_sight_article = view?.findViewById(R.id.sight_twice)!!

        bnv = requireActivity().findViewById(R.id.my_bottom_navigation)
    }

    private fun navigateToArticleContent(view: View , link: String , image: Int) {
        article_link = link
        bundle.putInt("image" , image)
        bundle.putString("name" , article_link)
        Navigation.findNavController(view)
                .navigate(R.id.action_articlesFragment_to_articleContent , bundle)
        bnv.visibility = View.INVISIBLE

    }
}