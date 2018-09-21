## Find Orphan Images

Checks a couple of markdown documents for the absence/presence of images files in a directory, and removes orphane images, that is images which are not referenced in the markdown documents.


# Installation

```bash
export PATH=/Users/brandl/projects/kotlin/kutils/tools/orphan_images:$PATH
orphimg.kt --help

```

or use a url-cached copy

```bash
alias orphimg=`kscript https://github.com/holgerbrandl/kutils/blob/master/tools/orphan_images/orphimg.kt'


orphimg --hell
```
