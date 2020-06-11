# CIP4 Alces
JDF / JMF Integration Tool

## Issue Tracking
Don't write issues, provide Pull-Requests!

## Development Notes
### Release a new Version
Creation and publishing of a new version to GitHub Release.

```bash
$ git tag -a Alces-[VERSION] -m "[TITLE]"
$ git push origin Alces-[VERSION]
```

In case a build has been failed, a tag can be deleted using the following command:
```bash
$ git tag -d Alces-[VERSION]
$ git push origin :refs/tags/Alces-[VERSION]
```