# RingCentral Connector

Open source Identity Management connector for [RingCentral](https://www.ringcentral.com/)

Now leverages the [Connector Base Framework](https://github.com/ExclamationLabs/connector-base)

Developed and tested in [Midpoint](https://evolveum.com/midpoint/), but also could be utilized in any [ConnId](https://connid.tirasa.net/) framework. 

## Introductory Notes/Software Limitations

- This software is Copyright 2020 Exclamation Labs.  Licensed under the Apache License, Version 2.0.

- During development, could not base this connector off of public Java SDK; public SDK doesn't support SCIM,
which would need for CRUD of Users.  Had to integrate with the RESTful services manually.

- PagingOnlyGroups do not seem to be listable or maintainable. So this connector will only support CRUD for Users.

- In RingCentral, users can have multiple emails, phones and addresses.  But for IAM purposes we only
 read and update a single one (first one in list).  Therefore the connector, at least for now, only supports a single
 email, phone or address.

- RingCentral supports paging and the default page size seems to be 100.  But connector as of this
writing does not support paging.  Retrieving more than 100 users may not work.

- RingCentral development license has a low limit on number of new users you can create.  As of this writing it
appears you can only have 3 additional users (4 users total if you count the user this account is setup on)

- RingCentral development has usage limits to be aware of; if you try making too many calls in succession in a limited
timeframe, you can run into errors.  Shown here from the RingCentral control panel is some information on the usage limits:

```
API group 	Request limit per user (RingCentral extension)	Penalty interval
Heavy	10 requests / 60 secs	60 secs
Medium	40 requests / 60 secs	60 secs
Light	50 requests / 60 secs	60 secs
Auth	5 requests / 60 secs	60 secs
```

- With 0.3, support added for Call Queues.  You cannot create, modify or remove call queues using
the connector (has to be done in Ring Central itself).  But you can read call queues, and can 
assign/remove Ring Central users to/from a call queue.  ObjectClass is "CallQueue".

## Getting started

- [Create a developer account](https://developers.ringcentral.com/login.html)
- [Getting started](https://developers.ringcentral.com/api-reference/using-the-api) (see in particular Provisioning -> SCIM section)
- Once logged in, [Create a RingCentral App](https://developer.ringcentral.com/new-app?name=API+Reference&desc=An+app+created+to+fascilitate+API+calls+within+the+API+Reference.&public=false&type=ServerOther&carriers=7710,7310,3420&permissions=Contacts,EditExtensions,EditMessages,EditPresence,Faxes,Glip,InternalMessages,Meetings,ReadAccounts,ReadCallLog,ReadCallRecording,ReadContacts,ReadMessages,ReadPresence,RingOut,SMS,SubscriptionWebhook,VoipCalling&redirectUri=)
    - App Type - Web server (e.g. Tomcat, Node.js) (Most common)
    - Access - Only members of my organization/company
    - (fill out an App Name and Description)
    - App Permissions - (keep all of them)
    - OAuth Redirect URI - http://localhost
    - From the Dashboard for your App page, make note of the Sandbox Credentials - Account phone number.  Activate the Password.
    - You will then receive a message/email containing your App phone number (generated by RingCentral)
    
- Your [Developer Account page](https://developers.ringcentral.com/my-account.html#/applications) can
be used to see your App and related information. You can also use this page
to establish a password for app usage (this is separate from your actual developer account/pasword)
    
- In order to fully use SCIM and create users, you will need the EditAccounts permission
which is not listed.  This needs to be requested by emailing devsupport@ringcentral.com.

- Another requirement for using SCIM API - you need to add it to your Directory Integration settings.
Login your account https://service.ringcentral.com (sandbox account https://service.devtest.ringcentral.com) then select Tools -> Directory Integration -> Google Cloud Directory or SCIM then enable it.

- Your [Admin Portal panel](https://service.devtest.ringcentral.com/) can be used
to view/manage Users.  You will also need to turn on SCIM from this interface (via More -> Account Settings
-> Directory Integration) 
        
## Midpoint configuration

See XML files in src/test/resources folder for Midpoint examples.  resourceOverlay.xml is an example
resource configuration setup for Midpoint.

## Configuration properties
 
- service.serviceUrl - Set to service url of RingCentral to connect to.  For development, `https://platform.devtest.ringcentral.com/` 
 
- security.authenticator.oauth2Password.tokenUrl - Currently is https://platform.devtest.ringcentral.com/restapi/oauth/token ... would likely
be different for production usage.

- security.authenticator.oauth2Password.encodedSecret - This is an encoded combination of your Consumer Key and Consumer Key.
In order to generate it, you can use https://www.base64encode.org/ and encode [client id]:[client secret].

- security.authenticator.oauth2Password.oauth2Username - This is the username/RingCentral phone number (with no punctuation) for your
sandbox access.  Example: 19294153709

- security.authenticator.oauth2Password.oauth2Password - The password for your sandbox account access.  Not the
same as your password for your developer account as a whole.

- custom.preferredCallQueueIds - Comma-delimited list of Preferred Call Queue ids that need to have user assignment.

- results.pagination (optional) - Set to `true` for RingCentral since their API and this connector supports pagination.

- results.deepGet (optional) - Not applicable for RingCentral.

- results.deepImport (optional) - Not applicable for RingCentral.

- results.importBatchSize (optional) - Not applicable for RingCentral.

