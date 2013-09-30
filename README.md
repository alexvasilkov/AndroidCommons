### Fluffy Commons ###

Various useful utilities for Android apps development.

#### Classes ####

##### UsefulIntents #####

Helper methods to start various intents:

    /* Starting email activity */
    sendEmail(Context context, String email, String subject, String content)
    
    /* Starting email activity */
    sendEmailAsHtml(Context context, String email, String subject, Spanned content)
    
    /* Starting email activity */
    sendEmail(Context context, String[] toEmails, String subject, Spanned content, String mimeType, String attachedFileUri)
    
    /* Starting sms activity */
    sendSms(Context context, String content)
    
    /* Starting 'add to calendar' activity */
    addCalendarEvent(Context context, long beginTime, long endTime, TimeZone tz, boolean isAllDay, String title, String description, String location)
    
    /* Starting web browser */
    openWebBrowser(Context context, String url)
    
    /* Common share action */
    share(Context context, String title, String text)
    
    /* Starting dailer */
    dial(Context context, String phone)
    
    /* Starting Google Play */
    openGooglePlay(Context context, String appPackage)

#### How to build ####

You need [Maven](http://maven.apache.org/) to build the project. Just run `mvn clean install` from project's root, jar file will be generated into `target` folder.

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
