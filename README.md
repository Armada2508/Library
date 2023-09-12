# Library 

This should hold all of the common robot code to be developed and used throughout the years for our projects. 

## Adding to a Project
Make sure you're in the project's top directory.
To add library as a submodule. <br>
`git submodule add https://github.com/Armada2508/Library library`

Add `implementation project(':library')` to your dependencies block in build.gradle. <br>
Add `include ':library'` to your settings.gradle.

Make sure source and target compatibility in build.gradle match that of the library, right now it's Java 17.

Make sure you have all vender deps installed for the library to work. Currently just CTRE Phoenix.

To pull updates from upstream. <br>
`git submodule update --remote`


## Developing Library
When adding new code that requires other wpilib namespaces you need to add an implementation for it in the dependencies block like so.

`implementation wpilibTools.deps.wpilibJava("NAME")` <br>
For other dependencies just treat it like normal gradle.
