package fr.inria.diverse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Utils {

    /**
     * Save an object to a json file, create the parent folder if necessary
     * @param fileName the file name
     * @param object the object to save
     * @param <T> the object type
     */
    public static <T> void save(String fileName,T object){
        ObjectMapper mapper = new ObjectMapper();
        try {
            File f =new File(fileName);
            File parent = f.getParentFile();
            f.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(f,object);
        } catch (IOException e) {
            throw new RuntimeException("Error while saving results",e);
        }
    }
}
