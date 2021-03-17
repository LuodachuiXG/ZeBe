package com.lpddr5.nzhaibao.ui.imageDisplay

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.lpddr5.nzhaibao.ZeBeApplication
import com.lpddr5.nzhaibao.databinding.ActivityImageDisplayBinding
import uk.co.senab.photoview.PhotoViewAttacher
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class ImageDisplayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageDisplayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDisplayBinding.inflate(layoutInflater)
//        window.addFlags(android.R.attr.windowFullscreen)
        setContentView(binding.root)


        binding.imageDisplayImageView.setImageDrawable(mDrawable)

        binding.imageDisplayImageView.onPhotoTapListener =
            object : PhotoViewAttacher.OnPhotoTapListener {
                override fun onPhotoTap(view: View?, x: Float, y: Float) {
                    finish()
                }
                override fun onOutsidePhotoTap() {}
            }

        binding.imageDisplayImageView.setOnLongClickListener {
            val that = this
            val bitmap = (binding.imageDisplayImageView.drawable as BitmapDrawable).bitmap
            AlertDialog.Builder(this).apply {
                setTitle("操作")
                setItems(ZeBeApplication.imageDisplayLongClickList) { _, i ->
                    if (ZeBeApplication.imageDisplayLongClickList[i] == "分享") {
                        val uri = saveBitmap(bitmap)
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(Intent.createChooser(intent, "分享到"))
                    } else if (ZeBeApplication.imageDisplayLongClickList[i] == "保存到相册") {
                        if (ZeBeApplication.applyStoragePermission(that)){
                            val f = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path + "/$mImageIndex.jpg")
                            try {
                                val fos = FileOutputStream(f)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                                fos.flush()
                                fos.close()
                                MediaStore.Images.Media.insertImage(contentResolver, f.absolutePath, "$mImageIndex.jpg", null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                create()
                show()
            }
            true
        }
    }

    /** * 将图片存到本地  */
    private fun saveBitmap(bm: Bitmap): Uri? {
        try {
            val f = File(getExternalFilesDir(null)?.path + "/share.jpg")
            if (!f.exists()) {
                f.parentFile.mkdirs()
                f.createNewFile()
            }
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this,
                    "com.lpddr5.nzhaibao.fileprovider", f)
            } else {
                Uri.fromFile(f)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    companion object {
        lateinit var mDrawable: Drawable
        private var mImageIndex: Int = 0

        fun startActivity(context: Context, drawable: Drawable, imageIndex: Int) {
            val intent = Intent(context, ImageDisplayActivity::class.java)
            mDrawable = drawable
            mImageIndex = imageIndex
            context.startActivity(intent)
        }
    }
}

