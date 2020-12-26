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
import androidx.annotation.LayoutRes

class IBubblesManager private constructor(private val context: Context) {


    /**
     * indica si el servicio vinculado está unido o no
     * true si está vinculado y false si no
     * */
    private var bounded = false
    /**
     * Servicio vinculado
     * @see IBubblesService
     * */
    private var bubblesService: BaseBubblesService? = null
    /**
     * Recurso de diseño para la vista de eliminar
     * */
    @LayoutRes
    private var trashLayoutResourceId = 0
    /**
     * Listener para un servicio vinculado, notifica cuando el
     * @see ServiceConnection.onServiceConnected se ha llamado
     * */
    private var listener: OnInitializedCallback? = null
    private val bubbleServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as? IBubblesService.BubblesServiceBinder
            bubblesService = binder?.service
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

    /**
     * Se usa para pasar el servicio a iniciar, por defecto es el servico IBubbleService,
     * sin embargo tambien se pueden pasar otros que sean desendientes de IBubbleService
     * */
    fun initialize(serviceClass: Class<*> = IBubblesService::class.java) {
        context.bindService(Intent(context, serviceClass),
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
        private val bubblesManager: IBubblesManager? = getInstance(context)

        fun setInitializationCallback(listener: OnInitializedCallback?): Builder {
            bubblesManager?.listener = listener
            return this
        }

        fun setTrashLayout(trashLayoutResourceId: Int): Builder {
            bubblesManager?.trashLayoutResourceId = trashLayoutResourceId
            return this
        }

        fun build(): IBubblesManager? {
            return bubblesManager
        }

    }

    companion object {
        private var INSTANCE: IBubblesManager? = null
        private fun getInstance(context: Context): IBubblesManager? {
            if (INSTANCE == null) {
                INSTANCE = IBubblesManager(context)
            }
            return INSTANCE
        }
    }
}