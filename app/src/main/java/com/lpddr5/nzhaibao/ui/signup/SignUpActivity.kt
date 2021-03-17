package com.lpddr5.nzhaibao.ui.signup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.R
import com.lpddr5.nzhaibao.databinding.ActivitySignUpBinding
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.tool.toast
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(SignUpViewModel::class.java) }

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.signUpToolbar)

        initView()
    }

    private fun initView() {
        title = ""
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        // 用户如果之前验证码等待时间没结束，就继续进行倒计时
        val lastTimeCountDownSecond = Repository.getCurrentCodeCountDown()
        // 获取用户之前触发验证码倒计时的邮箱，默认为空。倒计时为0时会被清空
        val lastTimeCountDownSecondEmail = Repository.getCurrentCodeCountDownEmailBySignUp()
        binding.signUpEmail.setText(lastTimeCountDownSecondEmail)
        if (lastTimeCountDownSecond > 0) {
            setBtnSendCodeIsCountDown(lastTimeCountDownSecond, lastTimeCountDownSecondEmail)
        }

        // 发送验证码单击事件
        binding.signUpBtnSendCode.setOnClickListener {
            val email = binding.signUpEmail.getText()
            if (email.isNullOrEmpty()) {
                binding.signUpEmail.setFocus(true)
                "请将邮箱填写完整".toast()
                return@setOnClickListener
            }

            if (email.indexOf("@") <= 0 || email.indexOf(".") <= 0) {
                binding.signUpEmail.setFocus(true)
                "请填写正确的邮箱地址".toast()
                return@setOnClickListener
            }
            viewModel.sendCode(email)
        }

        // 发送验证码回调观察
        viewModel.sendCode.observe(this) {
            val serviceResult = it.getOrNull()
            LogUtil.e(this, serviceResult.toString())
            if (serviceResult != null && serviceResult.request == "sendCode") {
                when (serviceResult.result) {
                    "1" -> {
                        setBtnSendCodeIsCountDown(60, binding.signUpEmail.getText())
                    }
                    "-3" -> {
                        // 用户已存在
                        "该邮箱已经注册".toast(Toast.LENGTH_LONG)
                    }
                    else -> {
                        // 服务端异常
                        "发送失败，请重试".toast()
                    }
                }
            } else {
                "未知错误，请重试".toast()
            }
        }

        // 注册账号单击事件
        binding.signUpBtnSignUp.setOnClickListener {
            val name = binding.signUpName.getText()
            val password = binding.signUpPassword.getText()
            val passwordAgain = binding.signUpPasswordAgain.getText()
            val email = binding.signUpEmail.getText()
            val code = binding.signUpCode.getText()

            if (name.isNullOrEmpty() || password.isNullOrEmpty() || passwordAgain.isNullOrEmpty()
                || email.isNullOrEmpty() || code.isNullOrEmpty()) {
                "请将信息填写完整".toast()
                when {
                    name.isNullOrEmpty() -> {
                        binding.signUpName.setFocus(true)
                    }
                    password.isNullOrEmpty() -> {
                        binding.signUpPassword.setFocus(true)
                    }
                    passwordAgain.isNullOrEmpty() -> {
                        binding.signUpPasswordAgain.setFocus(true)
                    }
                    email.isNullOrEmpty() -> {
                        binding.signUpEmail.setFocus(true)
                    }
                    code.isNullOrEmpty() -> {
                        binding.signUpCode.setFocus(true)
                    }
                }
                return@setOnClickListener
            }

            if (password.length < 10) {
                binding.signUpPassword.setFocus(true)
                "密码长度不能低于10".toast()
                return@setOnClickListener
            }

            if (password != passwordAgain) {
                binding.signUpPasswordAgain.setFocus(true)
                "两次输入密码不一致".toast()
                return@setOnClickListener
            }

            if (code.length < 5) {
                binding.signUpCode.setFocus(true)
                "验证码错误".toast()
                return@setOnClickListener
            }

            // 向服务器提交注册请求
            viewModel.signUpUser(name, password, email, code)
        }

        // 注册用户回调观察
        viewModel.signUpUser.observe(this) {
            val serviceResult = it.getOrNull()
            if (serviceResult != null && serviceResult.request == "signUpUser") {
                when (serviceResult.result) {
                    "-2" -> {
                        // 验证码错误
                        "验证码错误".toast()
                    }
                    "-1" -> {
                        // 服务端异常
                        "注册失败，请重试".toast()
                    }
                    "1" -> {
                        // 注册成功
                        "注册成功".toast()
                        // 注册成功后将验证码倒计时和触发邮箱清空
                        Repository.clearCurrentCodeCountDownAndEmail(0)

                        val mIntent = intent
                        val bundle = Bundle().apply {
                            putString("account", binding.signUpEmail.getText())
                            putString("password", binding.signUpPassword.getText())
                        }
                        mIntent.putExtras(bundle)
                        setResult(0x001, mIntent)
                        finish()
                    }
                }
            } else {
                "未知错误，请重试".toast()
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

    private fun setBtnSendCodeIsCountDown(count: Int, email: String) {
        // 发送成功
        binding.signUpBtnSendCode.isEnabled = false
        /** 倒计时，一次1秒 */
        object : CountDownTimer((count * 1000).toLong(), 1000) {
            override fun onFinish() {
                binding.signUpBtnSendCode.text = "发送验证码";
                binding.signUpBtnSendCode.isEnabled = true
            }
            override fun onTick(p0: Long) {
                val s = p0 / 1000
                // 将剩余秒保存到SP中，防止用户直接退出软件或返回页面后重新发送验证码
                Repository.setCurrentCodeCountDown(s.toInt(), email, 0)
                binding.signUpBtnSendCode.text = s.toString()
            }
        }.start()
    }

    companion object {
        fun startActivityForResult(context: FragmentActivity) {
            val intent = Intent(context, SignUpActivity::class.java)
            context.startActivityForResult(intent, 0x001)
        }
    }
}