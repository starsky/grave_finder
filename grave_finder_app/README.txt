1. NUTITEQ MAP
If you want to use nutiteq map remember to register on nutiteq site and add map api key.
To do it you NEED TO CREATE file in res/values/nutiteq_api_key.xml with content:
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <item name="nutiteq_key" type="string">TYPE_YOUR_KEY</item>
</resources>

2. CONTACT
If you have any question write to grave.finder.app(at)gmail.com
 
3. FEW WORDS ABOUT BUILD CONFIGURATION
Application can be buld in 3 configurations:
  a.) devel - during day to day development eg. from eclipse or using ant debug command
  b.) beta - for testing purposes just before release. To create package use build_beta.sh which will build application. And save results to releases/beta/<app_version>. This package it obfuscated by proguard. The difference between release version is that the package is marked as debbugable, and app label is different than release one.
  c.) release - package which is published in store. To create package use build_release.sh. The package is obfuscated and has different app label than previous versions.

To achieve such build configuration separation I had add some custom build targets in ant. And some resources files.

  a.) How does app labels are changed depends on build conf:
In build_config_files/value*.xml are res files which are copied to res/values/ folder before build. In this files build conf dependant strings are stored.

  b.) How do I create debbugable beta package
In custom_rulse.xml I override ant -package-resources task to provide my own property debug="${custom.package.debuggable}". Why do I have do it like this? As if I override std android property build.is.packaging.debug Android ant build will automatically switch do ant debug task which will cause no proguard. So I have to set debug property just before packaging.

  c.) manifestPackage="${custom.manifest.package}" in custom_rules
I also added manifestPackage property to aapt taks. This alows to change app package before packaging. Which can allow to have different version of app installed at the same time (devel, beta, release). Now it will not work as I use content providers which also have to be unique. But maybe in future I will resolve this problem. BTW Google is introducing soon gradle build system, ,maybe it will be more convenient.

  d.) copy-binaries task - copies apk and proguard mapping.txt to releases/<conf_type>/<version>

  e.) custom-beta-build custom-production-build tasks just set properties for tasks from b. c. d.


