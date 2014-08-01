### Android Commons ###

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.alexvasilkov/android-commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.alexvasilkov/android-commons)

Various useful utilities for Android apps development.

API documentation provided as javadocs.

#### Usage ####

Add dependency to your build.gradle file:

    compile 'com.alexvasilkov:android-commons:x.x.x'

#### Classes ####

##### Views #####
Simple utility class to find views within layout with implicit types casting. I.e. `TextView tv = Views.find(layout, R.id.text_view);`

##### Fonts #####
Utility to set custom fonts for all views extending TextView, uses android:tag to store font info.

##### Intents #####
Helper methods to start external apps, i.e. Email app, Calendar app and so on.

##### SpannableBuilder #####
SpannableStringBuilder implementation that helps applying various text styles to single TextView.

##### InstanceStateManager #####
Helper methods to save and restore instance state of activities and fragments.

##### Preferences & PreferencesHelper #####
Helper methods to store various values in SharedPreferences.

##### ItemsAdapter & LayoutItemsAdapter #####
BaseAdapter implementations to be used with java.util.List. LayoutItemsAdapter class provides methods to populate any ViewGroup with views from adapter with optional views recycling mechanism.

##### KeyboardHelper #####
Helper methods to show / hide keyboard and determine keyboard state.

##### Convertable & ConvertUtils #####
Helper utils to convert one set of objects into another

##### FillWidthImageView #####
ImageView that will be scaled to fit entire available width preserving aspect ratio (by adjusting view's height). It is also possible to set aspect ratio of the image before it is loaded into the view.

##### ThreadSafeDateFormatter #####
Thread safe wrapper for SimpleDateFormatter.

##### AdvancedDateFormatter #####
AdvancedDateFormatter extends SimpleDateFormatter and adds some postprocessing commands. I.e. pattern `'{lower:'MMMM'}' d` will lower month name.
Only `lower` and `upper` command are supported right now.



[Changelog](https://github.com/alexvasilkov/AndroidCommons/wiki/Changelog)


#### License ####

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
