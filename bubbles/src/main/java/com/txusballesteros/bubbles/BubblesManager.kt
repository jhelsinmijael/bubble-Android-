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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.txusballesteros.bubbles.BubblesService.BubblesServiceBinder

class BubblesManager private constructor(private val context: Context) {
    private var bounded = false
    private var bubblesService: BubblesService? = null
    private var trashLayoutResourceId = 0
    private var listener: OnInitializedCallback? = null
    private val bubbleServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as BubblesServiceBinder
            bubblesService = binder.service
            configureBubblesService()
            bounded = true
            if (listener != null) {
                listener?.onInitialized()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bounded = false
        }
    }

    private fun configureBubblesService() {
        bubblesService?.addTrash(trashLayoutResourceId)
    }

    fun initialize() {
        context.bindService(Intent(context, BubblesService::class.java),
                bubbleServiceConnection,
                Context.BIND_AUTO_CREATE)
    }

    fun recycle() {
        context.unbindService(bubbleServiceConnection)
    }

    fun addBubble(bubble: BubbleLayout, x: Int, y: Int) {
        if (bounded) {
            bubblesService?.addBubble(bubble, x, y)
        }
    }

    fun removeBubble(bubble: BubbleLayout?) {
        if (bounded && bubble!=null) {
            bubblesService?.removeBubble(bubble)
        }
    }

    class Builder(context: Context) {
        private val bubblesManager: BubblesManager? = getInstance(context)
        fun setInitializationCallback(listener: OnInitializedCallback?): Builder {
            bubblesManager?.listener = listener
            return this
        }

        fun setTrashLayout(trashLayoutResourceId: Int): Builder {
            bubblesManager?.trashLayoutResourceId = trashLayoutResourceId
            return this
        }

        fun build(): BubblesManager? {
            return bubblesManager
        }

    }

    companion object {
        private var INSTANCE: BubblesManager? = null
        private fun getInstance(context: Context): BubblesManager? {
            if (INSTANCE == null) {
                INSTANCE = BubblesManager(context)
            }
            return INSTANCE
        }
    }
}