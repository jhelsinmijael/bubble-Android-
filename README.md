[![](https://jitpack.io/v/jhelsinmijael/bubble-Android-.svg)](https://jitpack.io/#jhelsinmijael/bubble-Android-)


Bubbles for Android
=====================

Bubbles for Android is an Android library to provide chat heads capabilities on your apps. With a fast way to integrate with your development.

![Logo](assets/bubbles_demo.gif)

## Usage
```gradle
dependencies {
  implementation 'com.github.jhelsinmijael:bubble-Android-:latestVersion'
}
```

### Adding your first Bubble

Compose your Bubble layout, for example using a Xml layout file. Remember that the first view of your Bubble layout has to be a BubbleLayout view.

```xml
<com.txusballesteros.bubbles.BubbleLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">

    <ImageView
        android:id="@+id/avatar"
        android:layout_width="70dp"
        android:layout_height="70dp"
        android:layout_gravity="center"
        android:background="@drawable/profile_decorator"
        android:src="@drawable/profile"
        android:scaleType="centerCrop"/>

</com.txusballesteros.bubbles.BubbleLayout>
```

#### For IBinder Service

Register IBubblesService on Manifest
```xml
<manifest>
    <application>
        <service android:name=".IBubbleService"/>
    </application>
</manifest>
```

Create your BubblesManager instance.

```kotlin
class MyActivity: AppCompatActivity{

    private var iBubblesManager: IBubblesManager

    
    override fun onCreate(savedInstanceState: Bundle?) {
        iBubblesManager = IBubblesManager.Builder(this).build()
        iBubblesManager.initialize()
        //...
    }

    
    override fun onDestroy() {
        iBubblesManager.recycle()
        //...
    }
}
```

##### Or

If use subclass of IBubbleService

```xml
<manifest>
    <application>
        <service android:name=".MyIBubbleService"/>
    </application>
</manifest>
```

Indicate subclass

```kotlin
bubblesManager.initialize(MyIBubbleService::class.java)
```

#### For normal Service

```kotlin
class MyBubbleService: BubblesService() {

    private var bubblesManager: BubblesManager? = null

    override fun onCreate() {
        super.onCreate()

        bubblesManager = BubblesManager.Builder(this).build()

        val bubbleView = LayoutInflater.from(this).inflate(R.layout.bubble_layout, null) as BubbleLayout
        bubblesManager?.addBubble(bubbleView, 0, 0)
        //...
    }

    override fun onDestroy() {
        super.onDestroy()
        bubblesManager?.recycle()
        //...
    }

}
```

### For all Services

Attach your Bubble to the window.

```kotlin
val bubbleView: BubbleLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.bubble_layout, null) as? BubbleLayout
bubblesManager.addBubble(bubbleView, 60, 20);
```

### Configuring your Bubbles Trash

If you want to have a trash to remove on screen bubbles, you can configure the
layout of that.

Define your trash layout Xml.

```xml
<ImageView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginBottom="20dp"
    android:src="@mipmap/bubble_trash_background"
    android:layout_gravity="bottom|center_horizontal" />
```

Configure the trash layout with your BubblesManager builder.

```kotlin
bubblesManager = BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_trash_layout)
                .build()
```

## License

Copyright Txus Ballesteros 2015 (@txusballesteros)

This file is part of some open source application.

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

Contact: Txus Ballesteros <abderlahman.mobiledevleoper@gmail.com>
