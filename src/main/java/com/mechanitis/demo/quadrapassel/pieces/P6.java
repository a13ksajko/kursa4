package com.mechanitis.demo.quadrapassel.pieces;

public class P6 extends Piece{
    public P6() {
        npositions = 4;
        num_fig=5;
        positions = new Position[npositions];
        for(int i=0;i<npositions;i++)
            positions[i]=new Position();
        current_position = 0;

        positions[0].matrix[0][1]=true;
        positions[0].matrix[0][2]=true;
        positions[0].matrix[1][0]=true;
        positions[0].matrix[1][1]=true;

        positions[1].matrix[0][1]=true;
        positions[1].matrix[1][1]=true;
        positions[1].matrix[1][2]=true;
        positions[1].matrix[2][2]=true;

        positions[2].matrix[1][1]=true;
        positions[2].matrix[1][2]=true;
        positions[2].matrix[2][0]=true;
        positions[2].matrix[2][1]=true;

        positions[3].matrix[0][0]=true;
        positions[3].matrix[1][0]=true;
        positions[3].matrix[1][1]=true;
        positions[3].matrix[2][1]=true;

    }
}
