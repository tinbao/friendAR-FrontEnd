# Nitrogen-FrontEnd
Front End build of the FRIENDAR mobile application developed in native android and java.

### Features

* Augmented Reality Friend Finding
* Group Communications
* "*I'm Feeling Lucky*" Notable Location Search



### Install Instructions

* Prerequisite Installs
  * Java JDK 8.0
  * Android Studio (will install Android 25, we are running 19)
  * JUnit 4.12 (Android Studio will install this)
  * Volley 1.0 (Android Studio will install this)

##### WARNING

Friendar 1.0 is developed in native Android, we take no responsibility in execution failure in other environments including iOS, Windows and/or Linux.

##### FOR DEVELOPERS

* From GitHub clone the repository: https://github.com/COMP30022/Nitrogen-FrontEnd.git
* Open in Android Studio and ensure that VCS is connected to GitHub properly

##### FOR CONSUMERS

* Download the source code from the Releases tab (specified below)
* Import into Android Device, running on at least Android 4.4 Kit-Kat
* Unpack the folder and open in applications manager



## Building

Friendar is built using Gradle using native Android 4.4 Kit-Kat (Android 19). Several dependencies are required and are specified in the `build.gradle` file. 

##### BUILD STATUS: [![Build Status](https://travis-ci.com/COMP30022/Nitrogen-FrontEnd.svg?token=p8yLcFuVj6kMWC4pZF7s&branch=master)](https://travis-ci.com/COMP30022/Nitrogen-FrontEnd)



## Integration, Testing & Releasing

Friendar is continuously integrated using Travis and tests are automatically run. Merge requests cannot be tested and Travis will give an indicator on passing builds and integrations. 

Testing is automated building the project on Travis' servers and if tests are successful, they are released as source code (`apk`) with Travis versioning. Released Here: [Friendar Releases](https://github.com/COMP30022/Nitrogen-FrontEnd/releases)



## Development Workflow

This repo uses [git flow](http://nvie.com/posts/a-successful-git-branching-model/).

This means, all new features are done on a new branch and then when ready pull requested into `master`. Note: for a branch to be successfully merged into master. It has to pass a code review and the tests on Travis.


## Sources and Thanks

https://bitbucket.org/apacha/sensor-fusion-demo/ by Alexander Pacha and the Human Technology Laboratory New Zealand, MIT licence.


## License

All rights reserved.