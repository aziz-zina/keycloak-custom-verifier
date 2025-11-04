//package com.keycloak.email;
//
//import org.keycloak.Config;
//import org.keycloak.email.EmailTemplateProvider;
//import org.keycloak.email.EmailTemplateProviderFactory;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.models.KeycloakSessionFactory;
//
//public class CustomEmailTemplateProviderFactory implements EmailTemplateProviderFactory {
//
//    /**
//     * This is the unique ID for your provider.
//     * You will use this string in your keycloak.conf to activate it.
//     * e.g., spi-email-template-provider=custom-email-provider
//     */
//    public static final String PROVIDER_ID = "custom-email-provider";
//
//    @Override
//    public EmailTemplateProvider create(KeycloakSession session) {
//        // Returns a new instance of your provider class
//        return new CustomEmailTemplateProvider(session);
//    }
//
//    @Override
//    public String getId() {
//        return PROVIDER_ID;
//    }
//
//    // --- Boilerplate methods ---
//
//    @Override
//    public void init(Config.Scope config) {
//        // No-op
//    }
//
//    @Override
//    public void postInit(KeycloakSessionFactory factory) {
//        // No-op
//    }
//
//    @Override
//    public void close() {
//        // No-op
//    }
//}