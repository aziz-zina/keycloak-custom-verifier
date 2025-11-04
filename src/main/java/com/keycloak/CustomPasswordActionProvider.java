package com.keycloak;

import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.sessions.AuthenticationSessionModel;

public class CustomPasswordActionProvider implements RequiredActionProvider {
    @Override
    public void evaluateTriggers(RequiredActionContext requiredActionContext) {

    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        if (authSession.getAuthNote("MY_ACTION_CODE")!= null) {
            context.challenge(context.form().createForm("custom-password-verifier.ftl"));
            return;
        }
    }

    @Override
    public void processAction(RequiredActionContext requiredActionContext) {

    }

    @Override
    public void close() {

    }
}
