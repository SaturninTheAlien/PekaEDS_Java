# PekaEDS
A modern Pekka Kana 2 Levels Editor

PekaEDS - Pekka Kana 2 EDiting Suite is a program that lets users create and edit levels for the game **Pekka Kana 2**.

This editor saves **PK2 levels** in a new format, so it requires the most recent PK2 version, codename "Greta Engine," available at this link:

https://github.com/SaturninTheAlien/pk2_greta

## Note
Keep in mind that the code on the main branch is under active development and may contain bugs. It's strongly advised to backup your levels before using it.

## Binary distribution
See the <a href="https://github.com/SaturninTheAlien/PekaEDS_Java/releases">Releases</a> section to download the .jar file.

To run it, you need Java 21 or newer.

If you are on Windows, I recommend this version: <br>
https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.msi <br>
On Windows, you can usually run the .jar file by double-clicking it in File Explorer.

To run it on Linux or macOS, use the command line:

    $ java -jar PekaEDS-(version).jar

## FAQ
### Can I make or edit levels for older versions of Pekka Kana 2 than 1.5.0 "Greta" with this software?

If you want to make levels that work with older PK2 versions, the answer is no, and we will probably never add support for it.

If you want to open old levels made for older PK2 versions, the answer is yes. Support for old level formats is **read-only**. However, if you edit these levels, they are converted to the new format and will require a modern game version.


### I'm on Windows, the .jar file doesn't work.

Make sure you have installed Java 21 JDK or a newer version.
You can download it from Oracle here: <br>
https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.msi

If Java 8 is installed on your system, I highly recommend uninstalling it and switching to Java 21 or newer, as Java 8 is outdated and unsupported for most modern applications.

<span style="color:red; font-weight:bold">Do not download Java from java.com!</span> <br>
It offers only the obsolete Java 8 — a legacy version that Oracle forgot to take down.

## Build
PekaEDS uses [Gradle](https://gradle.org/). Run gradlew.bat build on Windows or ./gradlew on Linux & MacOS.
