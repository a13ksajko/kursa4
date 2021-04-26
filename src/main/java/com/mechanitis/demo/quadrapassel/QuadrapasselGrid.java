package com.mechanitis.demo.quadrapassel;

import com.mechanitis.demo.quadrapassel.pieces.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class QuadrapasselGrid extends GridPane {
    private Queue<Movement> queue;
    final Thread queueProcessor;
    final Thread simulator;
    Object lock = new Object();
    public enum Movement{
        LEFT,
        RIGHT,
        DOWN,
        ROTATE
    }
    enum Contents {
        EMPTY, BLUE, STATIC
    }
    private class Syncer implements Runnable{
        Rectangle rectangle;
        final Integer finalI;
        final Integer finalJ;
        QuadrapasselGrid grid;
        public Syncer(Rectangle rectangle, Integer finalI, Integer finalJ, QuadrapasselGrid grid){
            this.rectangle=rectangle;
            this.finalI=finalI;
            this.finalJ=finalJ;
            this.grid=grid;
        }
        @Override
        public void run() {
            rectangle.setFill((DesiredBuffer[finalI][finalJ] == Contents.BLUE) ? Color.BLUE : (DesiredBuffer[finalI][finalJ] == Contents.STATIC) ? Color.gray(0.4) : Color.gray(0.6));
            setNodeFromGridPane(grid, finalI, finalJ, rectangle);
            CurrentBuffer[finalI][finalJ] = DesiredBuffer[finalI][finalJ];
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
    private class Simulator implements Runnable{

        @Override
        public void run() {
            while (true) {
                queueMove(Movement.DOWN);
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
    private class QueueProcessor implements Runnable{
        @Override
        public void run() {
            while(true){
                if(!queue.isEmpty()){processQueue();continue;}
                try {
                    synchronized (lock) {
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
    public Integer width, height;
    Contents[][] CurrentBuffer;
    int delay=350;
    Piece piece=null;
    int piecex=0;
    int piecey=0;
    Contents[][] DesiredBuffer;
    Random r = new Random();
    public QuadrapasselGrid(Integer width, Integer height){
        super();
        this.height=height;
        this.width=width;
        CurrentBuffer = new Contents[width][height];
        DesiredBuffer = new Contents[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setHeight(20.0);
                rectangle.setWidth(20.0);
                rectangle.setFill(Color.gray(0.6));
                this.add(rectangle, i, j);
                CurrentBuffer[i][j] = Contents.EMPTY;
                DesiredBuffer[i][j] = Contents.EMPTY;
            }
        }
        queue = new LinkedList<Movement>();
        SpawnNewPiece();
        queueProcessor = new Thread(new QueueProcessor());
        queueProcessor.start();
        simulator = new Thread(new Simulator());
        simulator.start();
    }
    public synchronized void queueMove(Movement move){
        queue.add(move);
        synchronized (lock) {
            lock.notify();
        }
    }
    private void processQueue(){
        Movement move = queue.remove();
        if(move==Movement.DOWN){

            boolean[][] matrix = new boolean[4][4];
            int piecey_new = piecey + 1;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex + i >= width)||(piecex+i<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= height)||(piecey_new+j<0)) {
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
        if(move==Movement.ROTATE){
            boolean[][] matrix = new boolean[4][4];
            int piecex_new = piecex;
            int piecey_new = piecey;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex_new + i >= width)||(piecex_new+i<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= height)||(piecey_new+j<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                }
            if (piece.checkNextPosition(matrix)) {
                piece.NextPosition();
            }
        }
        if(move==Movement.RIGHT){
            boolean[][] matrix = new boolean[4][4];
            int piecex_new = piecex + 1;
            int piecey_new = piecey;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex_new + i >= width)||(piecex_new+i<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= height)||(piecey_new+j<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                }
            if (piece.checkThisPosition(matrix)) {
                piecex=piecex_new;
            }

        }
        if(move==Movement.LEFT){
            boolean[][] matrix = new boolean[4][4];
            int piecex_new = piecex - 1;
            int piecey_new = piecey;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex_new + i >= width)||(piecex_new+i<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= height)||(piecey_new+j<0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                }
            if (piece.checkThisPosition(matrix)) {
                piecex=piecex_new;
            }

        }
        redraw();
    }
    private void redraw(){
        Contents[][]  IntermediateBuffer= new Contents[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                IntermediateBuffer[i][j] = (DesiredBuffer[i][j]== Contents.STATIC)? Contents.STATIC: Contents.EMPTY;
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
        int linesremoved=0; //для подсчёта очков
        for (int j = 0; j < height; j++) {
            fullline=true;
            for (int i = 0; i < width; i++) {
                if(IntermediateBuffer[i][j]== Contents.BLUE)fullline=false;
                if(IntermediateBuffer[i][j]== Contents.EMPTY)fullline=false;
            }
            if(fullline){
                for (int i = 0; i < width; i++) {
                    IntermediateBuffer[i][j]= Contents.EMPTY;
                }
                linesremoved+=1;
            }
        }
        boolean flag=false;
        if(linesremoved>0){
            for (int i = 0; i < width; i++) {
                flag=false;
                for (int j = height-1; j >=0; j--) {
                    if(IntermediateBuffer[i][j]== Contents.EMPTY){
                        if(!flag){
                            flag=true;
                            continue;
                        }
                    }
                    if(flag)
                        if(IntermediateBuffer[i][j]== Contents.STATIC){
                            IntermediateBuffer[i][j]= Contents.EMPTY;
                            IntermediateBuffer[i][j+linesremoved]= Contents.STATIC;
                        }
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                DesiredBuffer[i][j]=IntermediateBuffer[i][j];
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (CurrentBuffer[i][j] != DesiredBuffer[i][j]) {
                    Rectangle rectangle = (Rectangle) getNodeFromGridPane(this, i, j);
                    int finalI = i;
                    int finalJ = j;
                    Platform.runLater(new Syncer(rectangle,finalI,finalJ,this));

                }
            }
        }

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
        boolean[][] matrix = new boolean[4][4];
        int piecex_new = piecex;
        int piecey_new = piecey;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if ((piecex_new + i >= width)||(piecex_new+i<0)) {
                    matrix[i][j] = true;
                    continue;
                }
                if ((piecey_new + j >= height)||(piecey_new+j<0)) {
                    matrix[i][j] = true;
                    continue;
                }
                matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
            }
        if(!piece.checkThisPosition(matrix))Die();
    }
    public void Die() {
        queueProcessor.interrupt();
        simulator.interrupt();
    }
}
