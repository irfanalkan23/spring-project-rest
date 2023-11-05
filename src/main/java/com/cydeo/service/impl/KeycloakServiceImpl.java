package com.cydeo.service.impl;

import com.cydeo.config.KeycloakProperties;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Service
public class KeycloakServiceImpl implements KeycloakService {


    private final KeycloakProperties keycloakProperties;

    public KeycloakServiceImpl(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    @Override
    public Response userCreate(UserDTO userDTO) {

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(userDTO.getPassWord());

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(userDTO.getUserName());
        keycloakUser.setFirstName(userDTO.getFirstName());
        keycloakUser.setLastName(userDTO.getLastName());
        keycloakUser.setEmail(userDTO.getUserName());
        keycloakUser.setCredentials(asList(credential));
        keycloakUser.setEmailVerified(true);
        keycloakUser.setEnabled(true);

        //a Keycloak instance is created when a user logs in
        Keycloak keycloak = getKeycloakInstance();

        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        UsersResource usersResource = realmResource.users();

        // Create Keycloak user
        Response result = usersResource.create(keycloakUser);

        String userId = getCreatedId(result);
        ClientRepresentation appClient = realmResource.clients()
                .findByClientId(keycloakProperties.getClientId()).get(0);

        //matching the user role to the role in Keycloak
        RoleRepresentation userClientRole = realmResource.clients().get(appClient.getId()) //
                .roles().get(userDTO.getRole().getDescription()).toRepresentation();

        realmResource.users().get(userId).roles().clientLevel(appClient.getId())
                .add(List.of(userClientRole));


        keycloak.close();
        return result;
    }

    @Override
    public void delete(String userName) {

        //a Keycloak instance is created when a user logs in
        Keycloak keycloak = getKeycloakInstance();

        //go to realm in Keycloak (cydeo-dev)
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        //get all the users
        UsersResource usersResource = realmResource.users();

        //find the user with usernamw
        List<UserRepresentation> userRepresentations = usersResource.search(userName);
        //get the user id and delete
        String uid = userRepresentations.get(0).getId();
        usersResource.delete(uid);

        keycloak.close();
    }

    private Keycloak getKeycloakInstance(){
        return Keycloak.getInstance(keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getMasterRealm(), keycloakProperties.getMasterUser()
                , keycloakProperties.getMasterUserPswd(), keycloakProperties.getMasterClient());
    }
}