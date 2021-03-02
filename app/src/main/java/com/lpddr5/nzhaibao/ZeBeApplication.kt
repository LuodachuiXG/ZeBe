package com.lpddr5.nzhaibao

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMViewModel
import com.permissionx.guolindev.PermissionX
import java.util.jar.Manifest

class ZeBeApplication : Application(){

    companion object {
        const val updateInfo =
            "1.现在白天或夜间都默认显示深色页面。\n\n2.优化部分UI显示。\n\n3.新增检查更新功能。\n\n4.优化浏览图片默认画质。" +
                    "\n\n5.为下一个版本登录功能做部分准备。"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        var currentCategory = NineFiveMMViewModel.HOT


        lateinit var nineFiveMMViewModel: NineFiveMMViewModel

        val imageDisplayLongClickList = arrayOf("保存到相册", "分享")

        fun applyStoragePermission(fragmentActivity: FragmentActivity): Boolean {
            var result = false
            PermissionX.init(fragmentActivity)
                .permissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .explainReasonBeforeRequest()
                .onExplainRequestReason { scope, deniedList ->
                    val message = "需要您同意以下权限才可以继续操作"
                    scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
                }
                .request {allGranted, grantedList,  deniedList ->
                    if (allGranted) {
                        result = true
                    }
                }
            return result
        }

        /**
         * 获取当前版本号
         */
        fun getVersionName(): Double {
            return try {
                // 获取packageManager的实例
                val packageManager: PackageManager = context.packageManager
                // getPackageName()是你当前类的包名，0代表是获取版本信息
                val packInfo: PackageInfo = packageManager.getPackageInfo(context.packageName, 0)
                packInfo.versionName.toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
                0.0
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}