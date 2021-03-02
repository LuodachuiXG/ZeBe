package com.lpddr5.nzhaibao

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lpddr5.nzhaibao.databinding.ActivityMainBinding
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMFragment
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMViewModel
import com.lpddr5.nzhaibao.ui.setting.SettingFragment
import com.lpddr5.nzhaibao.ui.workDisplay.WorkDisplayViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_ninefivemm.*
import android.view.MenuItem as MenuItem

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(MainActivityViewModel::class.java) }

    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerToggle: ActionBarDrawerToggle

    // toolbar上menu对象，用于获取menu中的action
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initView()
    }

    private fun initView() {
        // 启动后自动检测一次更新
        viewModel.getLatestVersion()

        // 设置默认 Fragment 是 HomeFragment
        replaceFragment(NineFiveMMFragment())

        // 显示home按钮
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
        // 设置home按钮动画效果
        drawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close)
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState();

        // 设置滑动菜单主页默认选中
        binding.navView.setCheckedItem(R.id.navHot)

        // 设置欢动菜单选中事件监听器
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navHot -> {
                    ZeBeApplication.currentCategory = NineFiveMMViewModel.HOT
                    replaceFragment(NineFiveMMFragment())
                }
                R.id.navNewest -> {
                    ZeBeApplication.currentCategory = NineFiveMMViewModel.NEWEST
                    replaceFragment(NineFiveMMFragment())
                }
                R.id.navCampusBelle -> {
                    ZeBeApplication.currentCategory = NineFiveMMViewModel.CAMPUS_BELLE
                    replaceFragment(NineFiveMMFragment())
                }
                R.id.navStar -> {
                    ZeBeApplication.currentCategory = NineFiveMMViewModel.STAR
                    replaceFragment(NineFiveMMFragment())
                }
                R.id.navSenXi -> {
                    ZeBeApplication.currentCategory = NineFiveMMViewModel.SEN_XI
                    replaceFragment(NineFiveMMFragment())
                }
                R.id.navTenderModel -> {
                    ZeBeApplication.currentCategory = NineFiveMMViewModel.TENDER_MODEL
                    replaceFragment(NineFiveMMFragment())
                }
                R.id.navPure -> {
                    ZeBeApplication.currentCategory = NineFiveMMViewModel.PURE
                    replaceFragment(NineFiveMMFragment())
                }
                R.id.navGirl -> {
                    ZeBeApplication.currentCategory = NineFiveMMViewModel.GIRL
                    replaceFragment(NineFiveMMFragment())
                }
                R.id.navCheckUpdate -> {
                    viewModel.getLatestVersion()
                }
                R.id.navSetting -> {
                    replaceFragment(SettingFragment())
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // 给 NavigationView 动态添加headerLayout，否则获取不到headerLayout中的view
        val headerLayout = binding.navView.inflateHeaderView(R.layout.nav_header)
        val imageView = headerLayout.findViewById(R.id.navHeaderIconImage) as CircleImageView
        imageView.setOnClickListener {
            "你好呀，嘿嘿~~".toast()
        }

        // ViewModel中变量观察
        // 观察检查更新LiveData变量
        viewModel.latestVersionLiveData.observe(this) {
            val latestVersion = it.getOrNull()
            LogUtil.v(this, latestVersion.toString())
            if (latestVersion != null && latestVersion.id != -1) {
                if (ZeBeApplication.getVersionName() < latestVersion.latestVersion.toDouble()) {
                    // 有新版本
                    AlertDialog.Builder(this).apply {
                        setTitle("检测到新版本")
                        setMessage(latestVersion.updateInfo.replace("\\n", "\n") + "\n\n点击确定立即前往下载")
                        setPositiveButton("确定") { _, _ ->
                            val intent = Intent(Intent.ACTION_VIEW)
                            val uri = Uri.parse(latestVersion.downloadUrl)
                            intent.data = uri
                            startActivity(intent)
                        }
                        setNegativeButton("取消", null)
                        create()
                        show()
                    }
                } else {
                    "您当前使用的是最新版".toast()
                }
            } else {
                "检查更新失败！请重试".toast()
            }
        }

        // 根据当前是否是新版第一次打开来显示更新内容
        if (Repository.getLastTimeRunningVersion().toDouble() < ZeBeApplication.getVersionName()) {
            AlertDialog.Builder(this).apply {
                setTitle("本次更新")
                setMessage("此通知仅显示一次\n\n本次更新：\n${ZeBeApplication.updateInfo}")
                setPositiveButton("OJBK", null)
                create()
                show()
            }
        }
        Repository.setLastTimeRunningVersion()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        menu?.let {
            this.menu = it
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.closeDrawers()
                }
            }
            R.id.toolbar_menu_toPage -> {
                val editText = EditText(this)
                AlertDialog.Builder(this).setTitle("输入跳转页数")
                    .setView(editText)
                    .setPositiveButton("确定") { _, _ ->
                        val page = editText.text.toString()
                        if (page != "") {
                            ZeBeApplication.nineFiveMMViewModel.currentIndex = page.toInt()
                            ZeBeApplication.nineFiveMMViewModel.gotoPage(
                                ZeBeApplication.currentCategory,
                                page.toInt()
                            )
                        }
                    }.show()
            }
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragment, fragment)
        transaction.commit()
    }
}