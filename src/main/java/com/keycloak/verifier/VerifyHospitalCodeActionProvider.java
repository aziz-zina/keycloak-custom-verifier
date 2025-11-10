package com.keycloak.verifier;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class VerifyHospitalCodeActionProvider implements RequiredActionProvider {
    private static final String FTL_PATH = "verify-hospital-code.ftl";
    private static final String FORM_FIELD_NAME = "hospital_code";
    private static final String USER_ATTRIBUTE_NAME = "hospital_code";
    private static final String ERROR_MESSAGE_KEY = "invalidHospitalCodeMessage";
    private static final Logger log = LoggerFactory.getLogger(VerifyHospitalCodeActionProvider.class);

    /**
     * This method is called to display the verification form to the user.
     */
    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        LoginFormsProvider form = context.form();
        Response challenge = form.createForm(FTL_PATH);
        context.challenge(challenge);
    }

    /**
     * This method is called when the user submits the form.
     */
    @Override
    public void processAction(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String submittedCode = formData.getFirst(FORM_FIELD_NAME);
        UserModel user = context.getUser();
        String correctCode = user.getFirstAttribute(USER_ATTRIBUTE_NAME);
        log.info("Verifying hospital code {} for user {}", submittedCode, user.getEmail());
        if (correctCode == null || submittedCode == null ||!correctCode.equals(submittedCode)) {
            LoginFormsProvider form = context.form()
                    .setError(ERROR_MESSAGE_KEY);

            Response challenge = form.createForm(FTL_PATH);
            context.challenge(challenge);
            return;
        }

        try {
            String userIdString = user.getId();
            UUID userUuid = UUID.fromString(userIdString);
            int statusCode = RestClient.assignDoctorToHospital(userUuid);
            if (statusCode >= 200 && statusCode < 300) {
                log.info("Successfully assigned doctor to hospital for user {}", user.getEmail());
                user.removeRequiredAction(VerifyHospitalCodeActionFactory.PROVIDER_ID);
                user.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD.name());
                context.success();

            } else {
                log.error("Failed to assign doctor. API returned status {}. User: {}",
                        statusCode, user.getEmail());
                LoginFormsProvider form = context.form()
                        .setError("Failed to assign hospital. Please try again or contact support.");
                Response challenge = form.createForm(FTL_PATH);
                context.challenge(challenge);
            }

        } catch (IOException | IllegalArgumentException e) {
            log.error("Failed to call assignDoctorToHospital for user {}: {}",
                    user.getEmail(), e.getMessage(), e);
            LoginFormsProvider form = context.form()
                    .setError("An unexpected error occurred. Please try again.");
            Response challenge = form.createForm(FTL_PATH);
            context.challenge(challenge);
        }
    }

    /**
     * This method is for actions that should be automatically applied at login.
     * Our flow is initiated via API, so this should be empty.
     */
    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // No-op for this use case
    }

    @Override
    public void close() {
        // No-op
    }
}
