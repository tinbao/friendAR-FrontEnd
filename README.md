# Nitrogen-FrontEnd
Front End build of the FRIENDAR mobile application developed in native android and java.

### Features

* Augmented Reality Friend Finding
* Group Communications
* Graphical User Interfaces for each requirement including home, maps, login, registration, friend search and chat.
* The REST client that communicates with the server and Back End using Volley.
* Continuous Integration and Deployment releasing an `apk` file for each merge and commit that is being done.





### Install Instructions

##### Prerequisite Installs

* Android Studio 2.3.3 (Android Studio will install all below, otherwise please install these)

  * JUnit 4.12
  * Volley 1.0.19
  * Java JDK 8.0

* Ruby 2.1.9 [OPTIONAL]

* Travis 5.2.1 [OPTIONAL]

  **Note** that Ruby and Travis are required if the *continuous deployment* and *integration* files and/or tags want to be changed or edited. Otherwise, they are not necessary.

* An Android device running at least Android 4.4 KitKat (19) and optimally Android 25 [OPTIONAL]

  ​

#### WARNING

Friendar 1.0 is developed in native Android, we take no responsibility for execution failure in other environments including iOS, Windows and/or Linux.



#### TEST INSTRUCTIONS (In Android Studio)

1. Clone our repository: https://github.com/COMP30022/Nitrogen-FrontEnd.git

2. Open in Android Studio and load the cloned repository

3. Press <kbd>Alt</kbd>-<kbd>Shift</kbd>-<kbd>F10</kbd> to open the run window

   ![run-as](C:\Users\tinba\Documents\GitHub\Nitrogen-FrontEnd\readme-resources\run-as.png)

4. Select "JunitTester" to run all unit tests

#### BUILD INSTRUCTIONS (In Android Studio)

1. On successful testing, press <kbd>Alt</kbd>-<kbd>Shift</kbd>-<kbd>F10</kbd> again to bring up the run window

2. Select "app"

3. Select suitable device, either emulated device or plugged in USB device

   ![deploy-as](C:\Users\tinba\Documents\GitHub\Nitrogen-FrontEnd\readme-resources\deploy-as.PNG)

   * **NOTE:** For first time setup of virtual device: [Creating Android Emulator](https://www.embarcadero.com/starthere/xe5/mobdevsetup/android/en/creating_an_android_emulator.html)
   * **NOTE:** For connected USB Android device: [Setup Android Hardware Device](https://developer.android.com/studio/run/device.html)

4. FriendAR will automatically install and run on the selected device

#### DEPLOY INSTRUCTIONS

Deployment is *automated* with FriendAR, so to deploy an `apk` file, it **must** go through Travis.

1. Using a shell command line (i.e Git Shell), branch from `master` and commit a change

2. Log into [Travis-ci.com](https://travis-ci.com/COMP30022/Nitrogen-FrontEnd) using GitHub credentials to view the build logs and rerun any previous builds. Commits, pushes and requests are automatically queued here

3. Submit a pull request onto `master` from this branch and get a review

4. Wait until Travis' tests and scripts have finished running

5. Merge the branch and will submit a merge request on Travis' servers

6. `app-debug.apk` will automatically be deployed onto COMP30022/Nitrogen-FrontEnd's releases page

   ![travis](C:\Users\tinba\Documents\GitHub\Nitrogen-FrontEnd\readme-resources\travis.PNG)

   An example of the raw log can be found [here](https://s3.amazonaws.com/archive.travis-ci.com/jobs/94046397/log.txt?AWSAccessKeyId=AKIAIETBFLRWUUPRBPHA&Expires=1507715069&Signature=3tHelKOgL%2FtubdcXak9hQeRNj04%3D) (#243).

   ​

#### FOR CONSUMERS (>Android 19 Device)

1. Download the latest source code `app-debug.apk` from the [Releases](https://github.com/COMP30022/Nitrogen-FrontEnd/releases) tab
2. Import into Android Device, running on at least Android 4.4 Kit-Kat
3. Ensure that the android device allows installation of apps from unknown sources
   * If not then please follow this guide: [Installing APK Files outside Google Play](https://www.cnet.com/au/how-to/how-to-install-apps-outside-of-google-play/)
4. Unpack and install the file, and will appear on the device's home screen
5. Tap the icon to open FriendAR




## Building

Friendar is built using Gradle targeting Android 4.4 Kit-Kat (Android 19) and optimally Android 25. Several dependencies are required and are specified in the `build.gradle` file. 

##### BUILD STATUS: [![Build Status](https://travis-ci.com/COMP30022/Nitrogen-FrontEnd.svg?token=p8yLcFuVj6kMWC4pZF7s&branch=master)](https://travis-ci.com/COMP30022/Nitrogen-FrontEnd)



## Integration, Testing & Releasing

Friendar is continuously integrated using Travis and tests are automatically run. Merge requests cannot be tested and Travis will give an indicator on passing builds and integrations. 

Testing is automated building the project on Travis' servers and if android and unit tests are successful, they are released as source code (`apk`) with Travis versioning. Released Here: [Friendar Releases](https://github.com/COMP30022/Nitrogen-FrontEnd/releases)



## Development Workflow

This repo uses [git flow](http://nvie.com/posts/a-successful-git-branching-model/).

This means, all new features are done on a new branch and then when ready pull requested into `master`. Note: for a branch to be successfully merged into master. It has to pass a code review and the tests on Travis.




## Sources and Thanks

https://bitbucket.org/apacha/sensor-fusion-demo/ by Alexander Pacha and the Human Technology Laboratory New Zealand, MIT licence.




## License

FriendAR - All rights reserved.
