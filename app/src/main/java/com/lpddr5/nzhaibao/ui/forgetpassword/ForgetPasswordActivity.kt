package com.lpddr5.nzhaibao.ui.forgetpassword

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.databinding.ActivityForgetPasswordBinding
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.tool.toast
import com.lpddr5.nzhaibao.ui.signup.SignUpActivity

class ForgetPasswordActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(ForgetPasswordViewModel::class.java) }

    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.forgetpasswordToolbar)

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
        val lastTimeCountDownSecondEmail = Repository.getCurrentCodeCountDownEmailByForgetPassword()
        binding.forgetPasswordEmail.setText(lastTimeCountDownSecondEmail)
        if (lastTimeCountDownSecond > 0) {
            setBtnSendCodeIsCountDown(lastTimeCountDownSecond, lastTimeCountDownSecondEmail)
        }
        // 验证码等待时间大于0，并且忘记密码邮箱不为空的话就将设置新密码的EditText和更改按钮显示
        if (lastTimeCountDownSecond > 0 && lastTimeCountDownSecondEmail.isNotEmpty()) {
            binding.forgetPasswordPassword.visibility = View.VISIBLE
            binding.forgetPasswordPasswordAgain.visibility = View.VISIBLE
            binding.forgetPasswordBtnChangePassword.visibility = View.VISIBLE
        }

        // 发送验证码单击事件
        binding.forgetPasswordBtnSendCode.setOnClickListener {
            val email = binding.forgetPasswordEmail.getText()
            if (email.isNullOrEmpty()) {
                binding.forgetPasswordEmail.setFocus(true)
                "请将邮箱填写完整".toast()
                return@setOnClickListener
            }

            if (email.indexOf("@") <= 0 || email.indexOf(".") <= 0) {
                binding.forgetPasswordEmail.setFocus(true)
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
                        // 发送成功，将新密码框显示出来
                        setBtnSendCodeIsCountDown(60, binding.forgetPasswordEmail.getText())
                        binding.forgetPasswordPassword.visibility = View.VISIBLE
                        binding.forgetPasswordPasswordAgain.visibility = View.VISIBLE
                        binding.forgetPasswordBtnChangePassword.visibility = View.VISIBLE
                    }
                    "-3" -> {
                        // 用户不存在
                        "用户不存在".toast()
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

        // 更改密码按钮点击事件
        binding.forgetPasswordBtnChangePassword.setOnClickListener {
            val email = binding.forgetPasswordEmail.getText()
            val code = binding.forgetPasswordCode.getText()
            val password = binding.forgetPasswordPassword.getText()
            val passwordAgain = binding.forgetPasswordPasswordAgain.getText()

            if (email.isNullOrEmpty() || code.isNullOrEmpty() || password.isNullOrEmpty() || passwordAgain.isNullOrEmpty()) {
                "请将信息填写完整".toast()
                when {
                    email.isNullOrEmpty() -> {
                        binding.forgetPasswordEmail.setFocus(true)
                    }
                    code.isNullOrEmpty() -> {
                        binding.forgetPasswordCode.setFocus(true)
                    }
                    password.isNullOrEmpty() -> {
                        binding.forgetPasswordPassword.setFocus(true)
                    }
                    passwordAgain.isNullOrEmpty() -> {
                        binding.forgetPasswordPasswordAgain.setFocus(true)
                    }
                }
                return@setOnClickListener
            }
            if (password.length < 10) {
                binding.forgetPasswordPassword.setFocus(true)
                "密码长度不能低于10".toast()
                return@setOnClickListener
            }
            if (password != passwordAgain) {
                binding.forgetPasswordPasswordAgain.setFocus(true)
                "两次输入密码不一致".toast()
                return@setOnClickListener
            }
            if (code.length < 5) {
                binding.forgetPasswordCode.setFocus(true)
                "验证码错误".toast()
                return@setOnClickListener
            }
            // 向服务器提交更改密码请求
            viewModel.updatePassword(email, password, code)
        }

        // 更改密码回调观察
        viewModel.updatePassword.observe(this) {
            val serviceResult = it.getOrNull()
            if (serviceResult != null && serviceResult.request == "updatePassword") {
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
                        // 更改成功
                        "密码更改成功".toast()
                        // 更改成功后将验证码倒计时和触发邮箱清空
                        Repository.clearCurrentCodeCountDownAndEmail(1)

                        val mIntent = intent
                        val bundle = Bundle().apply {
                            putString("account", binding.forgetPasswordEmail.getText())
                            putString("password", binding.forgetPasswordPassword.getText())
                        }
                        mIntent.putExtras(bundle)
                        setResult(0x002, mIntent)
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
        binding.forgetPasswordBtnSendCode.isEnabled = false
        /** 倒计时，一次1秒 */
        object : CountDownTimer((count * 1000).toLong(), 1000) {
            override fun onFinish() {
                binding.forgetPasswordBtnSendCode.text = "发送验证码";
                binding.forgetPasswordBtnSendCode.isEnabled = true
            }
            override fun onTick(p0: Long) {
                val s = p0 / 1000
                // 将剩余秒保存到SP中，防止用户直接退出软件或返回页面后重新发送验证码
                Repository.setCurrentCodeCountDown(s.toInt(), email, 1)
                binding.forgetPasswordBtnSendCode.text = s.toString()
            }
        }.start()
    }

    companion object {
        fun startActivityForResult(context: FragmentActivity) {
            val intent = Intent(context, ForgetPasswordActivity::class.java)
            context.startActivityForResult(intent, 0x002)
        }
    }
}