# Stop spamming my Chat (SSMC)
A mod for Minecraft which allows You to filter for any chat messages and either ignore them completely or show them e.g. in the action bar or as a toast.

## Features
This mod is meant to filter out annoying automated chat messages from servers and mitigate the cluttering of chat so player messages can be read more easily.

Therefore it has the following features:
 - Match chat messages using RegEx
 - Show chat messages
    - in the action bar,
    - as a title,
    - as a subtitle,
    - as a toast or
    - not at all
 - Filter chat messages from the game output log
 - InGame config accessible from the chat screen
 - Virtually unlimited filters

## Releases
Any distributor claiming to be releasing **OFFICIAL** binaries of this mod may have built the release themself and may have introduced modifications to the original source code which could harm Your system or steal Your data/personal information. Only download and execute software from a source You can and do trust.

## Build instructions
Clone the repository and run
```bash
./gradlew init
```
followed by
```bash
./gradlew build
```
You may have to change the Java home path at the bottom of `gradle.properties`. The [Fabric Wiki](https://fabricmc.net/wiki/tutorial:setup) can provide You with further instructions if You are getting stuck here.

## Installation
Once You have built the mod, You can drag it into the mods folder of Your Minecraft installation. Please note that this is a Fabric mod and it requires the Fabric API in order for it to work.

## Issues
Found any bugs, unintended behavior, leaked IPs or violations of common coding conventions? Please let me know by opening an issue! **Remember to be respectful.**