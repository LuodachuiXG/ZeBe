package com.lpddr5.nzhaibao.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * type 是指当前喜欢的类型，比如95MM、宅男女神
 */
@Parcelize
data class MyLike(val zeBeLike_id: Int, val zeBeLike_url: String,
                  val zeBeLike_title: String, val zeBeLike_imgUrl: String,
                  val zeBeLike_p: String, val zeBeLike_type: String,
                  val zeBeLike_email: String): Parcelable