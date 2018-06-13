/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.core

sealed class NetworkExceptions(message: String) : Throwable(message) {

    class FolderIsNotExistException(val folder: Folder) :
        NetworkExceptions("Folder ${folder.name} not exist")

    class ObjectParsingException : NetworkExceptions("Object Parsing Exception")

    class UnknownException: NetworkExceptions("Unknown Exception")
}