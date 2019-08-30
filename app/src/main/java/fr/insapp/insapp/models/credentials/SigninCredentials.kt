package fr.insapp.insapp.models.credentials

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by thomas on 10/07/2017.
 * Kotlin rewrite on 30/08/2019.
 */

@Parcelize
data class SigninCredentials(
    @SerializedName("Device") val device: String
): Parcelable
