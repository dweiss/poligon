package com.dawidweiss.spikes.instrumenter;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

/**
 * Test instrumenter.
 */
public class TestInstrumenter implements ClassFileTransformer
{
    // No instantiating me except in premain() or in {@link JarClassTransformer}.
    TestInstrumenter()
    {
    }

    public static void premain(String agentArgs, Instrumentation inst)
    {
        for (int i = 0; i < 0x80; i++) {
            byte [] arr = new byte [i];
            System.out.println("byte[" + i + "] takes " + 
                inst.getObjectSize(arr) + " bytes.");
        }
    }

    public byte [] transform(ClassLoader loader, String className,
        Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
        byte [] classfileBuffer) throws IllegalClassFormatException
    {
        return null;
    }
}
