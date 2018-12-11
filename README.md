
# Anypoint Template: Salesforce to SAP Opportunity Broadcast	

<!-- Header (start) -->
Broadcasts changes or creates of opportunities from Salesforce to SAP in real time. The detection criteria and fields to move are configurable. Additional systems can be added to be notified of the changes. 

Real time synchronization is achieved either via rapid polling of Salesforce or outbound notifications to reduce the number of API calls. This template uses Mule batching and watermarking to capture only recent changes and to efficiently process large amounts of records.

![aaf185ad-96ea-48a3-a21c-034bc5bead2a-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/aaf185ad-96ea-48a3-a21c-034bc5bead2a-image.png)

![1da986db-0b12-49bf-85bb-ae3b3215d239-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/1da986db-0b12-49bf-85bb-ae3b3215d239-image.png)

![69d038cc-93f8-4943-a1e0-5d3b2d1c651c-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/69d038cc-93f8-4943-a1e0-5d3b2d1c651c-image.png)

![fe23a3e1-c82c-4063-b4b1-3d1535ca21c7-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/fe23a3e1-c82c-4063-b4b1-3d1535ca21c7-image.png)

![e8dadb36-0770-46d4-ac6c-f9edf93703d3-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/e8dadb36-0770-46d4-ac6c-f9edf93703d3-image.png)

<!-- Header (end) -->

# License Agreement
This template is subject to the conditions of the <a href="https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf">MuleSoft License Agreement</a>. Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio. 
# Use Case
<!-- Use Case (start) -->
This template performs an online sync of won opportunities from Salesforce to sales orders in SAP.
When there is a new won opportunity or a change in an existing one with an assigned account in a Salesforce instance, the template fetches the opportunity and sends it to SAP to upsert in a sales order. You can use this template as an example or as a starting point to adapt your integration to your requirements.
			
This template leverages the Mule batch module. The batch job is divided into *Process* and *On Complete* stages. The integration is triggered by a scheduler to Salesforce opportunities. New or modified opportunities, which fulfill the *IsWon* and *HasAccount* criteria are passed to the batch as input. In the batch, the sales order is fetched from SAP by its purchase order number equal to opportunity ID. If it exists, more sales order details are fetched from SAP.

In next batch step if the property account.sync.policy is set to 'syncAccount', the customer is looked up by the opportunity account name. If the customer is found, its sales areas are fetched from SAP and the first one is selected to be used for sales order creation. If it is not found, then a dummy customer is used with a preconfigured sales area and customer number. If the property account.sync.policy is set to different value, the preconfigured sales area and customer number are used. 

The template doesn't support changing the customer of an existing sales order.

Another step creates or updates sales order in SAP. Finally during the On Complete stage the template logs output statistics data into the console.
<!-- Use Case (end) -->

# Considerations
<!-- Default Considerations (start) -->

<!-- Default Considerations (end) -->

<!-- Considerations (start) -->
To make this template run, there are certain preconditions that must be considered. All of them deal with the preparations in both, that must be made for the template to run smoothly. Failing to do so can lead to unexpected behavior of the template.

