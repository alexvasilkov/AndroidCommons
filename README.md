### Fluffy Commons ###

Various useful utilities for Android apps development.

API documentation provided as javadocs.

#### Classes ####

##### Preferences & PreferencesHelper #####
Helper methods to store various values in SharedPreferences.

##### InstanceStateManager #####
Helper methods to save and restore instance state of activities and fragments.

##### ItemsAdapter & LayoutItemsAdapter #####
BaseAdapter implementations to be used with java.util.List. LayoutItemsAdapter class provides methods to populate any ViewGroup with views from adapter with optional views recycling mechanism.

##### UsefulIntents #####
Helper methods to start external apps, i.e. Email app, Calendar app and so on.

##### KeyboardHelper #####
Helper methods to show / hide keyboard and determine keyboard state.

##### ThreadSafeDateFormatter #####
Thread safe wrapper for SimpleDateFormatter.

##### Fonts #####
Utility to set custom fonts for all views extending TextView, uses android:tag to store font info.

##### Views #####
Simple utility class to find views within layout with implicit types casting. I.e. `TextView tv = Views.find(layout, R.id.text_view);`

##### FillWidthImageView #####
ImageView that will be scaled to fit entire available width preserving aspect ratio (by adjusting view's height). Is is also possible to set aspect ratio of the image before it is loaded into the view.

##### Convertable & ConvertUtils #####
Helper utils to convert one set of objects into another

##### AdvancedDateFormatter #####
AdvancedDateFormatter extends SimpleDateFormatter and adds some postprocessing commands. I.e. pattern `'{lower:'MMMM'}' d` will lower month name.
Only `lower` and `upper` command are supported right now.

##### SpannableBuilder #####
SpannableStringBuilder implementation that helps applying various text styles to single TextView.

#### Gradle usage ####

`compile 'com.alexvasilkov:fluffy-commons:{latest version}'`

#### How to build ####

You need [Maven](http://maven.apache.org/) to build the project. Just run `mvn clean install` from project's root, jar file will be generated into `target` folder.

#### Change log ####

##### 1.0.9-SNAPSHOT: #####

* TypefaceHelper reworked into Fonts class with dynamic fonts caching.
* Added SpannableBuilder class to apply various dynamic styles to single TextView.
* Added AppContext class - static holder for application context.

##### 1.0.8: #####

* Added AdvancedDateFormatter class
* Added converting utils (Convertable & ConvertUtils)
* Support for chooser dialog title in UsefulIntents (for `share` method)
* Small API improvements

##### 1.0.7: #####

* Added new Preconditions utility
* Parcelable support for InstanceStateManager
* Bugs fixes

##### 1.0.6: #####

* Added new utilities: TypefaceHelper, Views
* KeyboardHelper now works with both adjustResize and adjustPan
* Small API improvements

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
