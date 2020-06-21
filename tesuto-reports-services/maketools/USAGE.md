[[ Docker Project Overview ]](../)
[[ Maketools Overview ]](./README.md) 
[[ Maketools Setup ]](./SETUP.md) 
[[ Versioning ]](./VERSIONING.md)
***
# Maketools Usage ##

Upon cloning a Docker project with maketools, issuing a `make` command followed by a target and any optional parameters is all that is needed for working with Docker images and containers associated with the project repo.

## Examples ##

All make parameters must be specified in uppercase. Use quotes `'` or `"` to surround multiple parameter values or those with spaces.

```shell
$ make <target> OPT='{additional docker options}' TAG|USER|CMD=value
```

Log in to registry specified in VERSION file
```shell
$ make login USER=clawson
```

Pull all tagged images from registry using details in VERSION file
```shell
$ make pull OPT='--all-tags=true'
```

Execute a command on a running container
```shell
$ make exec OPT=-it CMD=/bin/bash
```

Rename a currently running container that was started with `make run`.
```shell
$ make rename NEW_NAME=my-new-container-name
```

Work with the newly renamed container using maketools.
```shell
$ make stop NAME=my-new-container-name
```

Rename a previously renamed container
```shell
$ make rename NAME=my-new-container-name NEW_NAME=original-name
```

Specify `-n` or `--dry-run` to show the command that will execute without actually running it.

```shell
$ make --dry-run tag TAG=spring-2016-demo
```

Outputs the following but does not actually execute it
```shell
docker tag docker.dev.ccctechcenter.org/image docker.dev.ccctechcenter.org/image:spring-2016-demo
```

## Make targets ##

Included with this images is a file named *Makefile*. Together with *VERSION*, they allow for easy command line interaction with the image and container. Following is a list of supported ```make``` actions.

Target             | Parameters        | Description
-------------------|-------------------|------------
{null}             |                   | Lists available targets. Also, tab completion will list targets.
`attach`           | `OPT`,`CNAME`     | Attaches to the running container specified in *VERSION*.
`build`            | `OPT`,`TAG`       | Builds image tagged as 'latest' and with version specified in *VERSION*, when TAG not specified.
`build-all`        | `OPT`,`TAG`       | Executes `mvn clean package` then calls the `build` target to create the image.
`clean`            | `OPT`,`CNAME`     | Will attempt to remove any container with the name specified in *VERSION*.
`compose`          | `OPT`, `COMPOSE_FILE` | Wrapper around docker-compose command that uses provided compose file with values from *VERSION*. Use OPT to specify options.
`compose-down`     | `COMPOSE_FILE`    | Uses provided compose file to bring down services using values from *VERSION*
`compose-up`       | `COMPOSE_FILE`    | Uses provided compose file to bring up services using values from *VERSION*
`containers`       |                   | Alias for ```ps```.
`dev-apply`        |                   | Makes a backup of the LIVE Dockerfile then moves the DEVELOPMENT version to LIVE.
`dev-clean`        |                   | Removes the DEVELOPMENT files and returns folder to LIVE mode.
`dev-init`         |                   | Creates DEVELOPMENT copies of Dockerfile and VERSION and enters DEVELOPMENT mode.
`dev-status`       |                   | Displays the current mode, either DEVELOPMENT or LIVE.
`diff`             | `CNAME`           | Lists all file system additions, deletions and changes since container started.
`exec`             | `OPT`,`CMD`,`CNAME`| Execute specified command in running container whose name is specified in *VERSION*.
`help`             |                   | Lists all targets with descriptions.
`images`           |                   | Returns a list of images & tags for the image specified in *VERSION*.
`img`              |                   | Alias for ```images```
`login`            | `OPT`,`USER*`     | Logs in to registry specified in *VERSION*. USER is pulled from env however, can be overridden on cli.
`logs`             | `OPT`,`CNAME`     | Displays the logs for the image name specified in *VERSION*.
`name`             |                   | Returns the full image name and tag specified in *VERSION*.
`pause`            | `CNAME`           | Pauses the running container whose name is specified in *VERSION*.
`ps`               |                   | Returns a list of containers for the image specified in *VERSION*.
`pull`             | `OPT`,`TAG`       | Pull the image from the registry specified in *VERSION*.
`push`             | `OPT`,`TAG`       | Push the image to the registry specified in *VERSION*.
`rename`           | `NEW_NAME`,`CNAME`| Renames a currently running container to value specified with NEW_NAME. It's also possible to rename a renamed container with the NAME option.
`run`              | `OPT`,`TAG`,`CMD`,`CNAME` | Runs the image and tag specified in *VERSION* if TAG not specified.
`setname`          | `NAME`            | Override VERSION file. 
`shell`            | `OPT`,`CNAME`     | Starts /bin/bash in running container.
`start`            | `OPT`,`CNAME`     | Starts the stopped container whose name is specified in *VERSION*.
`stop`             | `OPT`,`CNAME`     | Stops the running container whose name is specified in *VERSION*.
`tag`              | `TAG`             | TAG required. Tags the image with specified tag.
`taglist`          | `USER*`,`CNAME`   | USER required. Returns a json object with tags for image name specified in VERSION. USER is pulled from env however, can be overridden on cli.
`test`             | `TAG`,`CNAME`     | Attempt to start container and execute an echo from shell.
`top`              | `CNAME`           | 
`unpause`          | `CNAME`           | 
`remote-maketools` |                   | Added git remote for maketools origin
`update-maketools` |                   | Fetches and merges any updates made in origin-maketools.
`version-maketools` |                  | Checks the maketools version of the current image repo against the latest maketools

