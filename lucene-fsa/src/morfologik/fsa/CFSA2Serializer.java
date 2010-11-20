package morfologik.fsa;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static morfologik.fsa.CFSA2.*;

/**
 * 
 */
public final class CFSA2Serializer implements FSASerializer {
	/**
	 * Maximum number of bytes for per-node data.
	 */
	private final static int MAX_NODE_DATA_SIZE = /* TODO: calculate exactly. */ 1024 * 20;

	/**
	 * @see FSA5#filler
	 */
	public byte fillerByte = FSASerializer.DEFAULT_FILLER;

	/**
	 * @see FSA5#annotation
	 */
	public byte annotationByte = FSASerializer.DEFAULT_ANNOTATION;

	/**
	 * <code>true</code> if we should serialize with numbers.
	 * 
	 * @see #withNumbers()
	 */
	private boolean withNumbers;

	/**
	 * Expand nodes that have an arc count larger than this value. Speeds up
	 * lookups.
	 */
	private int expandNodesFromArcCount = 256;

	/**
	 * Serialize the automaton with the number of right-language sequences in
	 * each node. This is required to implement perfect hashing. The numbering
	 * also preserves the order of input sequences.
	 * 
	 * @return Returns the same object for easier call chaining.
	 */
	public CFSA2Serializer withNumbers() {
		withNumbers = true;
	    return this;
    }
	
