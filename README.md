# Library 

This should hold all of the common robot code to be developed and used throughout the years for our projects. You should be updating the versions of GradleRIO and vendor dependencies at the same rate as projects so the versions always match.

## Adding to a Project
Make sure you're in the project's top directory.
To add library as a submodule. <br>
`git submodule add https://github.com/Armada2508/Library library`

Add `implementation ':library'` to your dependencies block in build.gradle. <br>
Add `includeBuild 'library'` to your settings.gradle.

Make sure source and target compatibility in build.gradle match that of the library, right now it's Java 17.
Make sure GradleRIO version matches.

Make sure you have all vender deps installed for the library to work and that they're all up to date. Currently just CTRE Phoenix.

To pull updates from upstream. <br>
`git submodule update --remote`