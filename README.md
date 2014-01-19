TransitionsBackport
===================

Android library for using the [Transitions API released with Android KitKat][1] on older versions of Android. At the moment, it is compatible with Android 4.0 (API 14) and above.


Usage
=====

The API is exactly the same as the [Transitions API][2], just change your imports
from `android.transition.XXX` to `android.support.transition.XXX`.
If you use XML files to create your transitions you need to put them in the res/anim folder instead of the res/transition folder.

You can take a look at [this video][3] from Chet Haase explaining how to use the Transitions API.

This is an early backport, some features may not be available yet.


Developed By
============

* Stéphane Guérin - <guerwan@gmail.com>



License
=======

    Copyright 2013 Stéphane Guérin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: http://developer.android.com/reference/android/transition/package-summary.html
[2]: http://developer.android.com/reference/android/transition/package-summary.html
[3]: https://www.youtube.com/watch?v=S3H7nJ4QaD8
