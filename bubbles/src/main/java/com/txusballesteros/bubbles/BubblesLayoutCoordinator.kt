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

import android.view.View
import android.view.WindowManager

internal class BubblesLayoutCoordinator private constructor() {
    private var trashView: BubbleTrashLayout? = null
    private var windowManager: WindowManager? = null
    private var bubblesService: BubblesService? = null
    fun notifyBubblePositionChanged(bubble: BubbleLayout, x: Int, y: Int) {
        if (trashView != null) {
            trashView?.visibility = View.VISIBLE
            if (checkIfBubbleIsOverTrash(bubble)) {
                trashView?.applyMagnetism()
                trashView?.vibrate()
                applyTrashMagnetismToBubble(bubble)
            } else {
                trashView?.releaseMagnetism()
            }
        }
    }

    private fun applyTrashMagnetismToBubble(bubble: BubbleLayout) {
        val trashContentView = trashContent
        val trashCenterX = (trashContentView?.left?:0) + (trashContentView?.measuredWidth?:0) / 2
        val trashCenterY = (trashContentView?.top?:0) + (trashContentView?.measuredHeight?:0) / 2
        val x = trashCenterX - bubble.measuredWidth / 2
        val y = trashCenterY - bubble.measuredHeight / 2
        bubble.viewParams?.x = x
        bubble.viewParams?.y = y
        windowManager?.updateViewLayout(bubble, bubble.viewParams)
    }

    private fun checkIfBubbleIsOverTrash(bubble: BubbleLayout): Boolean {
        var result = false
        if (trashView?.visibility == View.VISIBLE) {
            val trashContentView = trashContent
            val trashWidth = trashContentView?.measuredWidth
            val trashHeight = trashContentView?.measuredHeight
            val trashLeft = (trashContentView?.left?:0) - (trashWidth?:0) / 2
            val trashRight = (trashContentView?.left?:0) + (trashWidth?:0) + (trashWidth?:0) / 2
            val trashTop = (trashContentView?.top?:0) - (trashHeight?:0) / 2
            val trashBottom = (trashContentView?.top?:0) + (trashHeight?:0) + (trashHeight?:0) / 2
            val bubbleWidth = bubble.measuredWidth
            val bubbleHeight = bubble.measuredHeight
            val bubbleLeft = bubble.viewParams?.x
            val bubbleRight = (bubbleLeft?:0) + bubbleWidth
            val bubbleTop = bubble.viewParams?.y
            val bubbleBottom = (bubbleTop?:0) + bubbleHeight
            if ((bubbleLeft?:0) >= trashLeft && bubbleRight <= trashRight) {
                if ((bubbleTop?:0) >= trashTop && bubbleBottom <= trashBottom) {
                    result = true
                }
            }
        }
        return result
    }

    fun notifyBubbleRelease(bubble: BubbleLayout) {
        if (trashView != null) {
            if (checkIfBubbleIsOverTrash(bubble)) {
                bubblesService?.removeBubble(bubble)
            }
            trashView?.visibility = View.GONE
        }
    }

    class Builder(service: BubblesService?) {
        private val layoutCoordinator: BubblesLayoutCoordinator? = instance
        fun setTrashView(trashView: BubbleTrashLayout?): Builder {
            layoutCoordinator?.trashView = trashView
            return this
        }

        fun setWindowManager(windowManager: WindowManager?): Builder {
            layoutCoordinator?.windowManager = windowManager
            return this
        }

        fun build(): BubblesLayoutCoordinator? {
            return layoutCoordinator
        }

        init {
            layoutCoordinator?.bubblesService = service
        }
    }

    private val trashContent: View?
        private get() = trashView?.getChildAt(0)

    companion object {
        private var INSTANCE: BubblesLayoutCoordinator? = null
        private val instance: BubblesLayoutCoordinator?
            private get() {
                if (INSTANCE == null) {
                    INSTANCE = BubblesLayoutCoordinator()
                }
                return INSTANCE
            }
    }
}