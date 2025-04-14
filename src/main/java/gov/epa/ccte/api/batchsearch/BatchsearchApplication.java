package gov.epa.ccte.api.batchsearch;

import gov.epa.ccte.api.batchsearch.config.ApplicationProperties;
import gov.epa.ccte.api.batchsearch.config.Constants;
import gov.epa.ccte.api.batchsearch.config.DefaultProfileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class BatchsearchApplication implements InitializingBean {

    private static final Integer startPort = 9300;
    private static final Integer endPort = 9350;
    private static Integer port;
    private final Environment env;
    private final ApplicationProperties appProps;

    public BatchsearchApplication(Environment env, ApplicationProperties appProps) {
        this.env = env;
        this.appProps = appProps;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                    "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                    "run with both the 'dev' and 'cloud' profiles at the same time.");
        }

/*		if(!activeProfiles.contains("cloud")){
			Integer serverPort = appProps.getServerPort();
			System.setProperty("server.port", String.valueOf(serverPort));
			log.debug("cloud profile not found. Port {} set for non-cloud profiles", serverPort);
		}else{
			// cloud profile
			Integer serverPort = SocketUtils.findAvailableTcpPort(appProps.getStartPort(), appProps.getEndPort());;
			System.setProperty("server.port", String.valueOf(serverPort));
			log.debug("cloud profile found. Port {} set for cloud profile", serverPort);
		}*/
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        log.info("*** Application is started. ***");

        String profile = System.getProperty("spring.profiles.active");

        if (profile == null)
            profile = "dev"; // default profile

        // This is the only way to use random port with eureka server otherwise eureka server has port number set in application.yml
        //(startPort, endPort);
        setRandomPort(startPort, endPort, profile);

        SpringApplication app = new SpringApplication(BatchsearchApplication.class);

        DefaultProfileUtil.addDefaultProfile(app);
        ConfigurableApplicationContext ctx = app.run(args);
        //ctx.getParent().get;
        Environment env = ctx.getEnvironment();

        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {

        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isEmpty(contextPath)) {
            contextPath = "/";
        }

        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }

        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}{}\n\t" +
                        "External: \t{}://{}:{}{}\n\t" +
                        "Temp folder: \t{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                getTempDir(),
                env.getActiveProfiles()
        );

        String configServerStatus = env.getProperty("configserver.status");
        if (configServerStatus == null) {
            configServerStatus = "Not found or not setup for this application";
        }
        //log.info("Config Server: ----------------------------------------------------------" + configServerStatus.toString());
    }

    public static Object getTempDir() {
        return System.getProperty("java.io.tmpdir");
        //return System.getProperty("user.dir");
    }


    public static void setRandomPort(int startPOrt, int endPort, String profiles) {
        int port = 8096;

        if (profiles.contains("dynamic")) {
            for (int port_index = startPOrt; port_index <= endPort; port_index++) {
                try {
                    ServerSocket socket = new ServerSocket(port_index);
                    socket.close();
                    port = port_index;
                } catch (IOException e) {

                }
            }
            System.setProperty("server.port", String.valueOf(port));
            log.info("dynamic is a profile. Server port set to {} ", port);

        } else {
            System.setProperty("server.port", String.valueOf(port));
            log.info("No dynamic profile found. Server port set to {} ", port);
        }

        // int port = 9300;
    }
}
