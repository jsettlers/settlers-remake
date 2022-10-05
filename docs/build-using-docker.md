# Build Using Docker

<!-- TOC depthFrom:2 -->

- [Linux Build Instructions](#linux-build-instructions)
- [Windows Build Instructions](#windows-build-instructions)

<!-- /TOC -->

To avoid installing and configuring all the tools such as the Oracle JDK locally on your machine you can use Docker to build the desktop variant and dedicated server of "JSettlers" in an isolated environment.

## Linux Build Instructions

Run the following commands on Linux based systems:

```
git clone https://github.com/jsettlers/settlers-remake.git
cd settlers-remake

docker run -it --rm -e "GRADLE_OPTS=-Xmx600m -Dorg.gradle.jvmargs=-Xmx1500m" -v "${PWD}":/home/gradle gradle:jdk8 ./gradlew releaseJSettlers releaseDedicatedServer --stacktrace
```

The generated release files will be copied into the `./release` folder

## Windows Build Instructions

Run the following commands on Windows:

> Note: Be extra careful to not forget the `--config core.autocrlf=input` argument when cloning the repository on Windows as otherwise the Docker build will fail. If you've cloned already the repository without this argument you've to delete and clone it again.

```
git clone https://github.com/jsettlers/settlers-remake.git --config core.autocrlf=input
cd settlers-remake

docker run -it --rm -e "GRADLE_OPTS=-Xmx600m -Dorg.gradle.jvmargs=-Xmx1500m" -v %cd%:/home/gradle gradle:jdk8 ./gradlew releaseJSettlers releaseDedicatedServer --stacktrace
```

The generated release files will be copied into the `./release` folder