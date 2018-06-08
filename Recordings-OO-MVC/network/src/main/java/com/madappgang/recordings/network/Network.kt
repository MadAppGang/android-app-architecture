package com.madappgang.recordings.network

import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Track

/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */


interface Network {
    fun getRootFolder(): Result<Folder>

    fun createFolder(parentFolder: Folder, folder: Folder): Result<Folder>

    fun uploadTrack(parentFolder: Folder, track: Track): Result<Track>

    fun removeFolder(folder: Folder): Result<Unit>

    fun removeTrack(track: Track): Result<Unit>
}