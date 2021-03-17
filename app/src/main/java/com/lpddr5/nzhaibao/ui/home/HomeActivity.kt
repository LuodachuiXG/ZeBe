package com.lpddr5.nzhaibao.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.lpddr5.nzhaibao.databinding.ActivityHomeBinding
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMFragment

class HomeActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    private lateinit var binding: ActivityHomeBinding

    private val tabFragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setSupportActionBar(binding.homeToolbar)
        setContentView(binding.root)

        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        title = ""
        binding.homeTitle.text = Repository.getData("name")

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        // 添加ab
        for (i in viewModel.tabs.indices) {
            binding.homeTabLayout.addTab(binding.homeTabLayout.newTab().setText(viewModel.tabs[i]))
        }
        tabFragmentList.add(HomeLikeFragment())
        tabFragmentList.add(HomeHistoryFragment())
        tabFragmentList.add(HomeInfoFragment())

        binding.homeViewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return tabFragmentList[position]
            }

            override fun getCount(): Int {
                return tabFragmentList.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return viewModel.tabs[position]
            }
        }
        binding.homeTabLayout.setupWithViewPager(binding.homeViewPager, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }
}