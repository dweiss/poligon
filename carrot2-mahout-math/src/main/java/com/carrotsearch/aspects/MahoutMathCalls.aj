package com.carrotsearch.aspects;

/**
 * 
 */
public aspect MahoutMathCalls
{
    Object around():
        !within(com.carrotsearch.aspects..*) &&
        (call(* org.apache.mahout..*(..)) ||
         call(org.apache.mahout..new(..)))
    {
        return proceed();
    }
}