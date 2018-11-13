package snutella;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class FileManager {
    private static final String[] FILENAMES = {
            "Adventures of Tintin",
            "Jack and Jill",
            "Glee",
            "The Vampire Diarie",
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
    }
}