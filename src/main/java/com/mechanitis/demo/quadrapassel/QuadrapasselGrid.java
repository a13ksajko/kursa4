package com.mechanitis.demo.quadrapassel;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

import static com.mechanitis.demo.quadrapassel.GameplayScene.getNodeFromGridPane;
import static com.mechanitis.demo.quadrapassel.GameplayScene.setNodeFromGridPane;

public class QuadrapasselGrid extends GridPane {
    public Integer width, height;
    public QuadrapasselGrid(Integer width, Integer height){
        super();
        this.height=height;
        this.width=width;
    }


}
