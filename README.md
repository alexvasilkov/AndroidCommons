### Android Commons ###

[![Maven Central][mvn-img]][mvn-url]
[![Size][size-img]][size-url]

Various useful utilities for Android apps development.

API documentation provided as [Javadoc][javadoc-url].

### Usage ###

Add dependency to your build.gradle file:

    compile 'com.alexvasilkov:android-commons:2.0.2'

### Main features ###

#### Views ####
Simple utility class to find views within layout with implicit types casting
(e.g. `TextView tv = Views.find(layout, R.id.text_view);`)
Includes other methods to simplify work with views and layouts.

#### Navigate ####
Helper methods to navigate between screens and to open external app to perform related actions,
for example: share text; compose email or sms; open browser or Google Play, pick phone number
and many more.

#### SpannableBuilder ####
SpannableStringBuilder implementation that helps applying various text styles to a single TextView.

#### Fonts ####
Utility to set custom fonts for all views extending TextView, uses android:tag to store font info.

#### InstanceStateManager ####
Helper methods to save and restore instance state of activities and fragments.

#### ItemsAdapter & LayoutItemsAdapter ####
BaseAdapter implementations to be used with java.util.List.
LayoutItemsAdapter class provides methods to populate any ViewGroup with views from adapter
with optional views recycling mechanism.

#### KeyboardHelper ####
Helper methods to show / hide keyboard and listen for keyboard state.

#### Convertable & ConvertUtils ####
Helper utils to convert one set of objects into another.

#### ThreadSafeDateFormatter ####
Thread safe wrapper for SimpleDateFormatter.

#### ISO8601DateFormatter ####
ISO 8601 date parser and formatter.

#### FillWidthImageView ####
ImageView that will be scaled to fit entire available width preserving aspect ratio (by adjusting view's height). It is also possible to set aspect ratio of the image before it is loaded into the view.

#### BoundedFrameLayout & BoundedLinearLayout & BoundedRelativeLayout ####
Base view groups implementations that allows setting maxWidth and maxHeight.


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

[mvn-url]: https://maven-badges.herokuapp.com/maven-central/com.alexvasilkov/android-commons
[mvn-img]: https://img.shields.io/maven-central/v/com.alexvasilkov/android-commons.svg?style=flat-square

[size-url]: http://www.methodscount.com/?lib=com.alexvasilkov%3Aandroid-commons%3A2.0.2
[size-img]: https://img.shields.io/badge/Methods%20and%20size-814%20%7C%2085%20KB-e91e63.svg?style=flat-square

[javadoc-url]: http://javadoc.io/doc/com.alexvasilkov/android-commons
