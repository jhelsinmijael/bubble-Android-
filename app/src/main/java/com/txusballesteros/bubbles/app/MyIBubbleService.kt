package com.txusballesteros.bubbles.app

import android.util.Log
import com.txusballesteros.bubbles.IBubblesService

class MyIBubbleService : IBubblesService(){

    override fun onCreate() {
        super.onCreate()
        Log.d(MyIBubbleService::class.java.simpleName, "onCreate")
    }

}