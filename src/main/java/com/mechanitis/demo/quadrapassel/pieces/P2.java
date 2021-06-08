package com.mechanitis.demo.quadrapassel.pieces;

public class P2 extends Piece {
    public P2() {
        npositions = 2;
        num_fig=1;
        positions = new Position[npositions];
        for(int i=0;i<npositions;i++)
            positions[i]=new Position();
        current_position = 0;
        positions[0].matrix[1][0]=true;
        positions[0].matrix[1][1]=true;
        positions[0].matrix[1][2]=true;
        positions[0].matrix[1][3]=true;

        positions[1].matrix[0][1]=true;
        positions[1].matrix[1][1]=true;
        positions[1].matrix[2][1]=true;
        positions[1].matrix[3][1]=true;
    }
}