package com.tecknobit.nova.helpers.services;

import com.tecknobit.equinox.annotations.CustomParametersOrder;
import com.tecknobit.equinox.environment.helpers.services.EquinoxUsersHelper;
import com.tecknobit.novacore.records.NovaUser;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.novacore.records.NovaUser.ROLE_KEY;

@Primary
@Service
public class NovaUsersHelper extends EquinoxUsersHelper<NovaUser> {

    @Override
    @CustomParametersOrder(order = ROLE_KEY)
    protected List<String> getQueryValuesKeys() {
        ArrayList<String> custom = new ArrayList<>(super.getQueryValuesKeys());
        custom.add(ROLE_KEY);
        return custom;
    }

}
