package sk.intersoft.vicinity.agent.service.config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.service.config.processor.ThingDescriptions;
import sk.intersoft.vicinity.agent.thing.ThingDescription;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    final static Logger logger = LoggerFactory.getLogger(Configuration.class.getName());

    private static final String GATEWAY_API_ENDPOINT_KEY = "gateway-api-endpoint";

    public static ConfigurationMappings mappings = new ConfigurationMappings();
    public static String gatewayAPIEndpoint = "";

    public static Map<String, AgentConfig> agents = new HashMap<String, AgentConfig>();
    public static Map<String, AdapterConfig> adapters = new HashMap<String, AdapterConfig>();
    public static Map<String, ThingDescriptions> things = new HashMap<String, ThingDescriptions>();

//    public static boolean configureAgent(String agentId) {
//        try{
//            logger.info("CONFIGURING AGENT ["+agentId+"]");
//            AgentConfig config = mappings.agents.get(agentId);
//            if(config != null){
//                logger.info("AGENT EXISTS .. running configuration");
//                return config.configure();
//            }
//            else {
//                logger.info("AGENT DOES NOT EXISTS .. search in files");
//                AgentConfig newConfig = mappings.addAgent(agentId);
//                if(newConfig != null){
//                    logger.info("NEW CONFIG FOUND: \n"+newConfig.toString(0));
//                    return newConfig.configure();
//                }
//                else {
//                    logger.info("NEW CONFIG NOT FOUND!");
//                    return false;
//                }
//            }
//
//        }
//        catch(Exception e){
//            logger.error("", e);
//            logger.error("UNABLE TO CONFIGURE AGENT ["+agentId+"]");
//        }
//        return false;
//    }

    public static void create() throws Exception {

        String configFile = System.getProperty("service.config");

        logger.debug("CREATING CONFIG FROM FILE : "+configFile);
        JSONObject configSource = new JSONObject(FileUtil.file2string(new File(configFile)));
        gatewayAPIEndpoint = configSource.getString(GATEWAY_API_ENDPOINT_KEY);

        mappings.create();
    }

    public static String toString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT-SERVICE CONFIGURATION STATUS: ", indent);

        dump.add("GatewayAPI Endpoint: " + gatewayAPIEndpoint, indent);
        dump.add(mappings.toString(indent));

        return dump.toString();
    }

    public static String toStatusString(int indent) {
        Dump dump = new Dump();

        dump.add("AGENT-SERVICE CONFIGURATION SUMMARY (all maps): ", indent);

        dump.add("EXPOSED AGENTS: " + agents.values().size(), indent);
        for (Map.Entry<String, AgentConfig> entry : agents.entrySet()) {
            dump.add(entry.getValue().toStatusString(indent + 1));
        }

        dump.add("ALL EXPOSED ADAPTERS: " + adapters.values().size(), indent);
        for (Map.Entry<String, AdapterConfig> entry : adapters.entrySet()) {
            dump.add(entry.getValue().toStatusString(indent + 1));
        }

        dump.add("ALL EXPOSED THINGS: " + things.values().size(), indent);
        for (Map.Entry<String, ThingDescriptions> entry : things.entrySet()) {
            dump.add("for adapter: " + adapters.get(entry.getKey()).adapterId, (indent + 1));
            dump.add(entry.getValue().toStatusString(indent + 2));
        }

        return dump.toString();
    }

}
