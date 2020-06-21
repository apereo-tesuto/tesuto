[[ Docker Project Overview ]](../)
[[ Maketools Overview ]](./README.md) 
[[ Maketools Usage ]](./USAGE.md) 
[[ Maketools Setup ]](./SETUP.md)
***
## Versioning ##

Included with this image is a file named *VERSION*. This file contains key/value pairs that are used as default values for `make` targets thus standardizing the way that images and containers are created and named and, eliminating the need for specifying these values as part of the `docker` command.

Key        | Value                        | Description
-----------|------------------------------|------------------
REGISTRY   | docker.dev.ccctechcenter.org | Name of the target Docker register 
IMAGE_NAME | base-image                   | Name of the image
VERSION    | 1.0.0                        | 

For example, the following `make build` target 

```shell
$ make build OPT=--force-rm=true
```

has these underlying `docker` command entries 

```shell
    docker build $(OPT) -t $(REGISTRY)/$(IMAGE_NAME):$(VERSION) .
    docker tag -f $(REGISTRY)/$(IMAGE_NAME):$(VERSION) $(REGISTRY)/$(IMAGE_NAME):latest
```

and when executed, the variables are replaced with values from *VERSION* and any parameters specified on the command line

```shell
    docker build --force-rm=true -t docker.dev.ccctechcenter.org/base-image:1.0.0 .
    docker tag -f docker.dev.ccctechcenter.org/base-image:1.0.0 docker.dev.ccctechcenter.org/base-image:latest
```
***
[[ Docker Project Overview ]](../)
[[ Maketools Overview ]](./README.md) 
[[ Maketools Usage ]](./USAGE.md) 
[[ Maketools Setup ]](./SETUP.md)
