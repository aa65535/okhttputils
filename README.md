#OkHttpUtils

A Java utils library for [OkHttp][0]. For more information see the [Wiki][1].

[![Download][3]][2] [![Build Status][5]][4]

#Binaries

Gradle:

```
compile 'utils.okhttp:okhttputils:2.4.0'
```

Maven:

```
<dependency>
  <groupId>utils.okhttp</groupId>
  <artifactId>okhttputils</artifactId>
  <version>2.4.0</version>
  <type>pom</type>
</dependency>
```

Ivy:

```
<dependency org='utils.okhttp' name='okhttputils' rev='2.4.0'>
  <artifact name='$AID' ext='pom'></artifact>
</dependency>
```

#ProGuard

```
#OkHttpUtils
-keep class utils.okhttp.**{ *; }
-keep interface utils.okhttp.** { *; }
-dontwarn utils.okhttp.**

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
```

#License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[0]: https://github.com/square/okhttp
[1]: https://github.com/aa65535/okhttputils/wiki
[2]: https://bintray.com/aa65535/maven/okhttputils/_latestVersion
[3]: https://api.bintray.com/packages/aa65535/maven/okhttputils/images/download.svg
[4]: https://travis-ci.org/aa65535/okhttputils
[5]: https://travis-ci.org/aa65535/okhttputils.svg?branch=master
