package com.madappgang.architecture.recorder.data.models

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 04.07.2018.
 */
data class FileModel(val filePath: String,
                     val name: String = "",
                     val isDirectory: Boolean = true)