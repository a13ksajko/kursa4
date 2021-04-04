package com.mechanitis.demo.quadrapassel;

import com.mechanitis.demo.quadrapassel.pieces.*;
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
    int delay=350;
    int grid_height = 20;
    int grid_width = 14;
    QuadrapasselGrid grid = null;
    Contents[][] CurrentBuffer = new Contents[grid_width][grid_height];
    Piece piece=null;
    int piecex=0;
    int piecey=0;
    Contents[][] DesiredBuffer = new Contents[grid_width][grid_height];
    Random r = new Random();
    Thread simulator;
    Thread syncer;
    Thread difficulty;
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

                if(event.getCode()==KeyCode.LEFT){
                    boolean[][] matrix = new boolean[4][4];
                    int piecex_new = piecex - 1;
                    int piecey_new = piecey;
                    for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 4; j++) {
                            if ((piecex_new + i >= grid_width)||(piecex_new+i<0)) {
                                matrix[i][j] = true;
                                continue;
                            }
                            if ((piecey_new + j >= grid_height)||(piecey_new+j<0)) {
                                matrix[i][j] = true;
                                continue;
                            }
                            matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                        }
                    if (piece.checkThisPosition(matrix)) {
                        piecex=piecex_new;
                        piecey=piecey_new;
                        redraw();
                    }

                }
                if(event.getCode()==KeyCode.RIGHT){
                    boolean[][] matrix = new boolean[4][4];
                    int piecex_new = piecex + 1;
                    int piecey_new = piecey;
                    for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 4; j++) {
                            if ((piecex_new + i >= grid_width)||(piecex_new+i<0)) {
                                matrix[i][j] = true;
                                continue;
                            }
                            if ((piecey_new + j >= grid_height)||(piecey_new+j<0)) {
                                matrix[i][j] = true;
                                continue;
                            }
                            matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                        }
                    if (piece.checkThisPosition(matrix)) {
                        piecex=piecex_new;
                        piecey=piecey_new;
                        redraw();
                    }

                }
                if(event.getCode()==KeyCode.UP){
                    boolean[][] matrix = new boolean[4][4];
                    int piecex_new = piecex;
                    int piecey_new = piecey;
                    for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 4; j++) {
                            if ((piecex_new + i >= grid_width)||(piecex_new+i<0)) {
                                matrix[i][j] = true;
                                continue;
                            }
                            if ((piecey_new + j >= grid_height)||(piecey_new+j<0)) {
                                matrix[i][j] = true;
                                continue;
                            }
                            matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                        }
                    if (piece.checkNextPosition(matrix)) {
                        piece.NextPosition();
                        redraw();
                    }

                }
                if(event.getCode()==KeyCode.DOWN) {
                    movedown();
                    redraw();
                }
            }
        });
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

    public void Die() {
        syncer.interrupt();
        simulator.interrupt();
        difficulty.interrupt();
    }

    public void Run() {

        //Platform.runLater(new BuffersSync(CurrentBuffer,DesiredBuffer,grid));
        SpawnNewPiece();
        difficulty = new Thread(new Difficulty());
        difficulty.start();
        simulator = new Thread(new Simulate());
        simulator.start();
        syncer = new Thread(new BuffersSync());
        syncer.start();
    }

    enum Contents {
        EMPTY, BLUE, STATIC
    }
    private void movedown(){
        boolean[][] matrix = new boolean[4][4];
            int piecey_new = piecey + 1;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex + i >= grid_width)||(piecex+i<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= grid_height)||(piecey_new+j<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    matrix[i][j] = (DesiredBuffer[piecex + i][piecey_new + j] == Contents.STATIC);
                }
            if (!piece.checkThisPosition(matrix)) {
                matrix = piece.getCurrentPosition();
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++) {
                        if (matrix[i][j]) DesiredBuffer[piecex + i][piecey + j] = Contents.STATIC;
                    }
                SpawnNewPiece();
            } else {
                piecey = piecey_new;
            }
        }


    private void redraw(){
        Contents[][] OldBuffer= new Contents[grid_width][grid_height], IntermediateBuffer= new Contents[grid_width][grid_height];
        for (int i = 0; i < grid.width; i++) {
            for (int j = 0; j < grid.height; j++) {
                OldBuffer[i][j] = DesiredBuffer[i][j];
                IntermediateBuffer[i][j] = (OldBuffer[i][j]==Contents.STATIC)?Contents.STATIC:Contents.EMPTY;
            }
        }
        if(piece!=null) {
            boolean [][] matrix;
            matrix = piece.getCurrentPosition();
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if (matrix[i][j]) IntermediateBuffer[piecex + i][piecey + j] = Contents.BLUE;
                }
        }else{
            SpawnNewPiece();
        }

                boolean fullline=true;
                int linesremoved=0;
                for (int j = 0; j < grid.height; j++) {
                    fullline=true;
                    for (int i = 0; i < grid.width; i++) {
                        if(IntermediateBuffer[i][j]==Contents.BLUE)fullline=false;
                        if(IntermediateBuffer[i][j]==Contents.EMPTY)fullline=false;
                    }
                    if(fullline){
                        for (int i = 0; i < grid.width; i++) {
                            IntermediateBuffer[i][j]=Contents.EMPTY;
                        }
                        linesremoved+=1;
                    }
                }
                boolean flag=false;
                if(linesremoved>0){
                    for (int i = 0; i < grid.width; i++) {
                        flag=false;
                        for (int j = grid_height-1; j >=0; j--) {
                            if(IntermediateBuffer[i][j]==Contents.EMPTY){
                                if(!flag){
                                    flag=true;
                                    continue;
                                }
                            }
                            if(flag)
                            if(IntermediateBuffer[i][j]==Contents.STATIC){
                                IntermediateBuffer[i][j]=Contents.EMPTY;
                                IntermediateBuffer[i][j+linesremoved]=Contents.STATIC;
                            }
                        }
                    }
                }

        for (int i = 0; i < grid.width; i++) {
            for (int j = 0; j < grid.height; j++) {
                DesiredBuffer[i][j]=IntermediateBuffer[i][j];
            }
        }


    }
    private class Difficulty implements Runnable{
        @Override
        public void run() {
            while(true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(8000);
                } catch (InterruptedException e) {
                    return;
                }
                if (delay > 10) delay -= 10;
            }
        }
    }
    private class Simulate implements Runnable {

        @Override
        public void run() {
            while (true) {
                movedown();
                redraw();
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private void SpawnNewPiece() {
        int npiece = r.nextInt(7);
        if(npiece==0)piece=new P1();
        if(npiece==1)piece=new P2();
        if(npiece==2)piece=new P3();
        if(npiece==3)piece=new P4();
        if(npiece==4)piece=new P5();
        if(npiece==5)piece=new P6();
        if(npiece==6)piece=new P7();
        piecex=5;
        piecey=0;
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
                                    rectangle.setFill((DesiredBuffer[finalI][finalJ] == Contents.BLUE) ? Color.BLUE : (DesiredBuffer[finalI][finalJ] == Contents.STATIC) ? Color.gray(0.4) : Color.gray(0.6));
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
            }
        }
    }

}
