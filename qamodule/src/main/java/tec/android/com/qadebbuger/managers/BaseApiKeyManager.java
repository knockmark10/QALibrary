package tec.android.com.qadebbuger.managers;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import tec.android.com.qadebbuger.enums.EndpointsType;

public class BaseApiKeyManager<
        DATACONFIGURATION,
        DATACONFIGURATIONFOOD,
        FIREBASEAPP,
        FIREBASEOPTIONS,
        AWSMOBILECLIENT,
        AWSCONFIGURATION
        > {

    private DATACONFIGURATION dataConfiguration;
    private DATACONFIGURATIONFOOD dataConfigurationFood;
    private FIREBASEAPP firebaseApp;
    private AWSMOBILECLIENT awsMobileClient;
    private static final String API_KEY = "API_KEY";
    private static final String ZZK_PROPERTY = "zzk";
    private static final String ZZE_PROPERTY = "zze";
    private static final String APP_ID_PROPERTY = "AppId";
    private static final String ENDPOINT_DELIMITER = "/v";
    private static final String DEFAULT_PROPERTY = "Default";
    private static final String JSON_OBJECT_PROPERTY = "mJSONObject";
    private static final String AWS_CONFIGURATION = "awsConfiguration";
    private static final String CLIENT_SECRET_KEY = "CLIENT_SECRET_KEY";
    private static final String PINPOINT_ANALYTICS = "PinpointAnalytics";

    /**
     * Required empty private constructor
     */
    private BaseApiKeyManager() {
    }

    /**
     * Gets the api key from the http header. It requires the
     * DataConfiguration class to read the property using
     * reflection. If the property is not found with default
     * value, it will take the alternativeFieldName to look
     * instead.
     *
     * @param alternativeFieldName as alternative field name to look for
     * @param maskApiKey           specifies if it should mask the api key
     * @return the api key requested
     */
    public String getHeaderApiKey(@NonNull String alternativeFieldName, @NonNull boolean maskApiKey) {
        ReflectionUtils<DATACONFIGURATION, String> reflectionUtils = new ReflectionUtils<>();
        if (reflectionUtils.getProperty(dataConfiguration, API_KEY) != null) {
            return maskApiKey ? maskApiKey(reflectionUtils.getProperty(dataConfiguration, API_KEY))
                    : reflectionUtils.getProperty(dataConfiguration, API_KEY);
        } else {
            return maskApiKey ? maskApiKey(reflectionUtils.getProperty(dataConfiguration, alternativeFieldName))
                    : reflectionUtils.getProperty(dataConfiguration, alternativeFieldName);
        }
    }

    /**
     * Gets the client secret from the http header. It requires
     * the DataConfiguration class to look into. If the searching
     * process failed, it will take the alternativeFieldName.
     *
     * @param alternativeFieldName as alternative field name to look for
     * @param maskApiKey           specifies if it should mask the api key
     * @return the api key requested
     */
    public String getHeaderClientSecret(@NonNull String alternativeFieldName, @NonNull boolean maskApiKey) {
        ReflectionUtils<DATACONFIGURATION, String> reflectionUtils = new ReflectionUtils<>();
        if (reflectionUtils.getProperty(dataConfiguration, CLIENT_SECRET_KEY) != null) {
            return maskApiKey ? maskApiKey(reflectionUtils.getProperty(dataConfiguration, CLIENT_SECRET_KEY))
                    : reflectionUtils.getProperty(dataConfiguration, CLIENT_SECRET_KEY);
        } else {
            return maskApiKey ? maskApiKey(reflectionUtils.getProperty(dataConfiguration, alternativeFieldName))
                    : reflectionUtils.getProperty(dataConfiguration, alternativeFieldName);
        }
    }

    /**
     * Gets the firebase api key used for the current project, based
     * on the instance of the class provided. It uses reflection to
     * read the property.
     *
     * @param maskApiKey specifies if it should mask the api key
     * @return the api key requested
     */
    public String getFirebaseApiKey(@NonNull boolean maskApiKey) {
        ReflectionUtils<FIREBASEAPP, FIREBASEOPTIONS> reflectionUtils = new ReflectionUtils<>();
        FIREBASEOPTIONS firebaseOptions = reflectionUtils.getProperty(firebaseApp, ZZK_PROPERTY);
        ReflectionUtils<FIREBASEOPTIONS, String> firebaseOptionsReflection = new ReflectionUtils<>();
        return maskApiKey ? maskApiKey(firebaseOptionsReflection.getProperty(firebaseOptions, ZZE_PROPERTY))
                : firebaseOptionsReflection.getProperty(firebaseOptions, ZZE_PROPERTY);
    }

    /**
     * Gets the key from AWSMobileClient object using reflection. Deconstruction
     * of the object provided is performed to extract the key desired.
     *
     * @param maskApiKey specifies if it should mask the api key
     * @return the api key requested
     */
    public String getAWSKey(@NonNull boolean maskApiKey) {
        String awsKey;
        ReflectionUtils<AWSMOBILECLIENT, AWSCONFIGURATION> genericAWSClient = new ReflectionUtils<>();
        ReflectionUtils<AWSCONFIGURATION, JSONObject> genericJsonObject = new ReflectionUtils<>();
        try {
            AWSCONFIGURATION awsConfiguration = genericAWSClient.getProperty(awsMobileClient, AWS_CONFIGURATION);
            JSONObject mJsonObject = genericJsonObject.getProperty(awsConfiguration, JSON_OBJECT_PROPERTY);
            JSONObject pinpointObject = getJsonObject(mJsonObject, PINPOINT_ANALYTICS);
            JSONObject defaultObject = getJsonObject(pinpointObject, DEFAULT_PROPERTY);
            awsKey = defaultObject.get(APP_ID_PROPERTY).toString();
        } catch (JSONException exception) {
            exception.printStackTrace();
            awsKey = exception.getMessage();
        }
        return maskApiKey ? maskApiKey(awsKey) : awsKey;
    }

    /**
     * Performs an extraction of the endpoints declared on the
     * DataConfiguration classes. It uses reflection for the
     * extraction.
     *
     * @param type could be Tickets or Food to specify the proper class
     * @return list of endpoints requested
     */
    public List<String> getEndpointsFromDataConfiguration(@NonNull EndpointsType type) {
        List<String> endpoints = new ArrayList<>();
        Class classData;
        Field[] fieldsConfiguration;
        try {
            if (type == EndpointsType.Tickets) {
                classData = Class.forName(dataConfiguration.getClass().getName());
            } else {
                classData = Class.forName(dataConfigurationFood.getClass().getName());
            }
            fieldsConfiguration = classData.getDeclaredFields();
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }

        for (Field field : fieldsConfiguration) {
            String endpoint = getEndpointsByType(type, field);
            if (endpoint != null && !TextUtils.isEmpty(endpoint)) {
                endpoints.add(endpoint);
            }
        }
        return endpoints;
    }

    private String getEndpointsByType(EndpointsType type, Field field) {
        try {
            String fieldName;
            switch (type) {
                case Tickets:
                    fieldName = (String) field.get(dataConfiguration);
                    if (fieldName != null && fieldName.contains(ENDPOINT_DELIMITER)) {
                        return fieldName;
                    }
                    break;
                case Food:
                    fieldName = (String) field.get(dataConfigurationFood);
                    if (fieldName != null && fieldName.contains(ENDPOINT_DELIMITER)) {
                        return fieldName;
                    }
                    break;
            }
        } catch (ClassCastException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Masks the api key provided, showing only the last
     * four digits of the key.
     *
     * @param apiKey the key to mask
     * @return the api key masked
     */
    public String maskApiKey(String apiKey) {
        StringBuilder maskedApiKey = new StringBuilder();
        for (int i = 0; i < apiKey.length(); i++) {
            if (i < apiKey.length() - 4) {
                maskedApiKey.append("X");
            } else {
                maskedApiKey.append(String.valueOf(apiKey.charAt(i)));
            }
        }
        return maskedApiKey.toString();
    }

    private JSONObject getJsonObject(JSONObject jsonObject, String propertyName) {
        JSONObject jsonObjectToReturn = null;
        try {
            jsonObjectToReturn = (JSONObject) jsonObject.get(propertyName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectToReturn;
    }

    /**
     * Custom builder to construct the object
     * desired
     *
     * @param <DATACONFIGURATION>     instance of DataConfiguration
     * @param <DATACONFIGURATIONFOOD> instance of DataConfiguration from 'alimentos' module
     * @param <FIREBASEAPP>           instance of FirebaseApp
     * @param <FIREBASEOPTIONS>       instance of FirebaseOptions
     * @param <AWSMOBILECLIENT>       instance of AwsMobileClient
     * @param <AWSCONFIGURATION>      instance of AwsConfiguration
     */
    public static class Builder<
            DATACONFIGURATION,
            DATACONFIGURATIONFOOD,
            FIREBASEAPP,
            FIREBASEOPTIONS,
            AWSMOBILECLIENT,
            AWSCONFIGURATION
            > {

        private DATACONFIGURATION dataConfiguration;
        private DATACONFIGURATIONFOOD dataConfigurationFood;
        private FIREBASEAPP firebaseApp;
        private AWSMOBILECLIENT awsMobileClient;

        /**
         * Sets the instance from the DataConfiguration class
         * to inspect. Be sure to provide the one from 'app'
         * module.
         *
         * @param dataConfiguration from 'app' module
         * @return an instance of the builder to chain calls
         */
        public Builder setDataConfiguration(DATACONFIGURATION dataConfiguration) {
            this.dataConfiguration = dataConfiguration;
            return this;
        }

        /**
         * Sets the instance from DataConfiguration class to
         * inspect. Be sure to provide the one from 'alimentos'
         * module.
         *
         * @param dataConfigurationFood from 'alimentos' module
         * @return an instance of the builder to chain calls
         */
        public Builder setDataConfigurationFood(DATACONFIGURATIONFOOD dataConfigurationFood) {
            this.dataConfigurationFood = dataConfigurationFood;
            return this;
        }

        /**
         * Sets an instance of the FirebaseApp from
         * firebase module to analyze in further steps.
         * @param firebaseApp the singleton instance
         * @return an instance of the builder to chain calls
         */
        public Builder setFirebaseApp(FIREBASEAPP firebaseApp) {
            this.firebaseApp = firebaseApp;
            return this;
        }

        /**
         * Sets an instance of the AwsMobileClient from
         * AWSMobileClient to analyze in further steps.
         * @param awsMobileClient the singleton instance
         * @return
         */
        public Builder setAwsMobileClient(AWSMOBILECLIENT awsMobileClient) {
            this.awsMobileClient = awsMobileClient;
            return this;
        }

        /**
         * Method to build the full object with the
         * properties properly defined. It will raise
         * an exception if some property wasn't defined
         * in previous calls.
         * @return
         */
        public BaseApiKeyManager create() {
            checkNotNull();
            BaseApiKeyManager<
                    DATACONFIGURATION,
                    DATACONFIGURATIONFOOD,
                    FIREBASEAPP,
                    FIREBASEOPTIONS,
                    AWSMOBILECLIENT,
                    AWSCONFIGURATION
                    > baseApiKeyManager = new BaseApiKeyManager<>();
            baseApiKeyManager.dataConfiguration = this.dataConfiguration;
            baseApiKeyManager.dataConfigurationFood = this.dataConfigurationFood;
            baseApiKeyManager.firebaseApp = this.firebaseApp;
            baseApiKeyManager.awsMobileClient = this.awsMobileClient;
            return baseApiKeyManager;
        }

        private void checkNotNull() {
            if (dataConfiguration == null) {
                throw new IllegalArgumentException("You must set a DataConfiguration instance to initialize the manager.");
            }

            if (dataConfigurationFood == null) {
                throw new IllegalArgumentException("You must set a DataConfiguration instance from alimentos module to initialize the manager.");
            }

            if (firebaseApp == null) {
                throw new IllegalArgumentException("You must set a FirebaseApp instance to this manager. Try FirebaseApp.getInstance() method.");
            }

            if (awsMobileClient == null) {
                throw new IllegalArgumentException("You must set a AWSMobileClient instance to initialize the manager. Try AWSMobileClient.getInstance() method.");
            }
        }
    }

}
