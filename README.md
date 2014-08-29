# Mendeley Android SDK #

Version: Alpha-1

Released: 1 September 2014

<aside class="warning">
**Important note: this is an early pre-release version, and is subject to change.**
</aside>

## About the SDK ##

The SDK provides a convenient library for accessing the [Mendeley API](http://dev.mendeley.com/)
from Android applications.

## Minimum requirements ##

Android version: API level 14 (Android 4.0, ICS)

Java version: 1.7

## Getting started ##

A sample app is provided, which illustrates basic use of the library. The path to the
library is specified by the sample app's `project.properties` file.

<aside class="notice">
**To run it you must change the dummy client ID, client secret and redirect URI constants
defined at the top of `ExampleActivity.java` to the correct ones for your own app.**
These can be generated from the Mendeley developers website.
</aside>

## Using the library ##

You should start by calling `MendeleySdkFactory.getInstance()` to get a
`MendeleySdk` object. This contains methods for all the API calls, as well as
`isSignedIn()`, `signIn()` and `signOut()`. Be sure to call `signIn()` before
making API calls.

Many of the API calls take parameters, which are defined in the `com.mendeley.api.params` package.
They return a callback defined in one of the  of `com.mendeley.api.callbacks.*` subpackages.
The data types returned are defined in the `com.mendeley.api.model` package.
On failure, they either invoke the appropriate callback, or raise an exception from the
`com.mendeley.api.exceptions` package.

<aside class="notice">
Note: at present all the API calls presented by the SDK are asynchronous (non-blocking).
Blocking versions are likely to be provided as an option in future releases.
</aside>

## Resources ##

Reference documentation: [Javadoc](http://mendeley.github.io/mendeley-android-sdk/)

## Support ##

E-mail: api@mendeley.com
