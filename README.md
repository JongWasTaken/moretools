# MoreTools
A simple, server-side fabric mod which adds some more tools.

### Usage
Just like every other fabric mod.  

### Building
Resources are generated at compile-time using [JMC](https://jmc.wingedseal.com/).  
The Linux version of JMC is included, but if you are on Windows,  
you will have to download the Windows version yourself and put it in `src/main/resources/jmc/`.
You might have to adjust the IDEA tasks as well, since JMC runs as a pre-build task.  
Run `jmc compile` in the `src/main/resources/jmc/` directory to manually generate a datapack.  
  
Finally, use IntelliJ IDEA and let Gradle do its thing.  
Then simply execute your desired task, like `build` to generate a JAR in `build/libs/`.

### License
Code is licensed under the MIT license.   
This project is powered by [Polymer](https://github.com/Patbox/polymer).