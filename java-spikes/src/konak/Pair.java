
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package konak;

/**
 * An immutable pair of objects.
 */
public class Pair<I, J>
{
    public I first;
    public J second;

    public Pair(I clazz, J parameter)
    {
        this.first = clazz;
        this.second = parameter;
    }
    
    public Pair()
    {
    }
}
