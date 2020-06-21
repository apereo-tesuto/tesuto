#!/bin/bash

# Assumes that this script will be sitting "down" from the VERSION file in [project]/makefiles/scripts

## Function finds the current VERSION string from the VERSION properties file in the root of the project
findStr()
{
    local target=VERSION
    local file=VERSION
    sed '/^\#/d' ${file} | grep ${target} | sed -e 's/ //g' |
        while read LINE
        do
            local KEY=`echo $LINE | cut -d "=" -f 1`
            local VALUE=`echo $LINE | cut -d "=" -f 2`
            [ ${KEY} == ${target} ] && {
                local UNKNOWN_NAME=`echo $VALUE | grep '\${.*}' -o | sed 's/\${//' | sed 's/}//'`
                if [ $UNKNOWN_NAME ];then
                    local UNKNOWN_VALUE=`findStr ${UNKNOWN_NAME} ${file}`
                    echo ${VALUE} | sed s/\$\{${UNKNOWN_NAME}\}/${UNKNOWN_VALUE}/
                else
                    echo $VALUE
                fi
                return 
            }
        done
    return
}

# versions
snapshot=-SNAPSHOT
snapshotVersionLabel=$(findStr)
versionLabel=${snapshotVersionLabel%$snapshot}

# establish branch and tag name variables
devBranch=develop
masterBranch=master
releaseBranch=release/$versionLabel

# create the release branch from the develop branch
git fetch
git checkout -b $releaseBranch $devBranch
git push --set-upstream origin $releaseBranch

# Update the version number in the release branch by removing the -SNAPSHOT
awk -F"=" -v newval="$versionLabel" 'BEGIN{OFS="=";} /VERSION/{$2=newval;print;next}1' VERSION > VERSIONTMP
mv VERSIONTMP VERSION

# Commit and push changes
git add VERSION
git commit -m "RELEASE version $versionLabel"
git push

# merge release branch with the new version number into master
git checkout $masterBranch
git merge --no-ff -m "Merging $releaseBranch" $releaseBranch
 
# create tag for new version from -master
git tag $versionLabel
git push origin --tags

# Update the version in develop to next revision
git checkout $devBranch

# Make an array of the version values
versionPoints=( ${versionLabel//./ } )

# Increment the minor version number
((versionPoints[1]++))

# The patch number becomes zero followed by -SNAPSHOT
newVersion="${versionPoints[0]}.${versionPoints[1]}.0-SNAPSHOT"
awk -F"=" -v newval="$newVersion" 'BEGIN{OFS="=";} /VERSION/{$2=newval;print;next}1' VERSION > VERSIONTMP
mv VERSIONTMP VERSION

git add VERSION
git commit -m "NOJIRA updating develop to new version - $versionLabel"
git push
