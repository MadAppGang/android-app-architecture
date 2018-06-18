package com.madappgang.madappgangmvvmtestarch.model.models

/**
 * Created by Serhii Chaban sc@madappgang.com on 29.05.18.
 */
data class SourceFile(val id: String,
                      val name: String,
                      val filePath: String,
                      val isDirectory: Boolean = false)