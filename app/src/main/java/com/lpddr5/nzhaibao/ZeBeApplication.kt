package com.lpddr5.nzhaibao

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMViewModel
import com.permissionx.guolindev.PermissionX

class ZeBeApplication : Application(){

    companion object {
        const val updateInfo =
            "1.完善登录功能。\n\n2.完善’喜欢功能‘。\n\n3.给显示P的标签新增阴影，防止亮色无法分辨。" +
                    "\n\n4.新增个人主页并完善’我的喜欢’功能。"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        // 默认是显示最新图片
        var currentCategory = NineFiveMMViewModel.NEWEST

        lateinit var nineFiveMMViewModel: NineFiveMMViewModel

        // 查看大图页面长按菜单
        val imageDisplayLongClickList = arrayOf("保存到相册", "分享")

        // 不同平台标记
        const val NINE_FIVE_MM = "95mm"
        const val ZHAI_NAN_NV_SHEN = "宅男女神"

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