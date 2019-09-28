package com.`in`.weareindian.quiknews.ModelClasses

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class HomeActivity_API( val id: String, val url: String, val created: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(url)
        parcel.writeString(created)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeActivity_API> {
        override fun createFromParcel(parcel: Parcel): HomeActivity_API {
            return HomeActivity_API(parcel)
        }

        override fun newArray(size: Int): Array<HomeActivity_API?> {
            return arrayOfNulls(size)
        }
    }

}
