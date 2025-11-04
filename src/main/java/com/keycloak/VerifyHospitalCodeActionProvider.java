package com.keycloak;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;

public class VerifyHospitalCodeActionProvider implements RequiredActionProvider {
    private static final String FTL_PATH = "verify-hospital-code.ftl";
    private static final String FORM_FIELD_NAME = "hospital_code";
    private static final String USER_ATTRIBUTE_NAME = "hospital_code";
    private static final String ERROR_MESSAGE_KEY = "invalidHospitalCodeMessage";

    /**
     * This method is called to display the verification form to the user.
     */
    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        // This creates a form builder and sets the action URL to point back to this provider
        LoginFormsProvider form = context.form();

        // This tells Keycloak to render the FTL template.
        // This FTL name is the "contract" Keycloakify will use.
        Response challenge = form.createForm(FTL_PATH);

        // This sends the challenge (the HTML page) to the user's browser.
        context.challenge(challenge);
    }

    /**
     * This method is called when the user submits the form.
     */
    @Override
    public void processAction(RequiredActionContext context) {
        // 1. Get Form Data
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String submittedCode = formData.getFirst(FORM_FIELD_NAME);

        // 2. Get User Attribute
        UserModel user = context.getUser();
        String correctCode = user.getFirstAttribute(USER_ATTRIBUTE_NAME);

        // 3. Validation Logic
        if (correctCode == null || submittedCode == null ||!correctCode.equals(submittedCode)) {
            // FAILURE: Re-issue the challenge with an error message.
            LoginFormsProvider form = context.form()
                    .setError(ERROR_MESSAGE_KEY); // This key is resolved by the theme's messages_en.properties

            Response challenge = form.createForm(FTL_PATH);
            context.challenge(challenge);
            return;
        }

        // 4. SUCCESS:
        user.removeRequiredAction(VerifyHospitalCodeActionFactory.PROVIDER_ID);

        // Second, add the *next* action in the chain: UPDATE_PASSWORD
        user.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD.name());

        // Mark this custom action as complete [12, 18]
        // Keycloak will automatically see the new UPDATE_PASSWORD action
        // and redirect the user to that page.
        context.success();
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
