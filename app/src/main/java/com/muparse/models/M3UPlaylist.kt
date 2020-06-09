package com.muparse.models

internal class M3UPlaylist {

    var playlistName: String? = null

    var playlistParams: String? = null

    var playlistItems: ArrayList<M3UItem>? = null

    fun getSingleParameter(paramName: String): String {
        val paramsArray = this.playlistParams!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (parameter in paramsArray) {
            if (parameter.contains(paramName)) {
                return parameter.substring(parameter.indexOf(paramName) + paramName.length).replace("=", "")
            }
        }
        return ""
    }
}
