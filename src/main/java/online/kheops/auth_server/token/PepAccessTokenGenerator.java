package online.kheops.auth_server.token;

import online.kheops.auth_server.PepAccessTokenBuilder;
import online.kheops.auth_server.accesstoken.*;
import online.kheops.auth_server.entity.User;
import online.kheops.auth_server.principal.KheopsPrincipalInterface;
import online.kheops.auth_server.series.SeriesNotFoundException;
import online.kheops.auth_server.user.UserNotFoundException;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.*;
import static online.kheops.auth_server.user.Users.getOrCreateUser;

class PepAccessTokenGenerator {
    private static final Logger LOG = Logger.getLogger(PepAccessTokenGenerator.class.getName());

    private final ServletContext context;

    private String token;
    private String studyInstanceUID;
    private String seriesInstanceUID;

    private PepAccessTokenGenerator(final ServletContext context) {
        this.context = Objects.requireNonNull(context);
    }

    PepAccessTokenGenerator withToken(final String token) {
        this.token = Objects.requireNonNull(token);
        return this;
    }

    PepAccessTokenGenerator withStudyInstanceUID(final String studyInstanceUID) {
        this.studyInstanceUID = Objects.requireNonNull(studyInstanceUID);
        return this;
    }

    PepAccessTokenGenerator withSeriesInstanceUID(final String seriesInstanceUID) {
        this.seriesInstanceUID = Objects.requireNonNull(seriesInstanceUID);
        return this;
    }

    static PepAccessTokenGenerator createGenerator(final ServletContext context) {
      return new PepAccessTokenGenerator(context);
    }

    String generate(@SuppressWarnings("SameParameterValue") long expiresIn) {

        final AccessToken accessToken;
        try {
            accessToken = AccessTokenVerifier.authenticateAccessToken(context, Objects.requireNonNull(token));
        } catch (AccessTokenVerificationException e) {
            throw new TokenRequestException(TokenRequestException.Error.INVALID_GRANT, e.getMessage(), e);
        } catch (DownloadKeyException e) {
            LOG.log(Level.SEVERE, "Error downloading the public key", e);
            throw new WebApplicationException(Response.status(BAD_GATEWAY).entity("Error downloading the public key").build());
        }

        if (accessToken.getTokenType() == AccessToken.TokenType.PEP_TOKEN ) {
            throw new TokenRequestException(TokenRequestException.Error.INVALID_GRANT, "Request a pep token is unauthorized with a pep token");
        }

        final User callingUser;
        try {
            callingUser = getOrCreateUser(accessToken.getSubject());
        } catch (UserNotFoundException e) {
            throw new TokenRequestException(TokenRequestException.Error.INVALID_GRANT, "User not found", e);
        }

        try {
            final KheopsPrincipalInterface principal = accessToken.newPrincipal(context, callingUser);
            if (!principal.hasSeriesReadAccess(studyInstanceUID, seriesInstanceUID)) {
                throw new SeriesNotFoundException("");
            }
        } catch (SeriesNotFoundException e) {
            throw new TokenRequestException(TokenRequestException.Error.INVALID_GRANT, "The user does not have access to the given StudyInstanceUID and SeriesInstanceUID pair", e);
        }

        LOG.info(() -> "Returning pep token for user: " + accessToken.getSubject() + "for studyInstanceUID " + studyInstanceUID +" seriesInstanceUID " + seriesInstanceUID);
        PepAccessTokenBuilder tokenBuilder =  PepAccessTokenBuilder.newBuilder(accessToken)
                .withExpiresIn(expiresIn)
                .withStudyUID(studyInstanceUID)
                .withSeriesUID(seriesInstanceUID)
                .withSubject(accessToken.getSubject());
        return tokenBuilder.build();
    }
}
