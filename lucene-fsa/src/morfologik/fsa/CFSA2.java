package morfologik.fsa;

import static morfologik.fsa.FSAFlags.FLEXIBLE;
import static morfologik.fsa.FSAFlags.NEXTBIT;
import static morfologik.fsa.FSAFlags.NUMBERS;
import static morfologik.fsa.FSAFlags.STOPBIT;
import static morfologik.util.FileUtils.readFully;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * CFSA binary format implementation, version 2.
 */
public final class CFSA2 extends FSA {
	/**
	 * Automaton version as in the file header.
	 */
    public static final byte VERSION = (byte) 0xC6;

	/**
	 * Bit indicating that an arc corresponds to the last character of a
	 * sequence available when building the automaton.
	 */
	public static final int BIT_FINAL_ARC = 1 << 0;

	/**
	 * Bit indicating that an arc is the last one of the node's list and the
	 * following one belongs to another node.
	 */
	public static final int BIT_LAST_ARC = 1 << 1;

	/**
	 * Bit indicating that the target node of this arc follows it in the
	 * compressed automaton structure (no goto field).
	 */
	public static final int BIT_TARGET_NEXT = 1 << 2;

    /**
     * Bit indicating that the arc is part of an expanded node.
     */
    public static final int BIT_EXPANDED_ARC = 1 << 3;
    
    /**
     * Unused arc bit for expanded arcs.
     */
    public static final int BIT_UNUSED_ARC = 1 << 4;

    public final static byte NODE_SEQUENTIAL = (byte) 0x5e;
    public final static byte NODE_EXPANDED = (byte) 0xee;
    
	/**
	 * An array of bytes with the internal representation of the automaton.
	 * Please see the documentation of this class for more information on how
	 * this structure is organized.
	 */
	public final byte[] arcs;

	/**
	 * The length of the node header structure (if the automaton was compiled with
	 * <code>NUMBERS</code> option). Otherwise zero.
	 */
	public final int nodeDataLength;

	/**
	 * Flags for this automaton version.
	 */
    private final Set<FSAFlags> flags;

    /**
     * Number of bytes each address takes in full, expanded form (goto length).
     */
	public final int gtl;

	/** Filler character. */
	public final byte filler;
	
	/** Annotation character. */
	public final byte annotation;

	/**
	 * Read and wrap a binary automaton in FSA version 5.
	 */
	public CFSA2(InputStream fsaStream) throws IOException {
		// Read the header first.
		final FSAHeader header = FSAHeader.read(fsaStream);
		
		// Ensure we have version 5.
		if (header.version != VERSION) {
			throw new IOException("This class can read CFSA2 automata only: " + header.version);
		}
		
		/*
		 * Determine if the automaton was compiled with NUMBERS. If so, modify
		 * ctl and goto fields accordingly.
		 */
		flags = EnumSet.of(FLEXIBLE, STOPBIT, NEXTBIT);
		if ((header.gtl & 0xf0) != 0) {
			flags.add(NUMBERS);
		}

		this.nodeDataLength = (header.gtl >>> 4) & 0x0f;
		this.gtl = header.gtl & 0x0f;
		
		this.filler = header.filler;
		this.annotation = header.annotation;

		arcs = readFully(fsaStream);		
	}

	/**
	 * Returns the start node of this automaton.
	 */
	@Override
	public int getRootNode() {
		int epsilon = skipArc(getFirstArc(0));
        return getDestinationNodeOffset(getFirstArc(epsilon));
	}

	/**
     * {@inheritDoc} 
     */
	@Override
	public final int getFirstArc(int node) {
	    if (arcs[node] == NODE_SEQUENTIAL) {
	        return node + 
	               /* node type */ 1 + 
	               nodeDataLength; 
	    } else {
	        return node + 
                   /* node type */ 1 +  
                   /* lowest arc label */ 1 +
                   /* highest arc label */ 1 + 
                   nodeDataLength;
	    }
	}

	/**
     * {@inheritDoc} 
     */
	@Override
	public final int getNextArc(int arc) {
		if (isArcLast(arc))
			return 0;
		else
			return skipArc(arc);
	}

	/**
     * {@inheritDoc} 
     */
	@Override
	public int getArc(int node, byte label) {
	    if (arcs[node] == NODE_SEQUENTIAL) {
    		for (int arc = getFirstArc(node); arc != 0; arc = getNextArc(arc)) {
    			if (getArcLabel(arc) == label)
    				return arc;
    		}

            // An arc labeled with "label" not found.
            return 0;
	    } else {
	        int lbl = label & 0xff;
	        int low = arcs[node + 1] & 0xff;
            if (lbl >= low &&
	            lbl <= (arcs[node + 2] & 0xff)) {
	            int arc = node +
                       /* node type */ 1 +  
                       /* lowest arc label */ 1 +
                       /* highest arc label */ 1 +
                       nodeDataLength +
                       (lbl - low) * (1 + 1 + (gtl - 1) + nodeDataLength);

	            if (isArcUnused(arc))
	                return 0;
	            else
	                return arc;
	        } else {
	            // An arc labeled with "label" not found.
	            return 0;
	        }
	    }
	}