	public CFSA2Serializer withNodesExpandedIfLargerThan(int arcs) {
        expandNodesFromArcCount = arcs;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CFSA2Serializer withFiller(byte filler) {
        this.fillerByte = filler;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CFSA2Serializer withAnnotationSeparator(byte annotationSeparator) {
        this.annotationByte = annotationSeparator;
        return this;
    }

	/**
	 * 
	 */
	public <T extends OutputStream> T serialize(State s, T os) throws IOException
	{
		final ArrayList<State> linearized = new ArrayList<State>();
		final StateInterningPool pool = new StateInterningPool(
		    StateInterningPool.MINIMUM_BLOCK_SIZE);

		// Add the "sink node", with a single arc pointing to itself.
		State sink = pool.createState();
		sink.addArc((byte) 0xff, sink, false);

		linearized.add(sink); // Sink is not part of the automaton.

		// Add a special, initial "meta state".
		State meta = pool.createState();
        meta.addArc((byte) '^', s, false);
		s = meta.intern(pool);

        // Prepare space for arc offsets and linearize all the states.
        s.preOrder(new Visitor<State>() {
            public void accept(State s) {
                s.offset = 0;
                linearized.add(s);
            }
        });

		/*
		 * Calculate the number of bytes required for the node data,
		 * if serializing with numbers.
		 */
		int nodeDataLength = 0;
		if (withNumbers) {
			int maxNumber = s.number;
			while (maxNumber > 0) {
				nodeDataLength++;
				maxNumber >>>= 8;
			}
		}

		// Calculate minimal goto length.
		int gtl = 1;
		while (true) {
			// First pass: calculate offsets of states.
			if (!emitArcs(null, linearized, gtl, nodeDataLength)) {
				gtl++;
				continue;
			}

			// Second pass: check if goto overflows anywhere.
			if (emitArcs(null, linearized, gtl, nodeDataLength))
				break;

			gtl++;
		}

		/*
		 * Emit the header.
		 */
		os.write(new byte[] { '\\', 'f', 's', 'a' });
		os.write(CFSA2.VERSION);
		os.write(fillerByte);
		os.write(annotationByte);
		os.write((nodeDataLength << 4) | gtl);

		/*
		 * Emit the automaton.
		 */
		boolean gtlUnchanged = emitArcs(os, linearized, gtl, nodeDataLength);
		assert gtlUnchanged : "gtl changed in the final pass.";

		return os;
	}

    /**
	 * Update arc offsets assuming the given goto length.
	 */
	private boolean emitArcs(OutputStream os, 
							 ArrayList<State> linearized,
							 int gtl, 
							 int nodeDataLength)
        throws IOException
    {
		final ByteBuffer bb = ByteBuffer.allocate(MAX_NODE_DATA_SIZE);

		int offset = 0;
		int maxStates = linearized.size();
		for (int j = 0; j < maxStates; j++) {
			final State s = linearized.get(j);

			if (os == null) {
				s.offset = offset;
			} else {
				assert s.offset == offset;
			}

			// Check if this node is going to be expanded.
			final boolean expandedNode = s.arcsCount() > 0 &&
			                             s.arcsCount() >= this.expandNodesFromArcCount;

			// Emit node header.
		    bb.put(expandedNode ? NODE_EXPANDED : NODE_SEQUENTIAL);

			// Emit sequential node's data.
			if (!expandedNode) {
	            // Emit number
	            emitNumber(nodeDataLength, bb, s.number);

	            // Emit arcs sequentially.
	            final int lastTransition = s.arcsCount() - 1;
	            for (int i = 0; i <= lastTransition; i++) {
	                final State target = s.arcState(i);

	                int targetOffset = isTerminal(target) ? targetOffset = 0 
	                                                      : target.offset;

	                int flags = 0;
	                if (s.arcFinal(i)) {
	                    flags |= FSA5.BIT_FINAL_ARC;
	                }

	                if (i == lastTransition) {
	                    flags |= FSA5.BIT_LAST_ARC;

	                    if (j + 1 < maxStates && 
	                            target == linearized.get(j + 1) && 
	                            targetOffset != 0) {
	                        flags |= FSA5.BIT_TARGET_NEXT;
	                        targetOffset = 0;
	                    }
	                }

	                final byte label = s.arcLabel(i);

                    flags |= (targetOffset << 4);
                    bb.put((byte) flags);
                    bb.put(label);

                    if ((flags & FSA5.BIT_TARGET_NEXT) == 0) {
                        // Remaining bytes of the goto field.
                        flags >>>= 8;
                        for (int b = 1; b < gtl; b++) {
                            bb.put((byte) flags);
                            flags >>>= 8;
                        }
    
                        if (flags != 0) {
                            // gtl is too small. interrupt eagerly.
                            return false;
                        }
                    }
	            }
			}

			// Emit expanded node's data.
			else if (expandedNode) {
			    // Emit lo-hi arc range.
                final int lastTransition = s.arcsCount() - 1;
                int low, high;
                int [] used = new int [256];
                Arrays.fill(used, -1);
                low = high = s.arcLabel(0) & 0xff;
                used[low] = 0;
                for (int i = 1; i <= lastTransition; i++) {
                    final int label = s.arcLabel(i) & 0xff;
                    assert label >= (s.arcLabel(i - 1) & 0xff) : "Expected ordered arcs.";
                    low = Math.min(low, label);
                    high = Math.max(high, label);
                    used[label] = i;
                }

                bb.put((byte) low);
                bb.put((byte) high);

	            // Emit this node's number.
	            emitNumber(nodeDataLength, bb, s.number);

	            // Emit all arcs within the range.
	            int accumulated = 0;
	            for (int i = low; i <= high; i++) {
	                if (used[i] >= 0) {
	                    final State target = s.arcState(used[i]);
	                    assert target != null : "Expected a non-null state";

	                    int targetOffset = isTerminal(target) ? targetOffset = 0 
	                                                          : target.offset;

	                    int flags = BIT_EXPANDED_ARC;
	                    if (s.arcFinal(used[i])) {
	                        flags |= BIT_FINAL_ARC;
	                    }

	                    if (i == high) {
	                        flags |= BIT_LAST_ARC;
	                    }
	                    
	                    final byte label = (byte) i;

	                    assert targetOffset < (-1 >>> 5) : "GOTO overflow.";
	                    flags |= (targetOffset << 5);
	                    bb.put((byte) flags);
	                    bb.put(label);

                        // Remaining bytes of the goto field.
                        flags >>>= 8;
                        for (int b = 1; b < gtl; b++) {
                            bb.put((byte) flags);
                            flags >>>= 8;
                        }
    
                        if (flags != 0) {
                            // gtl is too small. interrupt eagerly.
                            return false;
                        }

                        // Accumulated number.
                        emitNumber(nodeDataLength, bb, accumulated);

                        if (s.arcFinal(used[i]))
                            accumulated += 1;
                        if (!isTerminal(target))
                            accumulated += target.number;
	                } else {
	                    // Emit a fake arc.
	                    bb.put((byte) (BIT_UNUSED_ARC | BIT_EXPANDED_ARC));   // flags
	                    bb.put((byte) 0x00);             // label
	                    for (int b = 0; b < gtl - 1 + nodeDataLength; b++)
	                        bb.put((byte) 0);            // GTL + number.
	                }
	            }
			}

			// Flush the node's representation
            bb.flip();
            offset += bb.remaining();
            if (os != null) {
                os.write(bb.array(), bb.position(), bb.remaining());
            }
            bb.clear();
		}

		return true;
	}

    private void emitNumber(int nodeDataLength, final ByteBuffer bb, int number) 
    {
        if (nodeDataLength > 0) {
            for (int i = 0; i < nodeDataLength; i++) {
                bb.put((byte) number);
                number >>>= 8;
            }
        }
    }

	/**
	 * A terminal state does not have any outgoing transitions.
	 */
	private static boolean isTerminal(State state) {
		return !state.hasChildren();
	}
}
