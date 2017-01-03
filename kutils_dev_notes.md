## Release Checklist

1. update version in build.gradle and README
2. push and create github release tag
3. 
```
grade install
```
4. create new version on jcenter

* Upload artifacts from ~/.m2/repository/de/mpicbg/scicomp/kutils to:
> https://bintray.com/holgerbrandl/mpicbg-scicomp/kutils

5. Check for release status on
https://jcenter.bintray.com/de/mpicbg/scicomp/

6. Bump versions for new release cycle