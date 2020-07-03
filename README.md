# CIP4 Alces
Alces is a CIP4 Tool for integration and testing of JDF Devices. On the one hand, if you are responsible for the integration in a printing house Alces enables you to test a JDF Interface of a JDF Device in order to find out how its works and if the device is configured on the right way.

Further, Alces is also a very useful tool for JDF Interface developers of JDF Devices. In this case the application plays the role of a Manager and helps to test the JDF compliance of your device. Alces is available in two flavors: one for interactive and manual testing and one for automated testing.

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
