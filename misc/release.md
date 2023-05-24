Release Checklist
-----------------

## Release Checklist

1. Update version in build.gradle and README

2. Do the central release

```bash
#  cd /c/brandl_data/projects/misc/kutils
 
./gradlew jar

./gradlew publishToMavenLocal

#./gradlew publishToSonatype closeSonatypeStagingRepository
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

3. Do the github source release

```bash
# cd /d/projects/misc/kutils


trim() { while read -r line; do echo "$line"; done; }
kutils_version='v'$(grep '^version' build.gradle | cut -f2 -d' ' | tr -d "'" | trim)

echo "new version is $kutils_version"



if [[ $kutils_version == *"-SNAPSHOT" ]]; then
  echo "ERROR: Won't publish snapshot build $kutils_version!" 1>&2
  exit 1
fi

git config  user.email "holgerbrandl@users.noreply.github.com"
git status
git commit -am "${kravis_version} release"


# make sure that are no pending chanes
git diff --exit-code  || echo "There are uncommitted changes"

git tag "${kutils_version}"

git push origin
git push origin --tags
```

4. Bump to snapshot version for new release cycle