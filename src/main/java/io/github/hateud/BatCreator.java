package io.github.hateud;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BatCreator {

    public static void createBat(long userId, DataBase db) throws IOException {
        ArrayList<Object[]> dataList = db.getData(userId);

        for (Object[] row : dataList) {
            Long apiId = (Long) row[1];
            String apiHash = (String) row[2];
            String channels = (String) row[3];
            String triggers = (String) row[4];

            String bat =
                    "start \"\" main.exe " +
                            userId + " " +
                            apiId + " " +
                            apiHash + " " +
                            channels + " " +
                            triggers + "\n";
            try (FileWriter writer = new FileWriter("start.bat")) {
                writer.write(bat);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void deleteBat() throws IOException {
        File file = new File("start.bat");
        try{
            file.delete();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}