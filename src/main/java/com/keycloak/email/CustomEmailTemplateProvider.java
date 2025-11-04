//package com.keycloak.email;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.keycloak.VerifyHospitalCodeActionFactory;
//import org.keycloak.email.EmailException;
//import org.keycloak.email.freemarker.FreeMarkerEmailTemplateProvider;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.models.UserModel;
//
//public class CustomEmailTemplateProvider extends FreeMarkerEmailTemplateProvider {
//
//    public CustomEmailTemplateProvider(KeycloakSession session) {
//        super(session);
//    }
//
//    @Override
//    public void sendExecuteActions(String link, long expirationInMinutes) throws EmailException {
//        // Check if the user for whom this email is being sent
//        // has our custom required action pending.
//        boolean needsHospitalCodeVerification = user.getRequiredActionsStream()
//                .anyMatch(action -> action.equals(VerifyHospitalCodeActionFactory.PROVIDER_ID));
//
//        if (needsHospitalCodeVerification) {
//            // 1. Build the attribute map for the template
//            Map<String, Object> attributes = new HashMap<>(this.attributes);
//
//            // 2. Add our custom attributes [20, 21]
//            attributes.put("hospitalCode", user.getFirstAttribute("hospital_code"));
//
//            // 3. Manually add the standard link attributes [22]
//            addLinkInfoIntoAttributes(link, expirationInMinutes, attributes);
//
//            // 4. Call send() with our custom subject key and template file name
//            // These will be defined in our Keycloakify theme (Part 4)
//            send("verifyHospitalCodeSubject", "verify-hospital-code-email.ftl", attributes);
//        } else {
//            // 5. Fallback to default behavior for all other actions (e.g., standard password resets)
//            super.sendExecuteActions(link, expirationInMinutes);
//        }
//    }
//}