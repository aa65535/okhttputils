#OkHttpUtils

A Java utils library for [OkHttp][0]. For more information see the [wiki][1].

#Download

Download the [latest JAR][2] or grab via Gradle:

```
compile 'utils.okhttp:okhttputils:2.0.1'
```

or Maven:

```
<dependency>
  <groupId>utils.okhttp</groupId>
  <artifactId>okhttputils</artifactId>
  <version>2.0.1</version>
  <type>pom</type>
</dependency>
```

or Ivy:

```
<dependency org='utils.okhttp' name='okhttputils' rev='2.0.1'>
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
[2]: https://github.com/aa65535/okhttputils/releases
