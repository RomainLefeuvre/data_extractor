package fr.inria.diverse;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.inria.diverse.model.RawRepositoryList;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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

    public static RawRepositoryList read(String fileName){
        ObjectMapper mapper = new ObjectMapper();

        try {
            File f =new File(fileName);
            return mapper.readValue(f, RawRepositoryList.class);
        } catch (IOException e) {
            throw new RuntimeException("Error while getting checkpoint",e);
        }
    }

   public static void deleteDirectoryStream(String string)  {
        Path path =Paths.get(string);
        assert(path.startsWith(Paths.get(".").toAbsolutePath()));
       try {
           Files.walk(path)
                   .sorted(Comparator.reverseOrder())
                   .map(Path::toFile)
                   .forEach(File::delete);
       } catch (IOException e) {
           new RuntimeException("Error while deleting",e);
       }
   }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
              {
                  try {
                      Files.walk(Paths.get(sourceDirectoryLocation))
                              .forEach(source -> {
                                  Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                                          .substring(sourceDirectoryLocation.length()));
                                  try {
                                      Files.copy(source, destination, REPLACE_EXISTING);
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }
                              });
                  } catch (IOException e) {
                      throw new RuntimeException("Error while copying",e);
                  }
              }
}
