package com.carrotsearch.tests;

import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix1D;

@SuppressWarnings("deprecation")
public class TestMahoutCalls
{
    public static void main(String [] args)
    {
        org.apache.mahout.math.AbstractMatrix.decodeMatrix("");
        new DenseDoubleMatrix1D(10);
    }
}
