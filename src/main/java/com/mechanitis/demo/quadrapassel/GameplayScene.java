package com.mechanitis.demo.quadrapassel;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GameplayScene extends Scene {
    public void Die() {
        syncer.interrupt();
        simulator.interrupt();
    }

    private class Simulate implements Runnable {

        @Override
        public void run() {
            Contents[][] OldBuffer;
            OldBuffer = new Contents[grid_width][grid_height];
            while (true) {
                for (int i = 0; i < grid.width; i++) {
                    for (int j = 0; j < grid.height; j++) {
                        OldBuffer[i][j] = DesiredBuffer[i][j];
                    }
                }
                for (int i = 0; i < grid.width; i++) {
                    for (int j = 0; j < grid.height; j++) {
                            if (j >0)
                                DesiredBuffer[i][j] = OldBuffer[i][j-1];
                            else
                                DesiredBuffer[i][j]=Contents.EMPTY;

                    }
                }
                //           Platform.runLater(new BuffersSync());
                System.out.println("Simulated");

                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private class BuffersSync implements Runnable {

        @Override
        public void run() {
            while (true) {
                for (int i = 0; i < grid.width; i++) {
                    for (int j = 0; j < grid.height; j++) {
                        if (CurrentBuffer[i][j] != DesiredBuffer[i][j]) {
                            Rectangle rectangle = (Rectangle) getNodeFromGridPane(grid, i, j);
                            int finalI = i;
                            int finalJ = j;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    rectangle.setFill((DesiredBuffer[finalI][finalJ] == Contents.BLUE) ? Color.BLUE : Color.gray(0.6));
                                    setNodeFromGridPane(grid, finalI, finalJ, rectangle);
                                    CurrentBuffer[finalI][finalJ] = DesiredBuffer[finalI][finalJ];
                                }
                            });

                        }
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(17);
                } catch (InterruptedException e) {
                    return;
                }
                //   System.out.println("Synced");
            }
        }
    }

    enum Contents {
        EMPTY, BLUE
    }

    ;
    int grid_height = 20;
    int grid_width = 14;
    QuadrapasselGrid grid = null;
    Contents[][] CurrentBuffer = new Contents[grid_width][grid_height];
    Contents[][] DesiredBuffer = new Contents[grid_width][grid_height];

    public GameplayScene(Parent root, double width, double height) {
        super(root, width, height);
        AnchorPane rootpane = (AnchorPane) root;
        this.fillProperty().setValue(Color.gray(0.2));
        grid = new QuadrapasselGrid(grid_width, grid_height);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        for (int i = 0; i < grid_width; i++) {
            for (int j = 0; j < grid_height; j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setHeight(20.0);
                rectangle.setWidth(20.0);
                rectangle.setFill(Color.gray(0.6));
                grid.add(rectangle, i, j);
                CurrentBuffer[i][j] = Contents.EMPTY;
                DesiredBuffer[i][j] = Contents.EMPTY;
            }
        }
        AnchorPane.setTopAnchor(grid, 10.0);
        AnchorPane.setLeftAnchor(grid, 10.0);
        AnchorPane.setRightAnchor(grid, 10.0);
        AnchorPane.setBottomAnchor(grid, 10.0);
        rootpane.getChildren().add(grid);
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.SPACE) SpawnNewBlock();
                if(event.getCode()==KeyCode.ENTER){
                    for (int i = 0; i < grid_width; i++) {
                        for (int j = 1; j < grid_height; j++) {
                            DesiredBuffer[i][j] = Contents.BLUE;
                        }
                    }
                }
            }
        });
    }

    Random r = new Random();

    private void SpawnNewBlock() {
        int i = r.nextInt(grid.width);
        int j = 0;
        DesiredBuffer[i][j] = Contents.BLUE;
    }

    Thread simulator;
    Thread syncer;

    public void Run() {

        //Platform.runLater(new BuffersSync(CurrentBuffer,DesiredBuffer,grid));
        simulator = new Thread(new Simulate());
        simulator.start();
        syncer = new Thread(new BuffersSync());
        syncer.start();
    }

    public static Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    public static Node setNodeFromGridPane(GridPane gridPane, int col, int row, Node newnode) {
        Node tobereplaced = null;
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                tobereplaced = node;
                break;
            }
        }
        gridPane.getChildren().set(gridPane.getChildren().indexOf(tobereplaced), newnode);
        return null;
    }

}
