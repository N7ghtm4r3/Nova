package com.tecknobit.nova.controllers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.novacore.records.NovaUser;

/**
 * The {@code DefaultNovaController} class is useful to give the base behavior of the <b>Nova's controllers</b>
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Structure
public abstract class DefaultNovaController extends EquinoxController<NovaUser> {
}
