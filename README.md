# Library 

This should hold all of the common robot code to be developed and used throughout the years for our projects. I have split the document into what concerns collaborators and what concerns maintainers.

## Collaborators
If you're here you are a collaborator that is working on one of our robot projects that requires this library as a submodule.
### Cloning a project with git submodules
When running git clone on a repo with submodules pass the `--recurse-submodules` flag to git. <br>
`git clone URLTOREPO --recurse-submodules` <br>
### Using a project with git submodules
After you run git pull in a repo with submodules it only updates what commit they point to and does not update submodules themselves. To do that you must run this command after git pull. <br>
`git submodule update --init --recursive` <br>

However there is a config to make git automatically run this command after every pull; **I strongly recommend** setting this config. <br>
`git config --global submodule.recurse true` <br>
## Maintainers
Docs for using git submodules: https://git-scm.com/book/en/v2/Git-Tools-Submodules <br>
You should be updating the versions of GradleRIO and vendor dependencies in your projects so they match that of the library as new updates are released. 
### Adding the library to a project
**Important** - Before adding the library to a project you should have basic knowledge of git, gradle and navigating around directories. <br>
Make sure you're in the project's root directory in the terminal. <br>
![](imgs/terminal.png) <br>
To add library as a submodule. <br>
`git submodule add -b master https://github.com/Armada2508/Library library` <br>
Of course if you want to track commits from a different branch then replace master with a branch of your choice.

Add `implementation ':library'` to your dependencies block in build.gradle. <br>
Add `includeBuild 'library'` to your settings.gradle.

Make sure source and target compatibility in build.gradle match that of the library, right now it's Java 17. <br>
Make sure GradleRIO version matches. <br>
Make sure you have all vender deps installed for the library to work and that they're all up to date. Currently just CTRE Phoenix 6. <br>

**Important** - I would run `./gradlew build` before continuing. <br>

### Updating your submodules
To update the commit that the submodule points to on the branch specified in .gitmodules. (Pull from upstream)<br>
`git submodule update --remote` <br>
Every time you update the library and you want to advance the commit that your repo points to you must run this.
### Editing library from within a project
Once you want to start working on the library as a submodule from within another project you need to checkout a branch. <br>
Cd into the libary's directory. <br>
`git checkout branchname` <br>
If you've updated the submodule since the last time you checked out a branch you probably need to run `git pull` in the library's directory.

Add the merge flag to not delete your changes when pulling from upstream. <br>
`git submodule update --remote --merge`

Commit your changes and then when you want to push it, if you're at the root directory run this. <br>
`git push --recurse-submodules=on-demand`

Otherwise just cd into the directory and manually git push from there.
VSCode source control tab should help out with using submodules or you can stick with commands in the terminal, both work.
