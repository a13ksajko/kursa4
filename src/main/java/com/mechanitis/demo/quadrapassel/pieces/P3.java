package com.mechanitis.demo.quadrapassel.pieces;

public class P3 extends Piece{
    public P3() {
        npositions = 4;
        positions = new Position[npositions];
        current_position = 0;
        positions[0].matrix[0][1]=true;
        positions[0].matrix[1][0]=true;
        positions[0].matrix[1][1]=true;
        positions[0].matrix[1][2]=true;

        positions[1].matrix[0][1]=true;
        positions[1].matrix[1][1]=true;
        positions[1].matrix[1][2]=true;
        positions[1].matrix[2][1]=true;

        positions[2].matrix[2][1]=true;
        positions[2].matrix[1][0]=true;
        positions[2].matrix[1][1]=true;
        positions[2].matrix[1][2]=true;

        positions[3].matrix[0][1]=true;
        positions[3].matrix[1][0]=true;
        positions[3].matrix[1][1]=true;
        positions[3].matrix[2][1]=true;

    }
}
