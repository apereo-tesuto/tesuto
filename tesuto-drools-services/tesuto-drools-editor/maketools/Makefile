#-- Internal config --
MAKEFLAGS += --silent
.PHONY: attach build build-all clean containers diff exec images img login logs pause ps pull rename run shell start stop tag taglist test version
.DEFAULT_GOAL:=.DEFAULT
COLON:=:
SPACE:=
SPACE+=
RED=\033[0;31m
YELL=\033[0;33m
# No Color
NC=\033[0m

#-- File loads --
VERSION_INCLUDE:=VERSION
ifneq ($(strip $(wildcard VERSION.dev)),)
  VERSION_INCLUDE:=VERSION.dev
endif

DOCKER_FILE:=Dockerfile
ifneq ($(strip $(wildcard Dockerfile.dev)),)
  DOCKER_FILE:=Dockerfile.dev
endif
ifneq ($(strip $(wildcard Dockerfile.labeled)),)
  DOCKER_FILE:=Dockerfile.labeled
endif

DEV_STATE:=LIVE
DEV_STATE_MSG:="$(RED)LIVE$(NC)\n\nUse '$(YELL)make dev-init$(NC)' to switch to DEVELOPMENT\n"
ifeq ($(VERSION_INCLUDE),VERSION.dev)
ifeq ($(DOCKER_FILE),Dockerfile.dev)
  DEV_STATE:=DEV
  DEV_STATE_MSG:="$(RED)DEVELOPMENT$(NC)\n\nUse '$(YELL)make dev-clean$(NC)' to discard changes and switch to LIVE\n"
endif
endif

include $(subst $(SPACE),\ ,$(CURDIR))/$(VERSION_INCLUDE)
include $(subst $(SPACE),\ ,$(CURDIR))/JARINFO
include $(subst $(SPACE),\ ,$(CURDIR))/maketools/PROPERTIES

#-- Var prep --
IMAGE_TAG=$(VERSION)
ifdef TAG
  IMAGE_TAG=$(TAG)
endif

CONTAINER_NAME=$(IMAGE_NAME)
ifdef CNAME
	CONTAINER_NAME=$(CNAME)
endif

ifneq ($(REGISTRY),)
	IMAGE_FULL_NAME=$(REGISTRY)/$(IMAGE_NAME)
else
	IMAGE_FULL_NAME=$(IMAGE_NAME)
endif
IMAGE_FULL_NAME_TAG=$(IMAGE_FULL_NAME):$(IMAGE_TAG)

#-- Methods --
existError = (echo "\n$(RED)Run 'make clean' to remove existing, named container$(NC)\n")

#-- Targets --
attach:           # Attach to current running container [ Args: OPT,CNAME | OPT=--no-stdin=false ]
	docker attach $(OPT) $(CONTAINER_NAME)

build:            # Build image from current Dockerfile [ Args: OPT | OPT="--no-cache=true --rm=false" ]
	if [ -a target/$(JAR_NAME) ]; \
	then \
		chmod 751 target/$(JAR_NAME); \
		cp -f target/$(JAR_NAME) includes$(JAR_OUTPUT_PATH); \
	else \
		echo "$(RED)\n*** Missing ./target/$(JAR_NAME). Run maven to create jar before Docker build\n$(NC)"; \
		exit 1; \
	fi;

	$(eval TODAY:=$(shell date '+%Y%m%d_%H%M%S'))
	touch includes/kill_cache_$(TODAY)
	docker build --tag=$(IMAGE_FULL_NAME_TAG) $(OPT) --file=$(DOCKER_FILE) .
	rm includes/kill_cache_*

build-all:
	mvn clean package
	@$(MAKE) -f ./maketools/Makefile build

clean:            # Remove current container [ Args: OPT,CNAME | OPT="--volumes=true --link=true" ]
	docker rm --force=true $(OPT) $(CONTAINER_NAME)

containers:       # Alias for ps
	@$(MAKE) -f ./maketools/Makefile ps

diff:             # Inspect changes on a container's filesystem [ Args: CNAME]
	docker diff $(CONTAINER_NAME)

exec:             # Run a command in a running container [ Args: OPT,CMD,CNAME | OPT=-it CMD="echo hello-world" ]
	docker exec $(OPT) $(CONTAINER_NAME) $(CMD)

images:           # List images/tags for current image [ Args: OPT | OPT="-q --filter dangling=true" ]
	docker images --all=true $(OPT) $(IMAGE_FULL_NAME)

img:              # Alias for images
	docker images --all=true $(OPT) $(IMAGE_FULL_NAME)

login:            # Register or log in to a Docker registry [ Args: USER, OPT | USER=clawson OPT=--email=cl@gmail.com ]
	docker login --username=$(USER) $(OPT) $(REGISTRY)

logs:             # Fetch the logs of current container [ Args: OPT,CNAME | OPT=--tag=20 ]
	docker logs $(OPT) $(CONTAINER_NAME)

name:             # Shows the current registry, image name and tag from current VERSION file
	echo $(IMAGE_FULL_NAME_TAG)

pause:            # Pause all processes within a container [ Args: CNAME]
	docker pause $(CONTAINER_NAME)

ps:               # List current running containers [ Args: OPT | OPT=--latest ]
	docker ps --all=true $(OPT) | awk 'NR==1 || $$2=="$(IMAGE_FULL_NAME_TAG)" {print}'

pull:             # Pull image from current registry [ Args: OPT | OPT=--all-tags=true ]
	docker pull $(OPT) $(IMAGE_FULL_NAME_TAG)

push:             # Push image to current registry [ Args: OPT | OPT=--disable-content-trust=false ]
	docker push $(OPT) $(IMAGE_FULL_NAME_TAG)

release:          # Create the release branch and update the versions
	./maketools/scripts/release.sh
rename:           # Rename current container (NAMES not IMAGE) [ Args: CNAME,NEW_NAME | NEW_NAME=base-testing ]
	docker rename $(CONTAINER_NAME) $(NEW_NAME)

run:              # Run/start current container [ Args: OPT,CMD,CNAME | OPT="-it -v ~/data:data" CMD=/bin/bash ]
	docker run --name="$(CONTAINER_NAME)" $(OPT) $(IMAGE_FULL_NAME_TAG) $(CMD) || $(existError)
compose:
	./maketools/scripts/maketools.sh "compose" "$(IMAGE_FULL_NAME_TAG)" "$(CONTAINER_NAME)" "$(OPT)" "$(COMPOSE_FILE)"
compose-up:
	./maketools/scripts/maketools.sh "compose-up" "$(IMAGE_FULL_NAME_TAG)" "$(CONTAINER_NAME)" "" "$(COMPOSE_FILE)"
compose-down:
	./maketools/scripts/maketools.sh "compose-down" "$(IMAGE_FULL_NAME_TAG)" "$(CONTAINER_NAME)" "" "$(COMPOSE_FILE)"

setname:          # Override VERSION file [ Args: NAME | NAME=registry/name:tag | NAME=name:tag | NAME=name (latest) ]
	./maketools/scripts/maketools.sh "setname" "$(NAME)"

shell:            # Shell into current container. Alias for `make exec OPT=-it CMD=/bin/bash` [ Args: OPT,CNAME | OPT="-t" ]
	docker exec --interactive=true --tty=true $(OPT) $(CONTAINER_NAME) /bin/bash

start:            # Start current container [ Args: OPT,CNAME | OPT=--attach=true ]
	docker start $(OPT) $(CONTAINER_NAME)

stop:             # Stop current container [ Args: OPT,CNAME | OPT=--time=20 ]
	docker stop $(OPT) $(CONTAINER_NAME)

tag:              # Tag current image with given TAG [ Args: TAG | TAG=1.1.0-local ]
ifndef TAG
	echo "\n$(RED)TAG must be specified.$(NC)\nusage: make tag TAG=<tag>\n"
	false
endif
	docker tag $(IMAGE_FULL_NAME):$(VERSION) $(IMAGE_FULL_NAME_TAG)

taglist:          # Return list of current image tags from registry [ Args: CNAME ]
ifndef USER
	echo "\n$(RED)USER must be specified.$(NC)\nusage: make listtags USER=<username>\n"
	false
endif
	curl -su '$(USER)' https://$(REGISTRY)/v2/$(CONTAINER_NAME)/tags/list

test:             # Starts current container and echo's a string to ensure that its runnable [ Args: CNAME ]
	docker run --name="$(CONTAINER_NAME)-TESTRUN" $(IMAGE_FULL_NAME_TAG) /bin/echo '-= Container started successfully =-' || $(existError)
	docker rm -f $(CONTAINER_NAME)-TESTRUN > /dev/null

top:              # Display the running processes of a container [ Args: CNAME ]
	docker top $(CONTAINER_NAME)

unpause:          # Unpause all processes within a container [ Args: CNAME ]
	docker unpause $(CONTAINER_NAME)

remote-maketools: # Adds git remote for maketools repo
	git remote add origin-maketools git@bitbucket.org:cccnext/maketools-docker.git

update-maketools: # Pulls down the latest Maketools updates
	git subtree pull --prefix=maketools origin-maketools $(CURRENT_BRANCH) --squash --message 'Updated maketools to latest version'

version-maketools: # Checks the maketools version of the current image repo against the latest maketools
	./maketools/scripts/maketools.sh "check-ver"

#-- Dev Targets --
dev-init:         # Enter 'dev' mode in local environment. Allows local changes to Dockerfile.dev and VERSION.dev
ifeq ($(DEV_STATE),DEV)
	echo "$(RED)Already in DEVELOPMENT mode. Nothing to do.$(NC)"
else
	sed -e '/VERSION.*=/ s/=.*/=latest/' VERSION > VERSION.dev
	cp Dockerfile Dockerfile.dev
endif

dev-apply:        # Backs up then overwrites Dockerfile and VERSION with .dev copies then exits 'dev' mode
ifeq ($(DEV_STATE),LIVE)
	echo "$(RED)Already in LIVE mode. Nothing to do.$(NC)"
else
	cp Dockerfile Dockerfile.bak
	cp Dockerfile.dev Dockerfile
	cp VERSION VERSION.bak
	cp VERSION.dev VERSION
	$(MAKE) dev-clean
endif

dev-clean:        # Exits 'dev' mode. All dev changes are removed.
	rm -f VERSION.dev
	rm -f Dockerfile.dev

dev-status:       # Shows the current mode
	echo "\nCurrent mode: "$(DEV_STATE_MSG)

help: # Prints this message
	printf '\n'
	cat Makefile maketools/Makefile | grep "^[a-z-]*$(COLON)" | sed "s/$(COLON).*#\s*/	/g" | sort -u -t '	' -k 1,1 | column -s '	' -t
	printf '\n\nUse `make -n <target>` to view the command to be executed without actually executing it.\n\n'
.DEFAULT: help
