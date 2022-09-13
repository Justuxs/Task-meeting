package com.task.meeting.repositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class InOut {
    public static void Write(String filePath , String string){
        try {
            File file = new File(filePath);
            if (file.createNewFile()) {
            } else {
                DeleteFile(filePath);
                Write(filePath,string);
                return ;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(string);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String Read(String filePath){
        String json = "";
        try {
            File file = new File(filePath);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                json+=(data);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return json;
    }
    private static void DeleteFile(String filePath){
        File file = new File(filePath);
        file.delete();
    }
}
