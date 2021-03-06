package morfologik.fsa;

import static morfologik.fsa.FSAFlags.NEXTBIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * Additional tests for {@link FSA5}.
 */
public final class FSA5Test {
	public ArrayList<String> expected = new ArrayList<String>(Arrays.asList(
	        "a", "aba", "ac", "b", "ba", "c"));

	@Test
	public void testVersion5() throws IOException {
		final FSA fsa = FSA.read(this.getClass().getResourceAsStream("abc.fsa"));
		assertFalse(fsa.getFlags().contains(FSAFlags.NUMBERS));
		verifyContent(expected, fsa);
	}

	@Test
	public void testVersion5WithNumbers() throws IOException {
		final FSA fsa = FSA.read(this.getClass().getResourceAsStream("abc-numbers.fsa"));

		verifyContent(expected, fsa);
		assertTrue(fsa.getFlags().contains(FSAFlags.NUMBERS));
	}

	@Test
	public void testArcsAndNodes() throws IOException {
		final FSA fsa1 = FSA.read(this.getClass().getResourceAsStream(
		        "abc.fsa"));
		final FSA fsa2 = FSA.read(this.getClass().getResourceAsStream(
		        "abc-numbers.fsa"));

		FSAInfo info1 = new FSAInfo(fsa1);
		FSAInfo info2 = new FSAInfo(fsa2);

		assertEquals(info1.arcsCount, info2.arcsCount);
		assertEquals(info1.nodeCount, info2.nodeCount);

		assertEquals(4, info2.nodeCount);
		assertEquals(7, info2.arcsCount);
	}
	
	@Test
	public void testArcsAndNodesLarge() throws IOException {
		final FSA fsa3 = FSA.read(Thread.currentThread().getContextClassLoader()
		    .getResourceAsStream("morfologik/dictionaries/pl.dict"));

		FSAInfo info3 = new FSAInfo(fsa3);

		assertEquals(299903, info3.nodeCount);
		assertEquals(687834, info3.arcsCount);
		assertEquals(3565575, info3.finalStatesCount);
	}

	@Test
	public void testNumbers() throws IOException {
		final FSA5 fsa = FSA.read(this.getClass().getResourceAsStream("abc-numbers.fsa"));

		assertTrue(fsa.getFlags().contains(NEXTBIT));

		// Get all numbers for nodes.
		byte[] buffer = new byte[128];
		final ArrayList<String> result = new ArrayList<String>();
		walkNode(buffer, 0, fsa, fsa.getRootNode(), 0, result);

		Collections.sort(result);
		assertEquals(Arrays
		        .asList("0 c", "1 b", "2 ba", "3 a", "4 ac", "5 aba"), result);
	}

	public static void walkNode(byte[] buffer, int depth, FSA fsa, int node,
	        int cnt, List<String> result) throws IOException {
		for (int arc = fsa.getFirstArc(node); arc != 0; arc = fsa.getNextArc(arc)) {
			buffer[depth] = fsa.getArcLabel(arc);

			if (fsa.isArcFinal(arc) || fsa.isArcTerminal(arc)) {
				result.add(cnt + " " + new String(buffer, 0, depth + 1, "UTF-8"));
			}

			if (fsa.isArcFinal(arc))
				cnt++;

			if (!fsa.isArcTerminal(arc)) {
				walkNode(buffer, depth + 1, fsa, fsa.getEndNode(arc), cnt, result);
				cnt += fsa.getNumberAtNode(fsa.getEndNode(arc));
			}
		}
	}

	private static void verifyContent(List<String> expected, FSA fsa) throws IOException {
		final ArrayList<String> actual = new ArrayList<String>();

		int count = 0;
		for (ByteBuffer bb : fsa.getSequences()) {
			assertEquals(0, bb.arrayOffset());
			assertEquals(0, bb.position());
			actual.add(new String(bb.array(), 0, bb.remaining(), "UTF-8"));
			count++;
		}
		assertEquals(expected.size(), count);
		Collections.sort(actual);
		assertEquals(expected, actual);
	}
}
