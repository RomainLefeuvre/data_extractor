Data-extractor Project
==
The objective of this project to identify github repositories of android applications that are also available on the play store.
We use the github graphQL API in order to search project having _https://play.google.com/store/apps/details?id=_ in its
description or readme.

For more information on github graphQL api 
https://docs.github.com/en/graphql

You need to generate a token with read access to the public repos and then put it on the application.properties like
`quarkus.smallrye-graphql-client.github-graphql.header.Authorization=bearer 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` 
(this is not an active token .. )

Results are stored in `/results` folder
* res.json is a list of `Result` object (repoUrl,sshRepoUrl, googlePlayUrl)
* desc_raw.jon contains the raw data of repositories having a google play link in description
* readme_raw.json contains contains the raw data of repository having a google play link in readme

Repositories that have not been processed are saved in `missed` folder :
* repositories having more than one link to a google play app in desc or readme 
* repositories that have no link google play app in desc or readme

ToDo:
* manage missed cases
* use logger instead of print
* optimize the time interval 
* manage `2022-05-02 13:59:40,975 SEVERE [org.ecl.yas.int.Marshaller] (Quarkus Main Thread) null` 
error that seems to be not blocking
* Change the graphql request to use the internal dsl instead of plain string

Notes :
* The graphql api of github can return only 1k response by "main request" using pagination and "sub request" (100)
* We need to decompose our main request because it returns 32k elements 
https://github.community/t/graphql-api-search-results-end-after-10-pages/14128/2

Issue:
For an unknow reason more than 1k repos have been created in 2017-10-27T21 .


# Quarkus commands : 
## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory. Be aware that it’s not an _über-jar_ as
the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/data-extractor-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.


