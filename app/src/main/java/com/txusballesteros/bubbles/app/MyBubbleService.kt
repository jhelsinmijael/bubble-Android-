package com.txusballesteros.bubbles.app

import android.util.Log
import android.view.LayoutInflater
import com.txusballesteros.bubbles.BubbleLayout
import com.txusballesteros.bubbles.BubblesManager
import com.txusballesteros.bubbles.BubblesService

class MyBubbleService: BubblesService() {

    private var bubblesManager: BubblesManager? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(MyBubbleService::class.java.simpleName, "onCreate")

        bubblesManager = BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_trash_layout)
                .build()

        val bubbleView = LayoutInflater.from(this).inflate(R.layout.bubble_layout, null) as BubbleLayout
        bubblesManager?.addBubble(bubbleView, 0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(MyBubbleService::class.java.simpleName, "onDestroy")
        bubblesManager?.recycle()
    }

}