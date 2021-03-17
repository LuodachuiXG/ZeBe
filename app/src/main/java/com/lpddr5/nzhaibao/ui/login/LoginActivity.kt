package com.lpddr5.nzhaibao.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.R
import com.lpddr5.nzhaibao.ZeBeApplication
import com.lpddr5.nzhaibao.databinding.ActivityLoginBinding
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.tool.toast
import com.lpddr5.nzhaibao.tool.*
import com.lpddr5.nzhaibao.ui.forgetpassword.ForgetPasswordActivity
import com.lpddr5.nzhaibao.ui.home.HomeActivity
import com.lpddr5.nzhaibao.ui.signup.SignUpActivity


class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.loginToolbar)

        initView()
    }

    private fun initView() {
        // 设置Toolbar标题
        title = ""

        // 显示返回按钮
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        // 注册账号单击事件
        binding.loginBtnSignUp.setOnClickListener {
            SignUpActivity.startActivityForResult(this)
        }

        // 忘记密码单击事件
        binding.loginBtnForgetPassword.setOnClickListener {
            ForgetPasswordActivity.startActivityForResult(this)
        }

        // 登录按钮单击事件
        binding.loginBtnSignIn.setOnClickListener {
            val account = binding.loginAccount.getText()
            val password = binding.loginPassword.getText()
            if (account.isNullOrEmpty() || password.isNullOrEmpty()) {
                "请将账号密码填写完整".toast()
                when {
                    account.isNullOrEmpty() -> {
                        binding.loginAccount.setFocus(true)
                    }
                    password.isNullOrEmpty() -> {
                        binding.loginPassword.setFocus(true)
                    }
                }
                return@setOnClickListener
            }
            viewModel.loginUser(account, password)
        }

        // 观察登录LiveData00
        viewModel.userLoginLiveData.observe(this) {
            val user = it.getOrNull()
            if (user != null && user.zeBeUser_id != -1) {
                // 登录成功
                "登陆成功".toast()
                // 将账号信息保存本地
                Repository.setData("email", user.zeBeUser_email)
                Repository.setData("password", binding.loginPassword.getText())
                Repository.setData("name", user.zeBeUser_name)
                Repository.setData("isLogin", "true")
                finish()
            } else {
                // 登录失败
                binding.loginTips.visibility = View.VISIBLE
                binding.loginTips.text = "登录失败，请检查您的账号和密码是否正确"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 注册账号或更改密码返回
        if (requestCode == resultCode) {
            val bundle = data?.extras
            val account = bundle?.getString("account")
            val password = bundle?.getString("password")
            binding.loginAccount.setText(account.toString())
            binding.loginPassword.setText(password.toString())
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    private fun showSoftInputFromWindow(activity: Activity, editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
//        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}