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

import android.content.Intent
import android.os.*
import java.util.*

/**
 * Servicio vinculado
 * */
open class IBubblesService : BaseBubblesService() {
    private val binder = BubblesServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        for (bubble in bubbles) {
            recycleBubble(bubble)
        }
        bubbles.clear()
        return super.onUnbind(intent)
    }

    internal inner class BubblesServiceBinder : Binder() {
        val service: IBubblesService
            get() = this@IBubblesService
    }
}