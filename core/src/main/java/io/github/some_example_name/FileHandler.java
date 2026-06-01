package io.github.some_example_name;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
public class FileHandler {
    String location;

    public FileHandler(String location){
        this.location = location;
    }

    public void loadFile(String[] args) throws IOException {

        File fp = new File(location);
        try {
            Scanner finput = new Scanner(fp);

            while(finput.hasNextLine()){
                String data = finput.nextLine();
                System.out.println(data);
                String[] fields = data.split(",");
                System.out.println(Arrays.toString(fields));
            }

        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }

    public void writeFile(String[] data) throws IOException {

        File fp = new File(location);

        try {
            fp.createNewFile();
            Scanner finput = new Scanner(fp);
            FileWriter writer = new FileWriter(location, true);
            for (int i = 0; i < data.length; i++) {
                writer.write(data[i]);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
