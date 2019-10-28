[[ Docker Project Overview ]](../)
[[ Maketools Overview ]](./README.md) 
[[ Maketools Usage ]](./USAGE.md) 
[[ Versioning ]](./VERSIONING.md)
***

# Maketools Setup with Docker Projects #

In order to take advantage of the Maketools project, it must first be added to a new/existing Docker repo.  Most Docker repos will already contain a maketools folder however, new repos will need to have them added using the following commands.

## Add Maketools repo to new/existing Dockerfile repo  ##

### Add maketools remote to project ###

Before the maketools repo can be added to a current project, it must contain a reference to the maketools repo. 

From within the project root, issue this command

```shell
$ git remote add origin-maketools git@bitbucket.org:cccnext/maketools-docker.git
```

### Add maketools subtree ###

With the remote added, the maketools repo can be pulled into the existing project repo. 

In order to maintain the established Docker/Make conventions, the only value that should be changed here is the branch-name. Note that the maketools repo has a number of branches. If an existing branch does not suit the current project needs, create a new branch and push it to the maketools repo.

```shell
$ git subtree add --prefix=maketools origin-maketools <branch-name> --squash
```

In most cases, the master branch will be used.

```shell
$ git subtree add --prefix=maketools origin-maketools master --squash
$ git push
```

### Change to a different subtree branch ###

If the current project already contains a maketools folder but needs to point at a different branch, first remove the current maketools folder from the project and pull down the preferred branch.

```shell
$ git rm -r maketools
$ git commit -m 'Changing maketools branch'
$ git subtree add --prefix=maketools origin-maketools <branch-name> --squash
```

### Pulling changes from maketools remote ###

If new changes were committed to the maketools repo that aren't in the current project, they can be pulled in and committed.

```shell
git subtree pull --prefix=maketools origin-maketools <branch-name> --squash
git push
```

### Pushing changes to maketools remote ###

If changes were made to any files in the maketools folder, those changes can be pushed up to the current project repo as well as to a branch in the maketools repo. Use this with caution as there could be a number of Docker repos that rely on the Makefile in the current branch. New changes could have a negative impact on those repos.

```shell
git add <files>
git commit -m 'Make changes in maketools folder'
git push 
git subtree push --prefix=maketools origin-maketools <branch-name> --squash
```

***
[[ Docker Project Overview ]](../)
[[ Maketools Overview ]](./README.md) 
[[ Maketools Usage ]](./USAGE.md) 
[[ Versioning ]](./VERSIONING.md)