Before continuing with the use of this template, see [Install the SAP Connector in Studio](https://docs.mulesoft.com/connectors/sap/sap-connector#install-the-sap-connector-in-studio).

## Disclaimer

This Anypoint template uses a few private Maven dependencies from Mulesoft to work. If you intend to run this template with Maven support, you need to add three extra dependencies for SAP to the pom.xml file.
<!-- Considerations (end) -->


## SAP Considerations

Here's what you need to know to get this template to work with SAP.


### As a Data Destination

There are no considerations with using SAP as a data destination.
## Salesforce Considerations

Here's what you need to know about Salesforce to get this template to work:

- Where can I check that the field configuration for my Salesforce instance is the right one? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US">Salesforce: Checking Field Accessibility for a Particular Field</a>.
- Can I modify the Field Access Settings? How? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US">Salesforce: Modifying Field Access Settings</a>.

### As a Data Source

If the user who configured the template for the source system does not have at least *read only* permissions for the fields that are fetched, then an *InvalidFieldFault* API fault displays.

```
java.lang.RuntimeException: [InvalidFieldFault [ApiQueryFault 
[ApiFault  exceptionCode='INVALID_FIELD'
exceptionMessage='Account.Phone, Account.Rating, Account.RecordTypeId, 
Account.ShippingCity
^
ERROR at Row:1:Column:486
No such column 'RecordTypeId' on entity 'Account'. If you are 
attempting to use a custom field, be sure to append the '__c' 
after the custom field name. Reference your WSDL or the describe 
call for the appropriate names.'
]
row='1'
column='486'
]
]
```

# Run it!
Simple steps to get this template running.
<!-- Run it (start) -->

<!-- Run it (end) -->

## Running On Premises
In this section we help you run this template on your computer.
<!-- Running on premise (start) -->

<!-- Running on premise (end) -->

### Where to Download Anypoint Studio and the Mule Runtime
If you are new to Mule, download this software:

+ [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
+ [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

**Note:** Anypoint Studio requires JDK 8.
<!-- Where to download (start) -->

<!-- Where to download (end) -->

### Importing a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your Anypoint Platform credentials, search for the template, and click Open.
<!-- Importing into Studio (start) -->

<!-- Importing into Studio (end) -->

### Running on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

1. Locate the properties file `mule.dev.properties`, in src/main/resources.
2. Complete all the properties required per the examples in the "Properties to Configure" section.
3. Right click the template project folder.
4. Hover your mouse over `Run as`.
5. Click `Mule Application (configure)`.
6. Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
7. Click `Run`.
<!-- Running on Studio (start) -->
To make this template run in Studio see [Install the SAP Connector in Studio](https://docs.mulesoft.com/connectors/sap/sap-connector#install-the-sap-connector-in-studio).
<!-- Running on Studio (end) -->

### Running on Mule Standalone
Update the properties in one of the property files, for example in mule.prod.properties, and run your app with a corresponding environment variable. In this example, use `mule.env=prod`. 


## Running on CloudHub
When creating your application in CloudHub, go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the mule.env value.
<!-- Running on Cloudhub (start) -->

<!-- Running on Cloudhub (end) -->

### Deploying a Template in CloudHub
In Studio, right click your project name in Package Explorer and select Anypoint Platform > Deploy on CloudHub.
<!-- Deploying on Cloudhub (start) -->

<!-- Deploying on Cloudhub (end) -->

## Properties to Configure
To use this template, configure properties such as credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.
### Application Configuration
<!-- Application Configuration (start) -->
**Common Configuration**

+ scheduler.frequency `10000`
+ scheduler.start.delay `1000`
+ watermark.default.expression `2014-08-14T10:15:00.000Z`

+ account.sync.policy `syncAccount`	

**Note:** The property **account.sync.policy** can take any of the two following values:

+ **empty_value**: If the property has no value assigned to it, then the application does nothing in respect to the account and just assigns the account and sales areas from the properties file.
+ **syncAccount**: It tries to get and fetch sales areas data from SAP customer if it exists.
		
**Salesforce Connector Configuration**

+ sfdc.username `bob.dylan@sfdc`
+ sfdc.password `DylanPassword123`
+ sfdc.securityToken `avsfwCUl7apQs56Xq2AKi3X`

**SAP Connector Configuration**

+ sap.jco.ashost `your.sap.address.com`
+ sap.jco.user `SAP_USER`
+ sap.jco.passwd `SAP_PASS`
+ sap.jco.sysnr `14`
+ sap.jco.client `800`
+ sap.jco.lang `EN`

**SAP Account(customer) Configuration**

+ account.sapCustomerNumber `0000400492`
+ account.sapSalesOrganization `3020`
+ account.sapDistributionChannel `30`
+ account.sapDivision `00`
<!-- Application Configuration (end) -->

# API Calls
<!-- API Calls (start) -->
Salesforce imposes limits on the number of API calls that can be made. However, in this template, only one call per poll cycle is done to retrieve all the information required.
<!-- API Calls (end) -->

# Customize It!
This brief guide provides a high level understanding of how this template is built and how you can change it according to your needs. As Mule applications are based on XML files, this page describes the XML files used with this template. More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

* config.xml
* businessLogic.xml
* endpoints.xml
* errorHandling.xml<!-- Customize it (start) -->

<!-- Customize it (end) -->

## config.xml
<!-- Default Config XML (start) -->
This file provides the configuration for connectors and configuration properties. Only change this file to make core changes to the connector processing logic. Otherwise, all parameters that can be modified should instead be in a properties file, which is the recommended place to make changes.
<!-- Default Config XML (end) -->

<!-- Config XML (start) -->

<!-- Config XML (end) -->

## businessLogic.xml
<!-- Default Business Logic XML (start) -->
The business logic XML file creates or updates objects in the destination system for a use case. You can customize and extend the logic of this template in this XML file to meet your needs.
<!-- Default Business Logic XML (end) -->

<!-- Business Logic XML (start) -->

<!-- Business Logic XML (end) -->

## endpoints.xml
<!-- Default Endpoints XML (start) -->
This file contains the endpoints for triggering the template and for retrieving the objects that meet the defined criteria in a query. You can execute a batch job process with the query results.
<!-- Default Endpoints XML (end) -->

<!-- Endpoints XML (start) -->

<!-- Endpoints XML (end) -->

## errorHandling.xml
<!-- Default Error Handling XML (start) -->
This file handles how your integration reacts depending on the different exceptions. This file provides error handling that is referenced by the main flow in the business logic.
<!-- Default Error Handling XML (end) -->

<!-- Error Handling XML (start) -->

<!-- Error Handling XML (end) -->

<!-- Extras (start) -->

<!-- Extras (end) -->
