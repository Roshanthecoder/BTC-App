package com.gsk.franchise.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.InputType
import android.text.Spanned
import android.util.*
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import com.btc.app.R
import com.btc.app.utils.MyApplication
import com.btc.app.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/*-----------------------------------------------------*/

const val TAG = "BTCApp"
val application = MyApplication()

const val currency = "₹"


val DEVICE_NAME = Build.DEVICE + " " + Build.MODEL + " " + Build.MANUFACTURER
val DEVICE_ID = getDeviceId()


@SuppressLint("HardwareIds")
fun getDeviceId(): String {
    val deviceId =
        Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
    return deviceId;
}


fun imageToBase64(imageFile: File): String {
    return "data:image/jpeg;base64," + fileToBase64(imageFile)
}

fun stringToBase64(str: String): String {
    val encodeStr = Base64.encode(str.toByteArray(), Base64.DEFAULT).toString(Charsets.UTF_8)
    return encodeStr
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b = baos.toByteArray()
    return "data:image/jpeg;base64," + Base64.encodeToString(b, Base64.DEFAULT)
}

fun fileToBase64(file: File): String {
    return ByteArrayOutputStream().use { outputStream ->
        Base64OutputStream(outputStream, Base64.DEFAULT).use { base64 ->
            file.inputStream().use { inputStream ->
                inputStream.copyTo(base64)
            }
        }
        return@use outputStream.toString()
    }
}


@SuppressLint("DiscouragedApi")
fun ImageView.setImageApp(icon: String) {
    val res = resources.getIdentifier(icon, "drawable", this.context.packageName)
    this.setImageResource(res)
}

fun TextView.setTextColor2(color: Int) {
    setTextColor(resources.getColor(color))
}

fun TextView.setTextColor2(color: String) {
    setTextColor(Color.parseColor(color))
}

fun TextView.setFontFamily(fontFamily: Int) {
    val typeface = ResourcesCompat.getFont(context, fontFamily)
    setTypeface(typeface)
}


fun TextView.textSize(dimen: Int) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(dimen))
}


fun View.setMargins(
    left: Int = this.marginLeft,
    top: Int = this.marginTop,
    right: Int = this.marginRight,
    bottom: Int = this.marginBottom,
) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(left, top, right, bottom)
    }
}


fun setTextColor(textView: TextView, color: Int) {
    textView.setTextColor(textView.context.resources.getColor(color))
}

fun getString(id: Int): String {
    return application.resources.getString(id)
}


fun hideSoftKeyBoard(context: Context) {
    if (context is Activity) {
        val view = context.currentFocus
        hideSoftKeyBoard(view)
    }
}

fun copyText(context: Context, string: String?) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("label", string)
    clipboardManager.setPrimaryClip(clipData)
}

fun hideSoftKeyBoard(view: View?) {
    view?.postDelayed({
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }, 100)
}

fun convertDateTime(dateTime: String): String {
    try {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date = dateFormat.parse(dateTime)
        val formatter = SimpleDateFormat("dd-MMM-yyyy hh:mm aa")
        val dateStr = formatter.format(date)
        return dateStr
    } catch (e: Exception) {
        return "N/A"
    }

}

/*
fun getDialog(context: Context, layoutId: Int): Dialog {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
    val width = Utils.getScreenWidth(context)
    dialog.setContentView(layoutId)
    val layout = dialog.findViewById<LinearLayout>(R.id.main_layout)
    val params = layout.layoutParams
    params.width = width - (width * 10 / 100)
    layout.layoutParams = params
    return dialog
}
*/

fun isNetworkAvailable(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        if (capabilities == null) return false
        else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
        else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
        else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
    } else {
        val activeNetworkInfo = manager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}


fun showToastShort(msg: String) {
    Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
}

fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun checkNumeric(mobile: String): Boolean {
    val regex = "^[0-9]{10}$"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(mobile)
    return matcher.matches()
}

fun checkMobileNo(mobile: String): Boolean {
    val regex = "^[6-9]{1}[0-9]{9}$"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(mobile)
    return matcher.matches()
}

fun getHtmlSpanned(htmlText: String?): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlText)
    }
}


fun number2digits(number: String?): String {
    if (number.isNullOrEmpty()) return ""
    val value = String.format("%.2f", number.toDouble())
    return value
}

fun number2digits(number: Double): String {
    val value = String.format("%.2f", number)
    return value
}

fun stringToArrayList(string: String): ArrayList<String> {
    val list = ArrayList<String>()
    for (item in string.trim(' ').split(",")) {
        list.add(item)
    }
    return list
}

fun onAlertSnackbar(view: View, msg: String) {
    try {
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
        val snackView = snackbar.view
        snackView.setBackgroundColor(Color.parseColor("#1562B6"))
        snackbar.show()
    } catch (e: Exception) {
        showToastShort(msg)
    }
}

fun onSnackbar(view: View, msg: String) {
    try {
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
        val snackView = snackbar.view
        snackView.setBackgroundColor(Color.parseColor("#DD1212"))
        snackbar.show()
    } catch (e: Exception) {
        showToastShort(msg)
    }
}


fun setButtonEnabled(view: View) {
    view.isClickable = false
    CoroutineScope(Dispatchers.IO).launch {
        delay(TimeUnit.MILLISECONDS.toMillis(1000))
        withContext(Dispatchers.Main) {
            view.isClickable = true
        }
    }
}

 
