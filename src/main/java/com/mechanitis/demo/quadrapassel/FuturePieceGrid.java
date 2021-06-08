package com.mechanitis.demo.quadrapassel;

import com.mechanitis.demo.quadrapassel.pieces.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class FuturePieceGrid extends GridPane {
    private class MyRunnable implements Runnable{
        FuturePieceGrid fpgrid;
        Piece piece;
        public MyRunnable(FuturePieceGrid fpgrid, Piece piece){
            this.fpgrid=fpgrid;
            this.piece=piece;
        }
        @Override
        public void run() {
            boolean[][] matrix = piece.getCurrentPosition();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Rectangle r = (Rectangle) getNodeFromGridPane(fpgrid,i,j);
                    r.setFill(matrix[i][j]?Color.BLUE:Color.gray(0.6));
                    setNodeFromGridPane(fpgrid,i,j,(Node) r);
                }
            }
        }
    }
    Piece piece;
    int npiece;
    Random r = new Random();
    int width=4;
    int height=4;
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

    public FuturePieceGrid(){
        super();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setHeight(20.0);
                rectangle.setWidth(20.0);
                rectangle.setFill(Color.gray(0.6));
                this.add(rectangle, i, j);
            }
        }
        NextPiece();
    }
    void NextPiece(){
        npiece = r.nextInt(7);
        if (npiece == 0) piece = new P1();
        if (npiece == 1) piece = new P2();
        if (npiece == 2) piece = new P3();
        if (npiece == 3) piece = new P4();
        if (npiece == 4) piece = new P5();
        if (npiece == 5) piece = new P6();
        if (npiece == 6) piece = new P7();
        Platform.runLater(new MyRunnable(this,piece));

    }
}
