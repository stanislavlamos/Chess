package com.company.Tests;

import company.Board.BoardLocation;
import company.Board.Files;
import company.Pieces.LocationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationUtilsTest {

    @Test
    void createBoardLocation() {
        BoardLocation currentLocation;
        Files file;
        Integer rank;
        Integer offsetX = -40;
        Integer offsetY = -50;

        for (int i = 0; i < 8; i++) {
            file = Files.values()[i];
            for (int j = 0; j < 8; j++){
                rank = j + 1;
                currentLocation = new BoardLocation(file ,rank);
                Assertions.assertEquals(null, LocationUtils.createBoardLocation(currentLocation, offsetX, offsetY));
            }
        }


    }
}