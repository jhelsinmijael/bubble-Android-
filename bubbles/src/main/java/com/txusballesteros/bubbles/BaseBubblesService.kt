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

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import java.util.*

/*/**
 * Clase base para los servicios expuestos
 * @see IBubblesService
 * @see BubblesService
 * */*/
open class BaseBubblesService : Service() {
    protected val bubbles: MutableList<BubbleLayout> = ArrayList()
    private var bubblesTrash: BubbleTrashLayout? = null
    private var windowManager: WindowManager? = null
    private var layoutCoordinator: BubblesLayoutCoordinator? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    protected fun recycleBubble(bubble: BubbleLayout) {
        Handler(Looper.getMainLooper()).post {
            getWindowManager()?.removeView(bubble)
            for (cachedBubble in bubbles) {
                if (cachedBubble == bubble) {
                    bubble.notifyBubbleRemoved()
                    bubbles.remove(cachedBubble)
                    break
                }
            }
        }
    }

    private fun getWindowManager(): WindowManager? {
        if (windowManager == null) {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        }
        return windowManager
    }

    fun addBubble(bubble: BubbleLayout, x: Int, y: Int) {
        val layoutParams = buildLayoutParamsForBubble(x, y)
        bubble.windowManager = getWindowManager()
        bubble.viewParams = layoutParams
        bubble.layoutCoordinator = layoutCoordinator
        bubbles.add(bubble)
        addViewToWindow(bubble)
    }

    fun addTrash(trashLayoutResourceId: Int) {
        if (trashLayoutResourceId != 0) {
            bubblesTrash = BubbleTrashLayout(this)
            bubblesTrash?.windowManager = windowManager
            bubblesTrash?.viewParams = buildLayoutParamsForTrash()
            bubblesTrash?.visibility = View.GONE
            LayoutInflater.from(this).inflate(trashLayoutResourceId, bubblesTrash, true)
            addViewToWindow(bubblesTrash)
            initializeLayoutCoordinator()
        }
    }

    private fun initializeLayoutCoordinator() {
        layoutCoordinator = BubblesLayoutCoordinator.Builder(this)
                .setWindowManager(getWindowManager())
                .setTrashView(bubblesTrash)
                .build()
    }

    private fun addViewToWindow(view: BubbleBaseLayout?) {
        Handler(Looper.getMainLooper()).post { getWindowManager()?.addView(view, view?.viewParams) }
    }

    var params: WindowManager.LayoutParams? = null
    private fun buildLayoutParamsForBubble(x: Int, y: Int): WindowManager.LayoutParams? {
        params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT)
        }
        params?.gravity = Gravity.TOP or Gravity.START
        params?.x = x
        params?.y = y
        return params
    }

    private fun buildLayoutParamsForTrash(): WindowManager.LayoutParams {
        val x = 0
        val y = 0
        val params: WindowManager.LayoutParams
        params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT)
        } else {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT)
        }
        params.x = x
        params.y = y
        return params
    }

    fun removeBubble(bubble: BubbleLayout) {
        recycleBubble(bubble)
    }

}