# Creating a New Release

## Required Software

1. TexLive - To generate the user manual.
2. GnuPG - For publishing to Maven.
3. Apache Ant - For running the build tasks.
4. Java JDK - For compiling the source code.  The earliest available version
   should be used for class compatibility.

## Release Steps

1. Update `META-INF/build.properties` with new version number.
2. Update `news.md` with version number, release date, and release notes.
3. Update `website/xslt/archive.xml` with links to the last version.
4. If necessary, update the copyright year.
   1. Source code is updated using the `update-header` Ant task in `auxiliary/checkstyle/build.xml`.
   2. Search for the previous year and update any other locations
5. Ensure all test runs pass.
   1. `ant -f text.xml`
   2. `ant -f test.xml build-maven-tests & cd build & mvn test`
6. Run all of the `package-*` Ant tasks in `build.xml`.
7. Create a new Github Release using a tag in the format `vX.XX`.
   1. Copy the release notes from `news.md`
   2. Publish the following files in the `dist` folder:
      - `MOEAFramework-X.XX.tar.gz`
      - `MOEAFramework-X.XX-Demo.jar`
      - `MOEAFramework-X.XX-Source.tar.gz`
8. Publish the Maven package:
   1. Run the `package-maven` Ant task in `build.xml`.
   2. Visit `oss.sonatype.org` and login
   3. Create a new Staging Upload, select Artifact Bundle, and upload `*-bundle.jar` from `maven/`
   4. After verifying in staging, release the package.
   5. Update README.md with the new Maven link and example
9. Publish the new website:
   1. Run the `package-website` Ant task in `build.xml`.
   2. Checkout the latest `MOEAFramework\Website` repo and switch to the `gh-pages` branch.
   3. Copy the entire contents of the `build` folder into the `Website` repo.
   4. Commit and push.
   5. Verify the download links work on the website.
   
