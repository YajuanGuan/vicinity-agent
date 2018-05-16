package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.List;

public class InteractionPattern {

    public String id = null;
    public String refersTo = null;
    public DataSchema output = null;
    public InteractionPatternEndpoint readEndpoint = null;
    public InteractionPatternEndpoint writeEndpoint = null;


    // PATTERN TYPES
    public static final String PROPERTY = "property";
    public static final String ACTION = "action";
    public static final String EVENT = "event";


    // JSON keys
    public static final String PID_KEY = "pid";
    public static final String AID_KEY = "aid";
    public static final String EID_KEY = "eid";

    public static final String MONITORS_KEY = "monitors";
    public static final String AFFECTS_KEY = "affects";

    public static final String OUTPUT_KEY = "output";

    public static final String READ_LINK_KEY = "read_link";
    public static final String WRITE_LINK_KEY = "write_link";





    public static void createLinks(InteractionPattern pattern,
                                   JSONObject patternJSON,
                                   ThingValidator validator) throws Exception {
        JSONObject read = JSONUtil.getObject(READ_LINK_KEY, patternJSON);
        JSONObject write = JSONUtil.getObject(WRITE_LINK_KEY, patternJSON);


        if(read != null || write != null){
            pattern.readEndpoint = InteractionPatternEndpoint.create(read, InteractionPatternEndpoint.READ, validator);
            pattern.writeEndpoint = InteractionPatternEndpoint.create(write, InteractionPatternEndpoint.WRITE, validator);
        }
        else {
            validator.error("At least one of read_link/write_link must be defined in: "+patternJSON.toString());
        }
    }
    public static InteractionPattern createProperty(JSONObject patternJSON, ThingValidator validator) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        try{
            pattern.id = JSONUtil.getString(PID_KEY, patternJSON);
            if(pattern.id == null) validator.error("Missing ["+PID_KEY+"] in property: "+patternJSON.toString());

            pattern.refersTo = JSONUtil.getString(MONITORS_KEY, patternJSON);
            if(pattern.refersTo == null) validator.error("Missing ["+MONITORS_KEY+"] in property: "+patternJSON.toString());

            createLinks(pattern, patternJSON, validator);
        }
        catch(Exception e){
            validator.error("Unable to process property: "+patternJSON.toString());
        }
        return pattern;

    }

    public static InteractionPattern createAction(JSONObject patternJSON, ThingValidator validator) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        try{
            pattern.id = JSONUtil.getString(AID_KEY, patternJSON);
            if(pattern.id == null) validator.error("Missing ["+AID_KEY+"] in action: "+patternJSON.toString());

            pattern.refersTo = JSONUtil.getString(AFFECTS_KEY, patternJSON);
            if(pattern.refersTo == null) validator.error("Missing ["+AFFECTS_KEY+"] in action: "+patternJSON.toString());

            createLinks(pattern, patternJSON, validator);
        }
        catch(Exception e){
            validator.error("Unable to process action: "+patternJSON.toString());
        }

        return pattern;

    }

    public static InteractionPattern createEvent(JSONObject patternJSON, ThingValidator validator) throws Exception {
        InteractionPattern pattern = new InteractionPattern();

        try{
            pattern.id = JSONUtil.getString(EID_KEY, patternJSON);
            if(pattern.id == null) validator.error("Missing ["+EID_KEY+"] in event: "+patternJSON.toString());

            pattern.refersTo = JSONUtil.getString(MONITORS_KEY, patternJSON);
            if(pattern.refersTo == null) validator.error("Missing ["+MONITORS_KEY+"] in event: "+patternJSON.toString());

            JSONObject output = JSONUtil.getObject(DataSchema.OUTPUT_KEY, patternJSON);
            if(output == null) validator.error("Missing ["+DataSchema.OUTPUT_KEY+"] in event: "+patternJSON.toString());
            pattern.output = DataSchema.create(output, validator);
        }
        catch(Exception e){
            validator.error("Unable to process event: "+patternJSON.toString());
        }

        return pattern;

    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("INTERACTION PATTERN:", indent);
        dump.add("id: "+id, (indent + 1));
        dump.add("refers-to (monitors/affects): "+refersTo, (indent + 1));


        if(output != null){
            dump.add("OUTPUT: ", (indent + 1));
            dump.add(output.toString(indent + 2));
        }

        dump.add("ENDPOINTS: ", (indent + 1));
        if(readEndpoint != null){
            dump.add("read endpoint: ", (indent + 2));
            dump.add(readEndpoint.toString(indent + 3));
        }
        else {
            dump.add("read endpoint: unset", (indent + 2));
        }

        if(writeEndpoint != null){
            dump.add("write endpoint: ", (indent + 2));
            dump.add(writeEndpoint.toString(indent + 3));
        }
        else {
            dump.add("write endpoint: unset", (indent + 2));
        }

        return dump.toString();
    }
}
