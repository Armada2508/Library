# Library 

This should hold all of the common robot code to be developed and used throughout the years for our projects. You should be updating the versions of GradleRIO and vendor dependencies at the same rate as projects so the versions always match. 

### Cloning a project with git submodules
When running git clone on a repo with submodules pass the `--recurse-submodules` flag to git. <br>
`git clone URLTOREPO --recurse-submodules`
## Adding the library to a project
**IMPORTANT** - Before adding the library to a project you should have basic knowledge of git, gradle and navigating around directories. <br>
Make sure you're in the project's root directory in the terminal. <br>
![](imgs/topdirectory.png) <br>
![](imgs/terminal.png) <br>
To add library as a submodule. <br>
`git submodule add -b master https://github.com/Armada2508/Library library` <br>
Of course if you want to track commits from a different branch then replace master with a branch of your choice.

Add `implementation ':library'` to your dependencies block in build.gradle. <br>
Add `includeBuild 'library'` to your settings.gradle.

Make sure source and target compatibility in build.gradle match that of the library, right now it's Java 17. <br>
Make sure GradleRIO version matches. <br>
Make sure you have all vender deps installed for the library to work and that they're all up to date. Currently just CTRE Phoenix. <br>

**Important** - I would run `./gradlew build` before continuing. <br>

### Using a project with git submodules
Docs for using git submodules: https://git-scm.com/book/en/v2/Git-Tools-Submodules

To update the commit that the submodule points to on the branch specified in .gitsubmodules. (Pull from upstream)<br>
`git submodule update --remote` <br>
Every time you update the library and you want to advance the commit that your repo points to you must run this.
## Editing library from within a project
***This only applies if you have write access to the library in the first place, otherwise you would just fork and pull request as normal.*** <br>
Once you want to start working on the library as a submodule from within another project you need to checkout a branch. <br>
`git checkout master` <br>
If you've updated the submodule since the last time you checked out a branch you probably need to run `git pull` in the library's directory.

Add the merge flag to not delete your changes when pulling from upstream. <br>
`git submodule update --remote --merge`

Commit your changes and then when you want to push it, if you're at the root directory run this. <br>
`git push --recurse-submodules=on-demand`

Otherwise just cd into the directory and manually git push from there.
VSCode source control tab will help out a lot with using submodules so you can rely on that.
