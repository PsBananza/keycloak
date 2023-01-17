package com.example.demo.service;

import com.example.demo.custom.RegisterUser;
import com.example.demo.custom.User;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
@Service
public class KeycloakAdminClientExample {

    public String created(RegisterUser users) {

        String serverUrl = "http://localhost:8080/";
        String realm = "master";
        // idm-client needs to allow "Direct Access Grants: Resource Owner Password Credentials Grant"
        String clientId = "login-app";
        String clientSecret = "WZ9AiOgQXOyUTXF6WGKrsF5ZAekxjrGm";

        ClientRepresentation client = new ClientRepresentation();
        client.setId(clientId);
        client.setClientId(clientId);
        client.setName(clientId);
        client.setSecret(clientSecret);
        client.setEnabled(true);
        client.setDirectAccessGrantsEnabled(true);
        client.setBaseUrl(serverUrl);

        // User "idm-admin" needs at least "manage-users, view-clients, view-realm, view-users" roles for "realm-management"
        Keycloak keycloak = KeycloakBuilder.builder() //
                .serverUrl(serverUrl) //
                .realm(realm) //
                .grantType(OAuth2Constants.PASSWORD) //
                .clientId("admin-cli") //
                .clientSecret(clientSecret) //
                .username("admin") //
                .password("admin") //
                .build();


        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(users.getUsername());
        user.setFirstName(users.getFirstName());
        user.setLastName(users.getLastName());
        user.setEmail(users.getEmail());
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));

        // Get realm
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersRessource = realmResource.users();

        // Create user (requires manage-users role)
        Response response = usersRessource.create(user);
        System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
        System.out.println(response.getLocation());
        String userId = CreatedResponseUtil.getCreatedId(response);

        System.out.printf("User created with userId: %s%n", userId);

        // Define password credential
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue("test");
//
        UserResource userResource = usersRessource.get(userId);
        RoleRepresentation userRealmRole = realmResource.roles()
                .get("user").toRepresentation();
        userResource.roles().realmLevel()
                .add(Collections.singletonList(userRealmRole));

        // Set password credential
        userResource.resetPassword(passwordCred);
        return userId;
////        // Get realm role "tester" (requires view-realm role)
//        RoleRepresentation testerRealmRole = realmResource.roles()//
//                .get("default-roles-master").toRepresentation();
////
////        // Assign realm role tester to user
//        userResource.roles().realmLevel() //
//                .add(Arrays.asList(testerRealmRole));
////
////        // Get client
//        ClientRepresentation app1Client = realmResource.clients() //
//                .findByClientId("login-app").get(0);
//        app1Client.getRegistrationAccessToken();
//
//        // Get client level role (requires view-clients role)
//        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()) //
//                .roles().get("user").toRepresentation();
//
//        Assign client level role to user
//        userResource.roles()
//                .clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));

        // Send password reset E-Mail
        // VERIFY_EMAIL, UPDATE_PROFILE, CONFIGURE_TOTP, UPDATE_PASSWORD, TERMS_AND_CONDITIONS
//        usersRessource.get(userId).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));

        // Delete User
//        userResource.remove();
    }
    public void sendEmailResetPassword() {
        String clientSecret = "WZ9AiOgQXOyUTXF6WGKrsF5ZAekxjrGm";
        String serverUrl = "http://localhost:8080/";
        String realm = "master";
        Keycloak keycloak = KeycloakBuilder.builder() //
                .serverUrl(serverUrl) //
                .realm(realm) //
                .grantType(OAuth2Constants.PASSWORD) //
                .clientId("admin-cli") //
                .clientSecret(clientSecret) //
                .username("admin") //
                .password("admin") //
                .build();
        RealmResource realmResource = keycloak.realm(realm);
        String userId = "1839a597-1cef-49d7-b682-37195d1d88b1";
        UsersResource usersRessource = realmResource.users();
        usersRessource.get(userId).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
    }
}
//    public AccessToken loadUserDetail(KeycloakAuthenticationToken authentication) {
//        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authentication.getDetails();
//    }