Tranf
===========

Features - Two main programs: One for Android, one for desktop. Communication between each program will facilitate necessary transfers between the two platforms.

Target Users - PC users with Android phones
OS - Android 2.3 and higher, Windows, Linux, OSX
Technologies Used: Java

Usage
===========
Place the jar wherever you want. When ran, creates a .conf folder in that directory that contains two files, out.txt and in.txt. These are configuration files which you can manually edit. Each line contains a folder to either sync out or into that machine.

Run the jar with the -n option to run in Daemon mode, or without to launch as GUI on startup.
```
java -jar Tranf.jar -n
```
In every directly pointed to by .conf/out.txt, there will be a metafile called .tranf. This file contains information about the folder and the files therein.

Detailed Description
============
  This project will consist of two main applications to be used on the Desktop and Phone at the same time. They will communicate via wifi (and possibly other methods) to allow users to synchronize files between their two devices however they choose.
  The goal of this project will be to allow users to, through a GUI on the desktop client, manually specify which directories they wish to have synchronized between the two devices. This will mean that users will have to have some way of remotely browsing the Android filesystem, as well as their current PC’s. Users should be allowed to select arbitrary folders to be synchronized between the two devices in whichever direction they choose (ie bidirectionally, PC->Phone, vice versa). The Android app will have to have to have at least a minimal GUI to allow for users to customize the application running (ports used, connection type, etc).
  This project will be produced using Java for both pieces of software, and will require socket programming and multithreading be implemented in order to efficiently communicate between the two programs and transfer files between devices. A number of more specific implementation design choices will need to be decided at a later date. For example, a communication protocol will have to be developed for communication between the two applications, and before that we will need to choose a format for said protocol (ie XML, JSON, etc).
