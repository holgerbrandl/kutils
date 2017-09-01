## Release Checklist

# 1. Increment version in `kutils`
# 2. Make sure that support api version is up to date and available from jcenter
# 3. Push and wait for travis CI results

export KUTILS_HOME="/Users/brandl/projects/kotlin/kutils";

trim() { while read -r line; do echo "$line"; done; }
kutils_version=$(grep '^version' ${KUTILS_HOME}/build.gradle | cut -f2 -d'=' | tr -d "'" | trim)
echo "new version is $kutils_version"
## see https://github.com/aktau/github-release


########################################################################
### Build the binary release


cd ${KUTILS_HOME}

gradle install
# careful with this one!
#gradle bintrayUpload


########################################################################
### Do the github release

## create tag on github 
#github-release --help

source /Users/brandl/Dropbox/archive/gh_token.sh
export GITHUB_TOKEN=${GH_TOKEN}
#echo $GITHUB_TOKEN

# make your tag and upload
cd ${KUTILS_HOME}

#git tag v${kutils_version} && git push --tags
(git diff --exit-code && git tag v${kutils_version})  || echo "could not tag current branch"
git push --tags

# check the current tags and existing releases of the repo
github-release info -u holgerbrandl -r kutils

# create a formal release
github-release release \
    --user holgerbrandl \
    --repo kutils \
    --tag "v${kutils_version}" \
    --name "v${kutils_version}" \
    --description "See [NEWS.md](https://github.com/holgerbrandl/kutils/blob/master/NEWS.md) for changes." 
#    \
#    --pre-release


## upload sdk-man binary set
github-release upload \
    --user holgerbrandl \
    --repo kutils \
    --tag "v${kutils_version}" \
    --name "kutils-${kutils_version}-bin.zip" \
    --file ${kutils_ARCHIVE}/kutils-${kutils_version}.zip


