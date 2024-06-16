# MoreTools
A simple, server-side fabric mod which adds some more tools.  
Currently being maintained for 1.20.1, 1.20.6 and 1.21.

### Usage
Throw it into the `mods` folder.  
  
Because this is a Polymer-based mod, resources have to be generated before usage.  
Simply run `/polymer generate-pack` and copy the resulting resource pack into your resourcepacks folder, or if you host a fabric server, distribute it to your players.  
You should probably use Minecraft's integrated `server-resource-pack` feature for that, check the MC wiki if you don't know how to make that work.  

### Building
First clone this repository, making sure to checkout the correct branch for your target version.
Then just let Gradle do its thing (just use JetBrains IntelliJ IDEA if you don't know how).  
Then simply execute your desired Gradle task, like `build` to generate a JAR in `build/libs/`.

### License
Code is licensed under the MIT license.   
This project is powered by [Polymer](https://github.com/Patbox/polymer).
