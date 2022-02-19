package com.company.Tests;

import company.Game.GameModel;
import company.View.FileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {

    @Test
    void readFile() {
        FileReader fileReader = FileReader.getInstance();
        GameModel gameModel = null;
        String path = "";

        Assertions.assertEquals(false, fileReader.readFile(gameModel, path));

        path = null;
        assertEquals(false, fileReader.readFile(gameModel, path));
    }

    @Test
    void writeFile() {
        FileReader fileReader = FileReader.getInstance();
        GameModel gameModel = null;
        String path = "";

        assertEquals(false, fileReader.writeFile(null, path));

        path = null;
        assertEquals(false, fileReader.writeFile(null, path));
    }
}