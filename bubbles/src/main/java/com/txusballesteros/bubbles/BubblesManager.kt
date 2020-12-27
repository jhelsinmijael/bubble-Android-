/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package com.txusballesteros.bubbles

import android.util.Log
import androidx.annotation.LayoutRes
import java.lang.ref.WeakReference

class BubblesManager private constructor(service: BubblesService) {

    private val bubblesService = WeakReference(service)

    /**
     * Recurso de diseño para la vista de eliminar
     * */
    @LayoutRes
    private var trashLayoutResourceId = 0
    /**
     * Listener para un servicio vinculado, notifica cuando el
     * @see ServiceConnection.onServiceConnected se ha llamado
     * */


    private fun configureBubblesService() {
        bubblesService.get()?.addTrash(trashLayoutResourceId)
    }

    fun recycle() {
        bubblesService.clear()
        INSTANCE = null
    }

    /**
     * y options
     * -1 top
     * -2 center-vetical
     * -3 bottom
     * */
    fun addBubble(bubble: BubbleLayout, x: Int, y: Int) {
        Log.d(LOG_TAG, "addBubble=${bubblesService.get()}")
        bubblesService.get()?.addBubble(bubble, x, y)
    }

    fun removeBubble(bubble: BubbleLayout?) {
        if (bubble!=null) {
            bubblesService.get()?.removeBubble(bubble)
        }
    }

    class Builder(service: BubblesService) {
        private val bubblesManager: BubblesManager? = getInstance(service)

        fun setTrashLayout(trashLayoutResourceId: Int): Builder {
            bubblesManager?.trashLayoutResourceId = trashLayoutResourceId
            return this
        }

        fun build(): BubblesManager? {
            bubblesManager?.configureBubblesService()
            return bubblesManager
        }

    }

    companion object {
        private val LOG_TAG = BubblesManager::class.java.simpleName
        private var INSTANCE: BubblesManager? = null
        private fun getInstance(service: BubblesService): BubblesManager? {
            if (INSTANCE == null) {
                INSTANCE = BubblesManager(service)
            }
            return INSTANCE
        }
    }
}