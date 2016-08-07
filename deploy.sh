#!/bin/bash
set -e # Exit with nonzero exit code if anything fails

SOURCE_BRANCH="master"
TARGET_BRANCH="gh-pages"

function doCompile {
  GRADLE_OPTS='-Xmx600m -Dorg.gradle.jvmargs="-Xmx1500m"' ./gradlew release
}

# Pull requests and commits to other branches shouldn't try to deploy, just build to verify
#if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_BRANCH" != "$SOURCE_BRANCH" ]; then
#    echo "Skipping deploy; just doing a build."
#    doCompile
#    exit 0
#fi

# Save some useful information
REPO=`git config remote.origin.url`
SSH_REPO=https://github.com/andreas-eberle/settlers-remake.git
SHA=`git rev-parse --verify HEAD`

# Clone the existing gh-pages for this repo into out/
# Create a new empty branch if gh-pages doesn't exist yet (should only happen on first deply)
git clone $REPO release
cd release
git checkout $TARGET_BRANCH || git checkout --orphan $TARGET_BRANCH
cd ..

echo "1"

# Clean out existing contents
rm -rf release/**/* || exit 0

# Run our compile script
doCompile

# Now let's go have some fun with the cloned repo
cd release
git config user.name "Travis CI"
git config user.email "email@andreas-eberle.com"

echo "2"

# If there are no changes to the compiled out (e.g. this is a README update) then just bail.
if [ -z `git diff --exit-code` ]; then
    echo "No changes to the output on this push; exiting."
    exit 0
fi

# Commit the "changes", i.e. the new version.
# The delta will show diffs between new and old versions.
git add .
git commit -m "Deploy to GitHub Pages: ${SHA}"

echo "3"

# Get the deploy key by using Travis's stored variables to decrypt deploy_key.enc
openssl aes-256-cbc -K $encrypted_af9c5a2dd85c_key -iv $encrypted_af9c5a2dd85c_iv -in ../deploy_key.enc -out deploy_key -d
chmod 600 deploy_key
eval `ssh-agent -s`
ssh-add deploy_key

echo "4"

# Now that we're all set up, we can push.
git push $SSH_REPO $TARGET_BRANCH