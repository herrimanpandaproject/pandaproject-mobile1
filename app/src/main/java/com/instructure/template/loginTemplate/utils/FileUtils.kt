/*
 * Copyright (C) 2019 - present Instructure, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.instructure.template.loginTemplate.utils

import java.io.*


object FileUtils {

    val FILE_DIRECTORY = "cache"

    /**
     * deleteAllFilesInDirectory will RECURSIVELY delete all files/folders in a directory
     *
     * @param startFile
     * @return
     */
    fun deleteAllFilesInDirectory(startFile: File): Boolean {
        try {
            //If it's a directory.
            if (startFile.isDirectory) {
                //Delete all files inside of it.
                val files = startFile.list()
                for (fileName in files) {
                    val file = File(startFile, fileName)
                    //If it's a directory. recursive.
                    if (file.isDirectory) {
                        deleteAllFilesInDirectory(file)
                    } else {
                        file.delete()
                    }//It's a file. Delete it.
                }
                //Now delete the parent folder.
                startFile.delete()
            } else {
                startFile.delete()
            }//If it's not a directory, delete the file.
            return true
        } catch (E: Exception) {
            return false
        }

    }
}

