package com.tecknobit.nova.helpers.services;

import com.tecknobit.equinox.annotations.CustomParametersOrder;
import com.tecknobit.equinox.environment.helpers.services.EquinoxUsersHelper;
import com.tecknobit.equinox.resourcesutils.ResourcesManager;
import com.tecknobit.novacore.records.NovaUser;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.NovaUser.ROLE_KEY;

/**
 * The {@code NovaUsersHelper} class is useful to manage all the Nova users database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ResourcesManager
 *
 * @since 1.0.1
 */
@Primary
@Service
public class NovaUsersHelper extends EquinoxUsersHelper<NovaUser> {

    /**
     * {@inheritDoc}
     */
    @Override
    @CustomParametersOrder(order = ROLE_KEY)
    protected List<String> getQueryValuesKeys() {
        ArrayList<String> custom = new ArrayList<>(super.getQueryValuesKeys());
        custom.add(ROLE_KEY);
        return custom;
    }

}
