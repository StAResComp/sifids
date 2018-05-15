# SIFIDS [![Build Status](https://travis-ci.org/StAResComp/sifids.svg?branch=master)](https://travis-ci.org/StAResComp/sifids)

An Android app being developed as part of the Scottish Inshore Fisheries Integrated Data System (SIFIDS) Project.

Allows fishermen to track their location and record their fishing activity in order to populate a [FISH1 Form](http://www.gov.scot/Topics/marine/Compliance/letters/FISH12016) which can be completed in the app. Completion of the form is eased by the setting of default values for vessel, owner, ports, gear, species etc.

## Building from source

This app requires a [Google Maps API key](https://developers.google.com/maps/documentation/android-sdk/signup), and expects to find one in the system resources. Add a file `keys.xml` in `catch/src/main/res/xml` with the following content:

```XML
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_maps_api_key">your-api-key-goes-here</string>
</resources>
```
