package com.tecknobit.novacore.helpers;

import java.util.List;

import static com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.HOST_ADDRESS_KEY;
import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.User.*;

public interface LocalSessionUtils {

    String DATABASE_NAME = "NovaSessions.db";

    String SESSIONS_TABLE = "sessions";

    String CREATE_SESSIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SESSIONS_TABLE + " (" +
                    IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY,\n" +
                    TOKEN_KEY + " VARCHAR(32) NOT NULL,\n" +
                    PROFILE_PIC_URL_KEY + " TEXT NOT NULL,\n" +
                    EMAIL_KEY + " VARCHAR(75) NOT NULL,\n" +
                    PASSWORD_KEY + " VARCHAR(32) NOT NULL,\n" +
                    HOST_ADDRESS_KEY + " VARCHAR(75) NOT NULL,\n" +
                    ROLE_KEY + " VARCHAR(8) NOT NULL\n"
                    + ");";

    void insertSession(String id, String token, String profilePicUrl, String email, String password, String hostAddress,
                       Role role);

    List<NovaSession> getSessions();

    NovaSession getSession(String id);

    void deleteAllSessions();

    void deleteSession(String id);

    record NovaSession(String id, String token, String profilePicUrl, String email, String password, String hostAddress,
                       Role role) {

        public static final String HOST_ADDRESS_KEY = "host_address";

    }

}
