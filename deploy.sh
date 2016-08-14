#!/bin/bash

set -e # Exit with nonzero exit code if anything fails

TARGET_BRANCH="gh-pages"

function doCompile {
  GRADLE_OPTS='-Xmx600m -Dorg.gradle.jvmargs="-Xmx1500m"' ./gradlew release
}

# Commits to other branches than master shouldn't be deployed
#if [ "$TRAVIS_BRANCH" != "master" ]; then
#    echo "This is the build of a branch => Skipping deploy;"
#    exit 0
#fi


if [ -z "$encrypted_af9c5a2dd85c_key" ]; then 
	echo "No encryption key for deploy provided => Skipping deplo;"
	exit 0;
fi

# Save some useful information
REPO=`git config remote.origin.url`
SSH_REPO=git@github.com:jsettlers/settlers-nightlies.git
SHA=`git rev-parse --short=7 --verify HEAD`


cd settings

# Get the deploy key by using Travis's stored variables to decrypt deploy_key.enc
openssl aes-256-cbc -K $encrypted_af9c5a2dd85c_key -iv $encrypted_af9c5a2dd85c_iv -in deploy_key.enc -out deploy_key -d
chmod 600 deploy_key
eval `ssh-agent -s`
ssh-add deploy_key

cd ..


# Run our compile script
doCompile


# Clone the existing gh-pages for this repo into out/
# Create a new empty branch if gh-pages doesn't exist yet (should only happen on first deply)

rm -rf gh-pages

git clone $SSH_REPO --branch $TARGET_BRANCH --single-branch gh-pages

# Create the folder name
if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then 
	FOLDER="pullRequests/$TRAVIS_PULL_REQUEST"

elif [ -n "$TRAVIS_TAG" ]; then
	FOLDER="tags/$TRAVIS_TAG"

elif [ "$TRAVIS_BRANCH" == "master" ]; then
	FOLDER="master"
	
else
	FOLDER="branch/$TRAVIS_BRANCH"
	
fi

DATE=`date +%Y-%m-%d_%H-%M-%S`
FOLDER="gh-pages/$FOLDER/"
mkdir -v -p "$FOLDER"


# Copy the release files into the folder
cp -R release/ "$FOLDER/${DATE}__$SHA/"

cd gh-pages


# Set git config
git config user.name "Travis CI"
git config user.email "travis@jsettlers"


# Commit the "changes", i.e. the new version.
git add .
git commit -m "Deploy to GitHub Pages: ${SHA}"


# Now that we're all set up, we can push.
git push $SSH_REPO $TARGET_BRANCH