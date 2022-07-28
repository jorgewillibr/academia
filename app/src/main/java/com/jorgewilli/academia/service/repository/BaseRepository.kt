package com.jorgewilli.academia.service.repository

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class BaseRepository (context: Context){

    val mContext: Context = context

    /**
     * Verifica se existe conexão com internet
     */
    fun isConnectionAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            cm.run {
                cm.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    fun checkPermissions(
        activity: Activity?,
        requestCode: Int,
        permissions: Array<String>
    ): Boolean {

        val list: MutableList<String> = ArrayList()
        //No nosso for a variavel permissao irá receber de uma em uma as strings do array da variavel permissoes
        for (permission in permissions) {

            //caso o usuario NÃO tenha permitido ao aplicativo certos privilegios de acesso
            //a variavel ok recebera false
            val ok = ContextCompat.checkSelfPermission(
                activity!!,
                permission
            ) == PackageManager.PERMISSION_GRANTED

            if (!ok) { // caso a variavel ok tenha recebido false, então cairá dentro desse if
                // a variavel list irá receber a nossa string do for "que simboliza a permissao que
                // o aplicativo está querendo"
                list.add(permission)
            }

        } // fim do nosso for - caso a variavel permissoes tenha mais de uma string,o for é executado novamente


        //se a nossa variavel list for vazia quer dizer que o usuario já permitiu que o nosso aplicativo
        //tenha certos privilegios e então entrará dentro no nosso if
        if (list.isEmpty()) {
            return true // caso entre dentro do nosso if o metodo acaba aqui
        }

        //solicita a permissao
        ActivityCompat.requestPermissions(activity!!, list.toTypedArray(), requestCode)
        return false
    }
}