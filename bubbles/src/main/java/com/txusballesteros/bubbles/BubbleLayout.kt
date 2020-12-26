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

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager

class BubbleLayout : BubbleBaseLayout {
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialX = 0
    private var initialY = 0
    private var onBubbleRemoveListener: OnBubbleRemoveListener? = null
    private var onBubbleClickListener: OnBubbleClickListener? = null
    private var lastTouchDown: Long = 0
    private var animator: MoveAnimator
    private var mWidth = 0
    private var mWindowManager: WindowManager
    private var shouldStickToWall = true
    fun setOnBubbleRemoveListener(listener: OnBubbleRemoveListener?) {
        onBubbleRemoveListener = listener
    }

    fun setOnBubbleClickListener(listener: OnBubbleClickListener?) {
        onBubbleClickListener = listener
    }

    constructor(context: Context) : super(context) {
        animator = MoveAnimator()
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        animator = MoveAnimator()
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        animator = MoveAnimator()
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        initializeView()
    }

    fun setShouldStickToWall(shouldStick: Boolean) {
        shouldStickToWall = shouldStick
    }

    fun notifyBubbleRemoved() {
        if (onBubbleRemoveListener != null) {
            onBubbleRemoveListener?.onBubbleRemoved(this)
        }
    }

    private fun initializeView() {
        isClickable = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playAnimation()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = (viewParams?.x?:0)
                    initialY = (viewParams?.y?:0)
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    playAnimationClickDown()
                    lastTouchDown = System.currentTimeMillis()
                    updateSize()
                    animator.stop()
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = initialX + (event.rawX - initialTouchX).toInt()
                    val y = initialY + (event.rawY - initialTouchY).toInt()
                    viewParams?.x = x
                    viewParams?.y = y
                    mWindowManager.updateViewLayout(this, viewParams)
                    if (layoutCoordinator != null) {
                        layoutCoordinator?.notifyBubblePositionChanged(this, x, y)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    goToWall()
                    if (layoutCoordinator != null) {
                        layoutCoordinator?.notifyBubbleRelease(this)
                        playAnimationClickUp()
                    }
                    if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {
                        if (onBubbleClickListener != null) {
                            onBubbleClickListener?.onBubbleClick(this)
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun playAnimation() {
        if (!isInEditMode) {
            val animator = AnimatorInflater
                    .loadAnimator(context, R.animator.bubble_shown_animator) as AnimatorSet
            animator.setTarget(this)
            animator.start()
        }
    }

    private fun playAnimationClickDown() {
        if (!isInEditMode) {
            val animator = AnimatorInflater
                    .loadAnimator(context, R.animator.bubble_down_click_animator) as AnimatorSet
            animator.setTarget(this)
            animator.start()
        }
    }

    private fun playAnimationClickUp() {
        if (!isInEditMode) {
            val animator = AnimatorInflater
                    .loadAnimator(context, R.animator.bubble_up_click_animator) as AnimatorSet
            animator.setTarget(this)
            animator.start()
        }
    }

    private fun updateSize() {
        val metrics = DisplayMetrics()
        mWindowManager.defaultDisplay.getMetrics(metrics)
        val display = mWindowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        mWidth = size.x - getWidth()
    }

    interface OnBubbleRemoveListener {
        fun onBubbleRemoved(bubble: BubbleLayout?)
    }

    interface OnBubbleClickListener {
        fun onBubbleClick(bubble: BubbleLayout?)
    }

    fun goToWall() {
        if (shouldStickToWall) {
            val middle = mWidth / 2
            val nearestXWall = if ((viewParams?.x?:0) >= middle) mWidth.toFloat() else 0.toFloat()
            animator.start(nearestXWall, viewParams?.y?.toFloat()?:0f)
        }
    }

    private fun move(deltaX: Float, deltaY: Float) {
        viewParams?.x =  viewParams?.x?.plus(deltaX.toInt())
        viewParams?.y = viewParams?.y?.plus(deltaY.toInt())
        mWindowManager.updateViewLayout(this, viewParams)
    }

    private inner class MoveAnimator : Runnable {
        private val handler = Handler(Looper.getMainLooper())
        private var destinationX = 0f
        private var destinationY = 0f
        private var startingTime: Long = 0
        fun start(x: Float, y: Float) {
            destinationX = x
            destinationY = y
            startingTime = System.currentTimeMillis()
            handler.post(this)
        }

        override fun run() {
            if (rootView != null && rootView.parent != null) {
                val progress = Math.min(1f, (System.currentTimeMillis() - startingTime) / 400f)
                val deltaX = (destinationX - (viewParams?.x?:0)) * progress
                val deltaY = (destinationY - (viewParams?.y?:0)) * progress
                move(deltaX, deltaY)
                if (progress < 1) {
                    handler.post(this)
                }
            }
        }

        fun stop() {
            handler.removeCallbacks(this)
        }
    }

    companion object {
        private const val TOUCH_TIME_THRESHOLD = 150
    }
}