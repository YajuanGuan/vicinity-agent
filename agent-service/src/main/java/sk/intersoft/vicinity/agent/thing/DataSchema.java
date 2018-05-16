package sk.intersoft.vicinity.agent.thing;

import org.json.JSONObject;
import sk.intersoft.vicinity.agent.utils.Dump;
import sk.intersoft.vicinity.agent.utils.JSONUtil;

import java.util.*;

public class DataSchema {
    public String type;
    public String description;
    public List<DataSchemaField> field= new ArrayList<DataSchemaField>();
    public DataSchema item;

    public static final String OBJECT = "object";
    public static final String ARRAY = "array";

    public static final String BOOLEAN = "boolean";
    public static final String INTEGER = "integer";
    public static final String DOUBLE = "double";
    public static final String STRING = "string";
    public static final Set<String> SIMPLE_TYPES =
            new HashSet<String>(
                    Arrays.asList(BOOLEAN, INTEGER, DOUBLE, STRING));

    // JSON KEYS
    public static final String TYPE_KEY = "type";
    public static final String DESCRIPTION_KEY = "description";
    public static final String FIELD_KEY = "field";
    public static final String ITEM_KEY = "item";

    public static boolean isObject(String type){
        return type.equals(DataSchema.OBJECT);
    }
    public boolean isObject(){
        return isObject(type);
    }

    public static boolean isArray(String type){
        return type.equals(DataSchema.ARRAY);
    }
    public boolean isArray(){
        return isArray(type);
    }

    public static boolean isSimpleType(String type){
        return DataSchema.SIMPLE_TYPES.contains(type);
    }
    public boolean isSimpleType(){
        return isSimpleType(type);
    }

    public static boolean isCorrectType(String type){
        return isObject(type) || isArray(type) || isSimpleType(type);
    }

    public static DataSchema create(JSONObject schemaJSON) throws Exception {
        DataSchema schema = new DataSchema();
        schema.type = JSONUtil.getString(TYPE_KEY, schemaJSON);
        if(schema.type == null) {
            throw new Exception("DataSchema: Missing ["+TYPE_KEY+"] in: "+schemaJSON.toString());
        }

        if(!isCorrectType(schema.type)){
            throw new Exception("DataSchema: Unknown ["+TYPE_KEY+" : "+schema.type+"] in: "+schemaJSON.toString());
        }

        schema.description = JSONUtil.getString(DESCRIPTION_KEY, schemaJSON);

        if(schema.isObject()){
            List<JSONObject> field = JSONUtil.getObjectArray(FIELD_KEY, schemaJSON);
            if(field == null) {
                throw new Exception("DataSchema: Missing ["+FIELD_KEY+"] in: "+schemaJSON.toString());
            }
            if(field.isEmpty()) {
                throw new Exception("DataSchema: Empty ["+FIELD_KEY+"] array in: "+schemaJSON.toString());
            }

            for(JSONObject f : field){
                schema.field.add(DataSchemaField.create(f));
            }

        }
        else if(schema.isArray()){
            JSONObject item = JSONUtil.getObject(ITEM_KEY, schemaJSON);
            if(item == null) {
                throw new Exception("DataSchema: Missing ["+ITEM_KEY+"] in: "+schemaJSON.toString());
            }
            schema.item = DataSchema.create(item);
        }

        return schema;
    }

    public String toString(int indent) {
        Dump dump = new Dump();

        dump.add("data-schema:", indent);
        dump.add("type: "+type, (indent + 1));
        dump.add("description: "+description, (indent + 1));
        if(isObject()){
            dump.add("field: "+field.size(), (indent + 1));
            for(DataSchemaField f : field){
                dump.add(f.toString(indent + 2));
            }
        }
        if(isArray()){
            dump.add("item: ", (indent + 1));
            dump.add(item.toString(indent + 2));

        }

        return dump.toString();
    }

}
