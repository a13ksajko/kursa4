package com.mechanitis.demo.quadrapassel;

import com.mechanitis.demo.quadrapassel.pieces.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class QuadrapasselGrid extends GridPane {
    final Thread queueProcessor;    //поток-обработчик очереди инструкций
    final Thread simulator;         //поток сдвига детали вниз (с определённым дилеем)
    final Thread difficultyThread;  //поток понижающий дилей
    public Integer width, height;   //размеры решётки
    Object lock = new Object();     //мьютекс для очереди
    Contents[][] CurrentBuffer;     //видимое игровое поле
    int delay = 350;                //задержка
    Piece piece = null;             //активная (падающая) фигура
    int piecex = 0;                 //координаты левой верхней точки фигуры
    int piecey = 0;
    Contents[][] DesiredBuffer;     //скрытый редактируемый буффер игр поля (нужен чтобы не перерисовывать всё)
    Random r = new Random();        //ГСЧ
    private Queue<Movement> queue;  //очередь всего
    FuturePieceGrid fpgrid;         //ссылка на решётку следующей фигуры
    GameplayScene scene;
    int chance=30;                  //шанс тетрис-момента
    public QuadrapasselGrid(Integer width, Integer height, FuturePieceGrid fpgrid, GameplayScene gameplayScene) {
        super();                    //вызываем конструктор родителя
        this.scene=gameplayScene;   //переприсвааеваем ссылки из параметров
        this.height = height;
        this.width = width;
        this.fpgrid=fpgrid;
        CurrentBuffer = new Contents[width][height];    //инициализируем буфферы
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
        }                                               //конец инициализации
        queue = new LinkedList<Movement>();             //создаём список с интерфейсом очереди
        SpawnNewPiece();                                //генерируем новую фигуру на игровом поле
        queueProcessor = new Thread(new QueueProcessor());  //стартуем!!
        queueProcessor.start();
        simulator = new Thread(new Simulator());
        simulator.start();
        difficultyThread= new Thread(new Difficulty());
        difficultyThread.start();
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
        Node tobereplaced = null;       //создаём пустую ссылку для старого квадрата, который впоследствии будет заменён на новый
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                tobereplaced = node;        //находим старый нужный элемент списка и берём на него ссылку
                break;
            }
        }
        gridPane.getChildren().set(gridPane.getChildren().indexOf(tobereplaced), newnode);      //заменили мать
        return null;
    }

    public synchronized void queueMove(Movement move) {//добавить любое действие в очередь
        queue.add(move);        //добавить действие в очередь
        synchronized (lock) {   //потоку, ждущему на замке, выслать уведомление
            lock.notify();
        }
    }

    private void processQueue() {
        if(queue.isEmpty())return;
        Movement move = queue.remove(); //взять действие и удалить его из очереди
        if(move==Movement.FUN){         //елси противник прокинул fun, то мы его обрабатываем
            Fun();
        }
        if(piece == null)return;        //если акт фигуры нет, вiйди разбiйник
        if (move == Movement.DOWN) {
            boolean[][] matrix = new boolean[4][4];
            int piecey_new = piecey + 1;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex + i >= width) || (piecex + i < 0)) {    //за гранью реальности
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= height) || (piecey_new + j < 0)) {   //за гранью нереальности (а похуй)
                        matrix[i][j] = true;
                        continue;
                    }
                    matrix[i][j] = (DesiredBuffer[piecex + i][piecey_new + j] == Contents.STATIC);
                }
            if (!piece.checkThisPosition(matrix)) { //акт фигуру, которая не может упасть, делаем в статик
                matrix = piece.getCurrentPosition();
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++) {
                        if (matrix[i][j]) DesiredBuffer[piecex + i][piecey + j] = Contents.STATIC;
                    }
                piece = null;   //вызываем новую фигуру
            } else {
                piecey = piecey_new;
            }
        }
        if (move == Movement.ROTATE) {
            boolean[][] matrix = new boolean[4][4];
            int piecex_new = piecex;
            int piecey_new = piecey;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex_new + i >= width) || (piecex_new + i < 0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= height) || (piecey_new + j < 0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                }
            if (piece.checkNextPosition(matrix)) {
                piece.NextPosition();
            }
        }
        if (move == Movement.RIGHT) {
            boolean[][] matrix = new boolean[4][4];
            int piecex_new = piecex + 1;
            int piecey_new = piecey;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex_new + i >= width) || (piecex_new + i < 0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= height) || (piecey_new + j < 0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                }
            if (piece.checkThisPosition(matrix)) {
                piecex = piecex_new;
            }
        }
        if (move == Movement.LEFT) {
            boolean[][] matrix = new boolean[4][4];
            int piecex_new = piecex - 1;
            int piecey_new = piecey;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((piecex_new + i >= width) || (piecex_new + i < 0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    if ((piecey_new + j >= height) || (piecey_new + j < 0)) {
                        matrix[i][j] = true;
                        continue;
                    }
                    matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
                }
            if (piece.checkThisPosition(matrix)) {
                piecex = piecex_new;
            }

        }
        redraw();
    }

    private void Fun() {
        int skip=r.nextInt(width);
        for(int j=0;j<height-1;j++){
            for(int i=0;i<width;i++){
                DesiredBuffer[i][j]=DesiredBuffer[i][j+1];
            }
        }
        for(int i=0;i<width;i++) {
            if(i==skip){DesiredBuffer[i][height-1]=Contents.EMPTY;continue;}
            DesiredBuffer[i][height-1]=Contents.STATIC;
        }
    }

    private void redraw() {
        Contents[][] IntermediateBuffer = new Contents[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                IntermediateBuffer[i][j] = (DesiredBuffer[i][j] == Contents.STATIC) ? Contents.STATIC : Contents.EMPTY;
            }
        }
        if (piece == null) {
            boolean fullline = true;
            for (int j = height - 1; j >= 0; j--) {
                fullline = true;
                for (int i = 0; i < width; i++) {
                    if (IntermediateBuffer[i][j] == Contents.EMPTY) fullline = false;
                }
                if (fullline) {
                    scene.UpScore();
                    if(r.nextInt(100)<=chance)
                        scene.SendFun();
                    for (int k = j - 1; k >= 0; k--) {
                        for (int i = 0; i < width; i++) {
                            IntermediateBuffer[i][k + 1] = IntermediateBuffer[i][k];
                        }
                    }
                    j += 1;
                }
            }
        }
        if (piece == null) SpawnNewPiece();
        boolean[][] matrix;
        matrix = piece.getCurrentPosition();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if (matrix[i][j]) IntermediateBuffer[piecex + i][piecey + j] = Contents.BLUE;
            }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                DesiredBuffer[i][j] = IntermediateBuffer[i][j];
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (CurrentBuffer[i][j] != DesiredBuffer[i][j]) {
                    Rectangle rectangle = (Rectangle) getNodeFromGridPane(this, i, j);
                    int finalI = i;
                    int finalJ = j;
                    Platform.runLater(new Syncer(rectangle, finalI, finalJ, this, fpgrid.npiece));
                }
            }
        }
    }

    private void SpawnNewPiece() {
        int npiece = fpgrid.npiece;
        if (npiece == 0) piece = new P1();
        if (npiece == 1) piece = new P2();
        if (npiece == 2) piece = new P3();
        if (npiece == 3) piece = new P4();
        if (npiece == 4) piece = new P5();
        if (npiece == 5) piece = new P6();
        if (npiece == 6) piece = new P7();
        fpgrid.NextPiece();
        piecex = 5;
        piecey = 0;
        boolean[][] matrix = new boolean[4][4];
        int piecex_new = piecex;
        int piecey_new = piecey;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if ((piecex_new + i >= width) || (piecex_new + i < 0)) {
                    matrix[i][j] = true;
                    continue;
                }
                if ((piecey_new + j >= height) || (piecey_new + j < 0)) {
                    matrix[i][j] = true;
                    continue;
                }
                matrix[i][j] = (DesiredBuffer[piecex_new + i][piecey_new + j] == Contents.STATIC);
            }
        if (!piece.checkThisPosition(matrix)) {Die(); scene.Lose();}
    }
    public void Die() {
        queueProcessor.interrupt();
        simulator.interrupt();
        difficultyThread.interrupt();
    }
    public enum Movement {
        LEFT,
        RIGHT,
        DOWN,
        ROTATE,
        FUN
    }
    enum Contents {
        EMPTY, BLUE, STATIC
    }
    private class Syncer implements Runnable {
        final Integer x;
        final Integer y;
        Rectangle rectangle;
        QuadrapasselGrid grid;
        int n_piece;
        public Syncer(Rectangle rectangle,  Integer x,  Integer y, QuadrapasselGrid grid, int n_piece) {
            this.rectangle = rectangle;
            this.x = x;
            this.y = y;
            this.grid = grid;
            this.n_piece = n_piece;
        }
        @Override
        public void run() {
            rectangle.setFill((DesiredBuffer[x][y] == Contents.STATIC) ? Color.gray(0.4) : Color.gray(0.6));
            if(DesiredBuffer[x][y]==Contents.BLUE){
                if(n_piece==0||n_piece==3)rectangle.setFill(Color.AQUAMARINE);
                if(n_piece==1||n_piece==4)rectangle.setFill(Color.PINK);
                if(n_piece==2||n_piece==5)rectangle.setFill(Color.LEMONCHIFFON);
                if(n_piece==6)rectangle.setFill(Color.TOMATO);
            }
            setNodeFromGridPane(grid, x, y, rectangle);
            CurrentBuffer[x][y] = DesiredBuffer[x][y];
        }
    }
    private class Difficulty implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(8000);
                } catch (InterruptedException e) {
                    return;
                }
                if (delay > 200) delay -= 10;
                else if(chance<100)chance+=5;
                else if (delay > 0) delay -= 10;
            }
        }
    }

    private class Simulator implements Runnable {

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

    private class QueueProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (!queue.isEmpty()) {
                    processQueue();
                    continue;
                }
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
}
