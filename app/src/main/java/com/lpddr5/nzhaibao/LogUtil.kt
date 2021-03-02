package com.lpddr5.nzhaibao

import android.util.Log
import androidx.fragment.app.Fragment

object LogUtil {

    private const  val VERBOSE = 1

    private const val DEBUG = 2

    private const val INFO = 3

    private const val WARN = 4

    private const val ERROR = 5

    private var level = VERBOSE

    fun v(any: Any, msg: String) {
        if (level <= VERBOSE) {
            Log.v(any.javaClass.simpleName, msg)
        }
    }

    fun d(any: Any, msg: String) {
        if (level <= DEBUG) {
            Log.d(any.javaClass.simpleName, msg)
        }
    }

    fun i(any: Any, msg: String) {
        if (level <= INFO) {
            Log.i(any.javaClass.simpleName, msg)
        }
    }

    fun w(any: Any, msg: String) {
        if (level <= WARN) {
            Log.w(any.javaClass.simpleName, msg)
        }
    }

    fun e(any: Any, msg: String) {
        if (level <= ERROR) {
            Log.e(any.javaClass.simpleName, msg)
        }
    }
}