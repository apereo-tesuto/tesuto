[[ Docker Project Overview ]](../)
[[ Maketools Usage ]](./USAGE.md) 
[[ Maketools Setup ]](./SETUP.md) 
[[ Versioning ]](./VERSIONING.md)
***
# Makefile Overview #

## Overview ##

The main goal of the Maketools Docker project is to provide a simple, repeatable and consistent way to build and operate on Docker images and containers. Aassuming they have the proper docker and make clients installed on their system, any person who clones a repo containing a Dockerfile and maketools folder should be able to execute `make build` to build the underlying Dockerfile without referring to any documentation. 

In addition, the underlying Makefile provides documentation for operating on images and containers. Looking at the contents of the Makefile will show how the author intended for the container to be run. This also ensures consistent naming and versioning of images and containers by reducing the number of parameters a builder needs to issue on the command line. 

### Example ###

Maintaining make tool consistency across such a large number of Docker projects poses a real challenge. The Maketools Docker project is an attempt to ensure that changes to underlying Makefiles can easily be tracked and propagated to all of the Docker projects with minimal effort.

The intent is to have a common Makefile housed in the `master` branch. If a particular Docker project must deviate from the `master` branch, a new branch should be created and given a name that closely matches the Docker project or intent while supporting the possibility for re-use amongst other Docker projects.

To give an example, the `run` target in the Makefile on `master` contains the following `docker run` command.

```shell
docker run --name="base-image" my-namespace/base-image:1.0.0
```

If however, the current Docker project must always specify other options or commands, a new branch, such as `cool-command`, should be created to house those changes. The Docker project should then reference `cool-command` for its maketools folder.

```shell
docker run -it -v /var/run/docker.sock:/var/run/docker.sock my-namespace/cool-command:latest
```

See [Maketools Usage](./USAGE.md) for more details.

***
[[ Docker Project Overview ]](../)
[[ Maketools Usage ]](./USAGE.md) 
[[ Maketools Setup ]](./SETUP.md) 
[[ Versioning ]](./VERSIONING.md)
