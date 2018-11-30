package snutella;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;

public class FileManager {
    private static final String FILE_DIRECTORY = "files";
    private static final String[] FILENAMES = {
            "Adventures of Tintin",
            "Jack and Jill",
            "Glee",
            "The Vampire Diaries",
            "King Arthur",
            "Windows XP",
            "Harry Potter",
            "Kung Fu Panda",
            "Lady Gaga",
            "Twilight",
            "Windows 8",
            "Mission Impossible",
            "Turn Up The Music",
            "Super Mario",
            "American Pickers",
            "Microsoft Office 2010",
            "Happy Feet",
            "Modern Family",
            "American Idol",
            "Hacking for Dummies"
    };

    private Set<String> availableFiles;

    public FileManager() {
        this.initializeRandomFiles();
    }

    public void initializeRandomFiles() {
        this.availableFiles = new HashSet<>();
        Random random = new Random();
        int numberOfFiles = random.nextInt(2) + 3;
        while (this.availableFiles.size() != numberOfFiles) {
            this.availableFiles.add(
                    FILENAMES[random.nextInt(FILENAMES.length)]);
        }
        createFiles();
    }

    private void createFiles() {
        File folder = new File(FILE_DIRECTORY);
        folder.mkdirs();
        folder.deleteOnExit();
        for (String filename: availableFiles) {
            try {
                File file = new File(FILE_DIRECTORY, filename);
                file.createNewFile();
                Random random = new Random();
                int fileSize = 2 * 1024 * 1024 + random.nextInt(8 * 1024 * 1024);
                RandomAccessFile randomAccessFile =
                        new RandomAccessFile(file, "rw");   // Code from Thuvarakan
                randomAccessFile.setLength(fileSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean satisfyQuery(String filename, String query) {
        Set<String> filenameWords = new HashSet<>();
        Collections.addAll(filenameWords, filename.toLowerCase().split(" "));
        Set<String> queryWords = new HashSet<>();
        Collections.addAll(queryWords, query.toLowerCase().split(" "));

        Set<String> intersection = new HashSet<>(filenameWords);
        intersection.retainAll(queryWords);
        return intersection.size() > 0;
    }
    public List<String> search(String query) {
        return this.availableFiles.stream()
                .filter(filename ->
                    satisfyQuery(filename, query)
                ).collect(Collectors.toList());
    }

    public void refreshFiles() {
        this.initializeRandomFiles();
    }

    public String getFilesString() {
        return this.availableFiles.stream().reduce("", (s1, s2) -> s1 + s2 + "\n");
    }
}
