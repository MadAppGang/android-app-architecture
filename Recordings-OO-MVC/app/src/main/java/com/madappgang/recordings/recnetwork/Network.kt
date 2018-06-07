package com.madappgang.recordings.recnetwork

import com.madappgang.recordings.reccore.Folder
import com.madappgang.recordings.reccore.Track

/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

interface Network {
    fun getRootFolder(): Result<Folder>

    fun createFolder(parentFolder: Folder, folder: Folder): Result<Folder>

    fun uploadTrack(parentFolder: Folder, track: Track): Result<Track>

    fun removeFolder(folder: Folder): Result<Unit>

    fun removeTrack(track: Track): Result<Unit>
}