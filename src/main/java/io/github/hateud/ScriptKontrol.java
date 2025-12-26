package io.github.hateud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ScriptKontrol {

    public static void startScript(long userId, DataBase db) {
        ArrayList<Object[]> dataList = db.getData(userId);

        for (Object[] row : dataList) {

            Long apiId  = (Long) row[1];
            String apiHash = (String) row[2];
            String channels = (String) row[3];
            String triggers = (String) row[4];
            try{

                ProcessBuilder pb = new ProcessBuilder("/home/hateud/IdeaProjects/drugivanabot/scripts/venv/bin/python3", "scripts/main.py", Long.toString(userId),Long.toString(apiId), apiHash, channels, triggers);
                pb.redirectErrorStream(true);
                pb.redirectErrorStream(true);

                Process process = pb.start();

// читаем вывод python
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("[PYTHON] " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }
    }
}
