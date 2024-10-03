package com.tecknobit.nova;

import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.equinox.resourcesutils.ResourcesProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static com.tecknobit.equinox.resourcesutils.ResourcesProvider.CUSTOM_CONFIGURATION_FILE_PATH;
import static com.tecknobit.equinox.resourcesutils.ResourcesProvider.DEFAULT_CONFIGURATION_FILE_PATH;
import static com.tecknobit.nova.helpers.resources.NovaResourcesManager.*;

/**
 * The {@code Launcher} class is useful to launch <b>Nova's backend service</b>
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@PropertySources({
        @PropertySource(value = "classpath:" + DEFAULT_CONFIGURATION_FILE_PATH),
        @PropertySource(value = "file:" + CUSTOM_CONFIGURATION_FILE_PATH, ignoreResourceNotFound = true)
})
@EnableAutoConfiguration
@EnableJpaRepositories("com.tecknobit.*")
@EntityScan("com.tecknobit.*")
@ComponentScan("com.tecknobit.nova.*")
@SpringBootApplication
public class Launcher {

    /**
     * Main method to start the backend, will be created also the resources directories if not exist invoking the
     * {@link ResourcesProvider} routine
     *
     * @param args: custom arguments to share with {@link SpringApplication} and with the {@link EquinoxController#protector}
     * @apiNote the arguments scheme:
     * <ul>
     *     <li>
     *         {@link EquinoxController#protector} ->
     *         <ul>
     *          <li>
     *             <b>rss</b> -> launch your java application with "rss" to recreate the server secret <br>
     *                       e.g java -jar Nova.jar rss
     *             </li>
     *              <li>
     *                  <b>dss</b> -> launch your java application with "dss" to delete the current server secret <br>
     *                       e.g java -jar Nova.jar dss
     *              </li>
     *              <li>
     *                  <b>dssi</b> -> launch your java application with "dssi" to delete the current server secret and interrupt
     *                        the current workflow of the server <br>
     *                        e.g java -jar Nova.jar dssi
     *              </li>
     *          </ul>
     *     </li>
     *     <li>
     *         {@link SpringApplication} -> see the allowed arguments <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html">here</a>
     *     </li>
     * </ul>
     */
    public static void main(String[] args) {
        EquinoxController.initEquinoxEnvironment(
                "tecknobit/nova/backend",
                " to correctly register a new user in the Nova system ",
                Launcher.class,
                args,
                LOGOS_DIRECTORY, ASSETS_DIRECTORY, REPORTS_DIRECTORY);
        SpringApplication.run(Launcher.class, args);
    }

}
