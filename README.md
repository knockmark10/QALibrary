# **Welcome to QA Debugger Library!**

This library was made thinking in the QA personnel, to give them the chance to inspect basic aspects of the application they're debugging. With this library they'll be able to discover the *version name*, *version code*, the *environment* of the app, the *ssl pinning status*, the *endpoints* the app is using, and the last but not least, to be able to enable/disable *ssl pinning* at will. 

# **Setup**
This library is available _via_ ![](https://jitpack.io/v/knockmark10/QALibrary.svg)

 1. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

 2. Add the dependency:
```java
dependencies {
	implementation 'com.github.knockmark10:QALibrary:$version'
}
```

## **What's inside it?**

You have multiple features within this library:

 [1. Authentication Dialog](https://github.com/knockmark10/KotlinCommons/wiki#1-shared-preferences)
 
 [2. Reflection Utils](https://github.com/knockmark10/KotlinCommons/wiki#2-runtime-permissions-manager)
 
 [3. Package Manager](https://github.com/knockmark10/KotlinCommons/wiki#3-geocoder-manager)
 
 [4. BaseApiKeyManager](https://github.com/knockmark10/KotlinCommons/wiki#4-tracking-manager-gps)

## **1. Authentication Dialog**

### **Description**

This dialog will help you to authenticate users in a very useful and quick way. You only need to call the dialog, put on valid credentials (managed by firebase console) and the dialog will handle the authentication process for you. The dialog will comunicate the result using callbacks.

**Public Methods**

|Return type|Method name|Parameters|Description|
|:---------:|:---------:|:--------:|:---------:|
|Builder| setPackageName| packagaName |Sets the package name of your current application, and return the same builder for chaining calls.
|Builder| setAuthenticationListener| authenticationListener |Sets the listener for handling the result of the authentication process.
|DialogAuthentication| create| - |Returns the dialog with the properties set and ready to autheticate.

### **Usage**

```java
DialogAuthentication dialog = new DialogAuthentication
								.Builder()
								.setPackageName("air.Cinepolis")
								.setAuthenticationListener(this)
								.create();
dialog.show(getSupportFragmentManager(), TAG);

@Override
public void onAuthenticationFailed(){
	//Handle errors
}

@Override
public void onSuccesfullAuthentication(){
	//Handle successfull authentication
}
```

## **2. Reflection utils**

### **Description**

This feature allows you to perform reflection on a very reliable and error-free way. It only requires to specify the base class you're gonna start from, and the kind of class the result will be retrieved.

|PUBLIC CONSTRUCTOR|DESCRIPTION|
|:--:|:--:|
|**ReflectionUtils <BASE, RESULT>**|Constructs the object with the two generic class provided.|

**Public Methods**

|Return type|Method name|Parameters|Description|
|:---------:|:---------:|:--------:|:---------:|
|RESULT (generic)|getProperty|type, fieldName|Uses the BASE class that will be used to extract the data, and the name of the field you want to extract. It returns the result with the actual value, or null if it's not present.|

### **Usage**

```java
/**
* We specify the kind of class we want to start inspecting, in
* this case, 'DataConfiguration', and the result of the property,
* which is 'String'. Then we call the method getProperty with the
* object 'dataConfiguration' and the value of the property with
* the name 'API_KEY'
*/
ReflectionUtils<DataConfiguration, String> reflection = new ReflectionUtils();
String apiKey = reflectionUtils.getProperty(dataConfiguration, "API_KEY");
```

## **3. PackageManager**

### **Description**

This class will help you to recover data from the package in a very easy way. You just need to call the only method in it, and specify the type of data you want to get. 

**NOTE: This class has only static methods**

**Public Static Methods**

|Return type|Method name|Parameters|Description|
|:---------:|:---------:|:--------:|:---------:|
|String|getPackageInfo|context, info|Gets one of three posible values from package: VERSIONCODE, VERSIONNAME or PACKAGENAME|


### **Usage**

```kotlin
String versionCode = PackageManager.getPackageInfo(getContext, AppInfo.VersionCode);
```

## **4. BaseApiKeyManager**

### **Description**

This is the most important class of the library, because it allows you to read keys from your SDK, and the ones you use to make a api rest call. You only need to set the objects that contains the keys you want to read, and the keys will be provided to you as Strings.

**Note: There's not public constructor available. You can construct the object with the builder design pattern shown below.**

**Public Methods**

|Return type|Method name|Parameters|Description|
|:---------:|:---------:|:--------:|:---------:|
|Builder|setDataConfiguration|dataConfiguration|Sets an instance of the DataConfiguration class from ***app module***. Returns an instance of the Builder to chain calls.|
|Builder|setDataConfigurationFood|dataConfigurationFood|Sets an instance of the DataConfiguration class from ***alimentos module***. Returns an instance of the Builder to chain calls.|
|Builder|setFirebaseApp|firebaseApp|Sets an instance of the FirebaseApp from the ***core module.*** Returns an instance of the Builder to chain calls.|
|Builder|setAwsMobileClient|awsMobileClient|Sets an instance of the AWSMobileClient class. Returns an instance of the Builder to chain calls.|
|BaseApiKeyManager|create|-|Creates the *BaseApiKeyManager* with an instance of the classes provided above. It will raise an exception if you don't provide all the clases required.|
|String|getHeaderApiKey|alternativeFieldName, maskApiKey|Takes an instance of the *DataConfiguration* class and extracts the api key used for api rest calls. If the key is not found with default value, you can provide the name, and it will take a look with it.|
|String|getHeaderClientSecret|alternativeFieldName, maskApiKey|Takes an instance of the *DataConfiguration* class and extracts the client secret key used for api rest calls. If the key is not found with default value, you can provide the name, and it will take a look with it.|
|String|getFirebaseApiKey|maskApiKey|Takes an instance of the *FirebaseApp* class and extracts the internal api key.|
|String|getHeaderClientSecret|alternativeFieldName, maskApiKey|Takes an instance of the *DataConfiguration* class and extracts the client secret key used for api rest calls. If the key is not found with default value, you can provide the name, and it will take a look with it.|
|String|getAWSKey|maskApiKey|Takes an instance of the *ClientAWSClient* class and extracts the internal api key.|
|listOfString|getEndpointsFromDataConfiguration|type|It takes the *DataConfiguration* class to extract the endpoints declared. It requires a type to decide in which module it should look for.|
|String|maskApiKey|apiKey|It masks an api provided. This method is public because you might need it in a local manager to take advantages of this method.|

### **Usage**

1. Call the proper Builder to construct your object.

```java
baseApiKeyManager = new BaseApiKeyManager.Builder<  
  DataConfiguration,  
  com.ia.alimentoscinepolis.connection.data.utils.DataConfiguration,  
  FirebaseApp,  
  FirebaseOptions,  
  AWSMobileClient,  
  AWSConfiguration>()  
 .setDataConfiguration(dataConfiguration)  
 .setDataConfigurationFood(foodDataConfiguration)  
 .setFirebaseApp(firebaseApp) 
 .setAwsMobileClient(awsMobileClient)  
 .create();
```

2. Your object now contains a reference of the classes it requires, and now can perform the extraction.

```java
baseApiKeyManager.getEndpointsFromDataConfiguration(EndpointType.Tickets);
```