	/**
     * {@inheritDoc} 
     */
	@Override
	public int getEndNode(int arc) {
		final int nodeOffset = getDestinationNodeOffset(arc);
		assert nodeOffset != 0 : "No target node for terminal arcs.";
		return nodeOffset;
	}

	/**
     * {@inheritDoc} 
     */
	@Override
	public byte getArcLabel(int arc) {
	    // Sequential or expanded, no matter what.
        return arcs[arc + 1];
	}
	
	/**
	 * 
	 */
	public boolean isArcSequential(int arc) {
	    return (arcs[arc] & BIT_EXPANDED_ARC) == 0; 
	}

	/**
     * {@inheritDoc} 
     */
	@Override
	public boolean isArcFinal(int arc) {
	    // Sequential or expanded, no matter what.
        return (arcs[arc] & BIT_FINAL_ARC) != 0;
	}

    /** 
     */
    public boolean isArcUnused(int arc) {
        assert !isArcSequential(arc) : "Expected an expanded arc.";
        return (arcs[arc] & BIT_UNUSED_ARC) != 0;
    }

	/**
     * {@inheritDoc} 
     */
	@Override
	public boolean isArcTerminal(int arc) {
		return (0 == getDestinationNodeOffset(arc));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public int getNumberAtNode(int node) {
    	assert getFlags().contains(FSAFlags.NUMBERS) 
    		: "This FSA was not compiled with NUMBERS.";

    	if (arcs[node] == NODE_SEQUENTIAL) {
    	    return decodeFromBytes(arcs, node + 1, nodeDataLength);
    	} else {
    	    return decodeFromBytes(arcs, node + 1 + 2, nodeDataLength);
    	}
    }

	/**
	 * {@inheritDoc}
	 * 
	 * <p>For this automaton version, an additional {@link FSAFlags#NUMBERS} flag
	 * may be set to indicate the automaton contains extra fields for each node.</p>
	 */
	@Override
	public Set<FSAFlags> getFlags() {
	    return Collections.unmodifiableSet(flags);
	}

	/**
	 * Returns <code>true</code> if this arc has <code>LAST</code> bit set.
	 * 
	 * @see #BIT_LAST_ARC
	 */
	public boolean isArcLast(int arc) {
        // Sequential or expanded, no matter what.
        return (arcs[arc] & BIT_LAST_ARC) != 0;
	}

	/**
	 * @see #BIT_TARGET_NEXT
	 */
	public boolean isNextSet(int arc) {
        if (isArcSequential(arc)) {
            return (arcs[arc] & BIT_TARGET_NEXT) != 0;
        } else {
            return false;
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<ByteBuffer> getSequences(int node) {
	    return FSAUtils.getSequences(this, node);
	}

	/**
	 * Returns an n-byte integer encoded in byte-packed representation.
	 */
	static final int decodeFromBytes(
			final byte[] arcs, final int start, final int n)
	{
		int r = 0;
		for (int i = n; --i >= 0;) {
			r = r << 8 | (arcs[start + i] & 0xff);
		}
		return r;
	}

	/**
	 * Returns the address of the node pointed to by this arc.
	 */
	final int getDestinationNodeOffset(int arc) {
		if (isNextSet(arc)) {
			/* The destination node follows this arc in the array. */
			return skipArc(arc);
		} else {
			/*
			 * The destination node address has to be extracted from the arc's goto field.
			 */
            int r = 0;
            for (int i = gtl; --i >= 1;) {
                r = r << 8 | (arcs[arc + 1 + i] & 0xff);
            }
            r = r << 8 | (arcs[arc] & 0xff);

		    if (isArcSequential(arc)) {
                return r >>> 4;
		    } else {
                return r >>> 5;
		    }
		}
	}

	/**
     * Read the arc's layout and skip as many bytes, as needed.
     */
    private int skipArc(int arc) {
        if (isArcSequential(arc)) {
        	return arc + 
        	       (isNextSet(arc) 
        			? 1 + 1   /* flags, label */ 
        			: 1 + gtl /* flags, label, goto address */);
        } else {
            do {
                arc += 1 + 1 + (gtl - 1) /* flags, label, goto address */ +
                       nodeDataLength;
            } while (isArcUnused(arc));

            return arc;
        }
    }

    /**
     * Cumulative sum of numbers from nodes up to the given arc.
     */
    public int getNumbersSumToArc(int node, int arc)
    {
        if (arcs[node] == NODE_EXPANDED) {
            assert !isArcSequential(arc) : "Expected expanded arc.";
            return decodeFromBytes(arcs, arc + 1 + 1 + (gtl - 1), nodeDataLength);                
        } else {
            int cumulative = 0;
            for (int i = getFirstArc(node); i != arc; i = getNextArc(i)) {
                if (isArcFinal(i))
                    cumulative += 1;
                if (!isArcTerminal(i))
                    cumulative += getNumberAtNode(getEndNode(i));
            }
            return cumulative;
        }
    }
}