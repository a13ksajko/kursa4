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
    QuadrapasselGrid grid = null;
    int grid_height = 20;
    int grid_width = 14;
    public GameplayScene(Parent root, double width, double height) {
        super(root, width, height);
        AnchorPane rootpane = (AnchorPane) root;
        this.fillProperty().setValue(Color.gray(0.2));
        grid = new QuadrapasselGrid(grid_width, grid_height);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(grid, 10.0);
        AnchorPane.setLeftAnchor(grid, 10.0);
        AnchorPane.setRightAnchor(grid, 10.0);
        AnchorPane.setBottomAnchor(grid, 10.0);
        rootpane.getChildren().add(grid);
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if(event.getCode()==KeyCode.LEFT){
                    grid.queueMove(QuadrapasselGrid.Movement.LEFT);

                }
                if(event.getCode()==KeyCode.RIGHT){
                    grid.queueMove(QuadrapasselGrid.Movement.RIGHT);

                }
                if(event.getCode()==KeyCode.UP){
                    grid.queueMove(QuadrapasselGrid.Movement.ROTATE);


                }
                if(event.getCode()==KeyCode.DOWN) {
                    grid.queueMove(QuadrapasselGrid.Movement.DOWN);

                }
            }
        });
    }
    public void Die() {
        grid.Die();
    }

}
