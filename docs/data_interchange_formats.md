# Data Interchange Formats

The SIFIDS mobile app sends four kinds of data to the [SIFIDS web
application](https://github.com/StAResComp/sifids_web) at the url defined in
`catch/src/main/res/values/strings.xml` as `post_request_url`:

1. Location data
2. Observation data
3. Copies of FISH-1 forms
4. Consent data

These four are each handled separately and have their own formats.

Note that the web application singles the success or failure of a data
submission via HTTP codes - no other data is sent in that direction.

## Location Data

The POST request containing location data includes two parameters. The first is
labelled `vessel_name` and contains a text string of the vessel's identifier or
PLN. The second, labelled `tracks` contains the location data in CSV format
without a header row.

### CSV Columns

#### Timestamp

The date and time that the location was recorded, formatted to millisecond
precision as per ISO 8601 ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").

#### Is Fishing?

A boolean value, encoded as a 1 or a 0, indicating whether fishing activity was
taking place at that point.

#### Latitude

The recorded latitude in decimal format.

#### Longitude

The recorded longitude in decimal format.

#### Accuracy

The recorded horizontal accuracy of the location. As per the [Android Developer
documentation](https://developer.android.com/reference/android/location/Location#getAccuracy()),
this value is the radius in metres of a circle, centred at the recorded
latitude and longitude. There is estimated to be a 68% probability that the
true location lies within this circle.

## Observation data

Observation data is POSTed in a JSON format which looks like:

```json
{
  "pln": "SOME_PLN",
  "animal": "Dolphin",
  "species":  "Bottlenose dolphin",
  "timestamp": "2020-02-07T15:07Z",
  "latitude": "56.341663",
  "longitude": "-2.795040",
  "count": "3",
  "notes": "Some notes about the observation"
}
```

## FISH-1 Forms

The POST request containing a FISH-1 form includes two parameters. The first is
labelled `vessel_name` and contains a text string of the vessel's identifier or
PLN. The second, labelled `fish_1_form` contains the form information in a CSV
format designed to mimic the FISH-1 spreadsheet. This is the same format that
the app generates for sending by email.

The first 10 rows contain the header information for the form, as follows:

- \# Fishery Office: _Fishery Office name_
- \# Email: _Fishery Office email address_
- \# Port of Departure: _Port of departure_
- \# Port of Landing: _Port of landing_
- \# PLN: _PLN_
- \# Vessel Name: _vessel name_
- \# Owner/Master: _owner/master name_
- \# Address: _owner/master address_
- \# Total Pots Fishing: _total pots fishing_

The next line is blank, followed by the header row for the tabular data:

> Fishing Activity Date,Lat/Long,Stat Rect / ICES Area,Gear,Mesh Size,Species,State,Presentation,Weight,DIS,BMS,Number of Pots Hauled,Landing or Discard Date,"Transporter Reg, Not Transported or Landed to Keeps"

The remaining rows are the tabular data, as per the headings above.

## Consent Data

Consent data is posted in a JSON format which looks like:

```json
{
  "consent_read_understand": "true",
  "consent_questions_opportunity": "true",
  "consent_questions_answered": "true",
  "consent_can_withdraw": "true",
  "consent_confidential": "true",
  "consent_data_archiving": "true",
  "consent_risks": "true",
  "consent_photography_capture": "true",
  "consent_photography_publication": "true",
  "consent_photography_future_studies": "true",
  "consent_name": "User Name",
  "consent_email": "user@email.address",
  "consent_phone": "0123456789",
  "consent_fish_1": "true",
  "pref_vessel_pln": "abc123",
  "pref_vessel_name": "Titanic",
  "pref_owner_master_name": "User Name"
}
```

All those values given as `"true"` above may also be `"false"`; if any are
`"false"` then a HTTP error code should be returned. The value of
`"pref_owner_master_name"` will probably be the same as that of
`"consent_name"` but this cannot be assumed to be the case.
