package com.example.cimon_chilimonitoring.ui.blog.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItemBlog
import com.example.cimon_chilimonitoring.databinding.ActivityBlogDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale


class BlogDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBlogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        updateUI()
    }

    private fun updateUI(){
        val blog = intent.getParcelableExtra<ResultsItemBlog>(EXTRA_BLOG)
        blog?.let {
            with(binding){
                tvDetailTitle.text = it.title
                tvDetailDate.text = it.description
                tvDetailSourceInput.text = it.source
                tvDetailSourceDate.text = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(it.createdAt)
                    ?.let { date ->
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
                    }
                Glide.with(this@BlogDetailActivity)
                    .load(it.imageUrl)
                    .into(ivDetailPlaceholder)

                btnDetailDeeplink.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(blog.source))
                    startActivity(intent)
                }
            }
        }
    }

    companion object {
        const val EXTRA_BLOG = "extra_blog"
    }
}