Parameters marked with ***** are required, all others are optional.

## Parameters ##

Parameter | Description
----------|------------
`CMD`     | Specify the command to execute for those targets supporting this parameter.
`CNAME`   | Specify the name to use when running and interacting with a container.
`NAME`    | Specify full image name to override values in *VERSION*. Works with `setname` only. NAME=registry/nametag | NAME=nametag | NAME=name (latest)
`NEW_NAME`| Specify a new name for a currently running container.
`OPT`     | Specify additional options not provided by the target default. [See Docker CLI pages for supported options](https://docs.docker.com/v1.8/reference/commandline/cli/)
`TAG`     | Specify an alternate tag than the default pulled from *VERSION*
`USER`    | Specify the user name for the registry specified in *VERSION*

# Development Mode #

The default mode for the project is *LIVE* which means that Makefile targets operate on the live versions of the *Dockerfile* and *VERSION*. When entering *DEVELOPMENT* mode dev copies of both the *Dockerfile* and *VERSION* are created automatically and the *VERSION* is set to *latest*. When in *DEVELOPMENT* mode the *Makefile* operates on the dev versions of these files. Developers can make any changes necessary to *Dockerfile.dev* and *VERSION.dev* for testing and enhancement. 

## Dev Init ##

```shell
$ make dev-init
```

This target copies of the existing *Dockerfile* and *VERSION* are created with *.dev* extensions. In addition, the *VERSION* within *VERSION.dev* is changed to *latest* so that the *Makefile* will use the image and container tagged *latest* 

A good use-case for this features is if the developer needs to target a different version of the base images. This can be accomplished by changing the that specified on the *FROM* line in *Dockerfile.dev*.

Both *Dockerfile.dev* and *VERSION.dev* are added to *.gitignore* so there is no chance for them to be committed to the repo.

## Dev Clean ##

```shell
$ make dev-clean
```

This target simple deletes the *Dockerfile.dev* and *VERSION.dev*. Any changes made to these files will be lost.

## Dev Apply ##

```shell
$ make dev-apply
```

This target moves changes made in *Dockerfile.dev* to the *LIVE* *Dockerfile*. It first makes a backup of the existing *Dockerfile* called *Dockerfile.bak*. It then moves *Dockerfile.dev* to *Dockerfile*.

## Dev Status ##

```shell
$ make dev-status
```

This target shows the current mode by simply looking for the existence of the *Dockerfile.dev* and *VERSION.dev*. An alternative to this command is to issue an `ls` and manually look for these files.

* When both files exist, the project is in *DEVELOPMENT* mode and the developer is free to make changes to the *.dev* files.

* When one or both files are missing, the project is in *LIVE* mode.

***
[[ Docker Project Overview ]](../)
[[ Maketools Overview ]](./README.md) 
[[ Maketools Setup ]](./SETUP.md) 
[[ Versioning ]](./VERSIONING.md)
