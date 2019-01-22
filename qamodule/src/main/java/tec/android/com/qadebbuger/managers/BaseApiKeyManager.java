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

    private BaseApiKeyManager() {
    }

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

    public String getFirebaseApiKey(@NonNull boolean maskApiKey) {
        ReflectionUtils<FIREBASEAPP, FIREBASEOPTIONS> reflectionUtils = new ReflectionUtils<>();
        FIREBASEOPTIONS firebaseOptions = reflectionUtils.getProperty(firebaseApp, ZZK_PROPERTY);
        ReflectionUtils<FIREBASEOPTIONS, String> firebaseOptionsReflection = new ReflectionUtils<>();
        return maskApiKey ? maskApiKey(firebaseOptionsReflection.getProperty(firebaseOptions, ZZE_PROPERTY))
                : firebaseOptionsReflection.getProperty(firebaseOptions, ZZE_PROPERTY);
    }

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

        public Builder setDataConfiguration(DATACONFIGURATION dataConfiguration) {
            this.dataConfiguration = dataConfiguration;
            return this;
        }

        public Builder setDataConfigurationFood(DATACONFIGURATIONFOOD dataConfigurationFood) {
            this.dataConfigurationFood = dataConfigurationFood;
            return this;
        }

        public Builder setFirebaseApp(FIREBASEAPP firebaseApp) {
            this.firebaseApp = firebaseApp;
            return this;
        }

        public Builder setAwsMobileClient(AWSMOBILECLIENT awsMobileClient) {
            this.awsMobileClient = awsMobileClient;
            return this;
        }

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
