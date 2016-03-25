package com.clust4j.data;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Test;

import com.clust4j.except.MatrixParseException;
import com.clust4j.utils.MatUtils;


public class BufferedMatrixReaderTests {
	final static String file = new String("tmpbmrtfile.csv");
	final static Path path = FileSystems.getDefault().getPath(file);
	
	static Object[] fromDoubleArr(double[][] d) {
		final Object[] o = new Object[d.length];
		
		int idx = 0;
		String s;
		for(double[] dub: d) {
			s = Arrays.toString(dub);
			s = s.substring(1, s.length() - 1);
			o[idx++] = s;
		}
		
		return o;
	}
	
	static DataSet readCSVComplex(boolean b) throws FileNotFoundException, IOException {
		return new BufferedMatrixReader(new File(file)).read(b);
	}
	
	static DataSet readCSVComplex() throws FileNotFoundException, IOException {
		return new BufferedMatrixReader(new File(file)).read();
	}
	
	static void writeCSV(Object[] in) throws IOException {
		final String sep = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		
		for(int i= 0; i < in.length; i++) {
			sb.append(in[i].toString());
			sb.append(sep);
		}
		
		String out = sb.toString();
		
		// Actually do the writing...
		BufferedWriter bf = new BufferedWriter(new FileWriter(file));
		try {
			bf.write(out);
		} finally {
			bf.close();
		}
	}
	
	@Test
	public void testBuffered1() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{
				"1,2,3,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5},
					new double[]{6,7,8,9,10}
				}, readCSVComplex().getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testBufferedCustomDelim() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{
				"1$2$3$4$5",
				"6$7$8$9$10"
			};
			
			
			writeCSV(o);
			DataSet d = new BufferedMatrixReader(new File(file), (byte)'$').read();
			
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testBufferedSingleQuoted() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{
				"'1','2','3','4','5'",
				"'6','7','8','9','10'"
			};
			
			writeCSV(o);
			DataSet d = new BufferedMatrixReader(new File(file), true).read();
			
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testBufferedDoubleQuoted() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{
				"\"1\",\"2\",\"3\",\"4\",\"5\"",
				"\"6\",\"7\",\"8\",\"9\",\"10\""
			};
			
			writeCSV(o);
			DataSet d = new BufferedMatrixReader(new File(file), false).read();
			
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testBufferedDoubleQuotedUnmatched() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{
				"\"1\",\"2\",\"3\",\"4\",\"5\"",
				"\"6\",\"7\",\"8\",\"9\",10"
			};
			
			writeCSV(o);
			DataSet d = new BufferedMatrixReader(new File(file), false).read();
			
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testTrailingComma1() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,3,4,5,",
				"6,7,8,9,10,"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5,Double.NaN},
					new double[]{6,7,8,9,10,Double.NaN}
				}, d.getDataRef().getDataRef())
			);
			
			assertTrue(MatUtils.containsNaN(d.getDataRef().getDataRef()));
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testInf1() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,inf,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,Double.POSITIVE_INFINITY,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testInf2() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,infinity,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,Double.POSITIVE_INFINITY,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testNegInf1() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,-inf,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,Double.NEGATIVE_INFINITY,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testNegInf2() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,-infinity,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,Double.NEGATIVE_INFINITY,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testNaNEmbedded1() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,nan,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(MatUtils.containsNaN(d.getDataRef().getDataRef()));
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testNaNEmbedded2() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2, ,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(MatUtils.containsNaN(d.getDataRef().getDataRef()));
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testNaNEmbedded3() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(MatUtils.containsNaN(d.getDataRef().getDataRef()));
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testNaNEmbedded4() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,na,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(MatUtils.containsNaN(d.getDataRef().getDataRef()));
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testNaNEmbedded5() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,?,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			DataSet d= readCSVComplex();
			assertTrue(MatUtils.containsNaN(d.getDataRef().getDataRef()));
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	/*
	 * Test that merely the presence of a ? is not enough to trigger NaN
	 */
	@Test(expected=MatrixParseException.class)
	public void testEmbeddedNonNaN() throws IOException {
		try {
			Object[] o = new Object[]{
				"1,2,a?,4,5",
				"6,7,8,9,10"
			};
			
			writeCSV(o);
			readCSVComplex();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testDifferentDelim() throws IOException {
		try {
			Object[] o = new Object[]{
				"1|2|3|4|5",
				"6|7|8|9|10"
			};
			
			writeCSV(o);
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5},
					new double[]{6,7,8,9,10}
				}, readCSVComplex().getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testWithHeader() throws IOException {
		try {
			Object[] o = new Object[]{
				"a|b|c|d|e",
				"1|2|3|4|5",
				"6|7|8|9|10"
			};
			
			writeCSV(o);
			DataSet d = readCSVComplex();
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5},
					new double[]{6,7,8,9,10}
				}, d.getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testLarge() throws IOException {
		try {
			double[][] d = MatUtils.randomGaussian(5000, 25);
			
			Object[] o = fromDoubleArr(d);
			writeCSV(o);
			assertTrue(
				MatUtils.equalsExactly(d, 
					readCSVComplex().getDataRef().getDataRef())
			);
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test(expected=MatrixParseException.class)
	public void testParseException1() throws IOException {
		/*
		 * This one will fail in the setup
		 */
		try {
			Object[] o = new Object[]{
				"1|2|a|4|5",
				"6|7|8|9|10"
			};
			
			writeCSV(o);
			readCSVComplex();
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test(expected=MatrixParseException.class)
	public void testParseException2() throws IOException {
		/*
		 * This one will fail on read()
		 */
		try {
			Object[] o = new Object[]{
				"1|2|3|4|5",
				"6|7|8|9|10",
				"1|2|3|4|5",
				"6|7|8|9|10",
				"1|2|3|4|5",
				"6|7|8|9|10",
				"1|2|3|4|5",
				"6|7|a|9|10",
			};
			
			writeCSV(o);
			readCSVComplex();
			
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testSingleRow() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{ "1,2,3,4,5" };
			
			writeCSV(o);
			BufferedMatrixReader bmr = new BufferedMatrixReader(new File(file));
			DataSet d = bmr.read();
			assertTrue(bmr.setup.header_offset == 0);
			assertTrue(bmr.setup.headers == null);
			
			assertTrue(
				MatUtils.equalsExactly(new double[][]{
					new double[]{1,2,3,4,5}
				}, d.getDataRef().getDataRef())
			);
			
			assertTrue(bmr.hasWarnings());
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test(expected=MatrixParseException.class)
	public void testSingleRowNumericFailure() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{ "1,2,a,4,5" };
			
			writeCSV(o);
			readCSVComplex();
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test(expected=MatrixParseException.class)
	public void testSingleRowHeaderFailure() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{ "a,b,c,d,e" };
			
			writeCSV(o);
			readCSVComplex();
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test(expected=MatrixParseException.class)
	public void testSingleRowNoSep() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{ "abcde" };
			
			writeCSV(o);
			readCSVComplex();
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test(expected=MatrixParseException.class)
	public void testJagged() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{
					"1,2,3,4,5",
					"6,7,8",
					"1,2,3,4,5"
				};
			
			writeCSV(o);
			readCSVComplex();
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test(expected=MatrixParseException.class)
	public void testPrettyPrinterInError() throws IOException {
		// test simple
		try {
			Object[] o = new Object[]{
				"1,2,3,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z"
			};
			
			writeCSV(o);
			readCSVComplex();
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testParallel1() throws IOException {
		try {
			double[][] g = MatUtils.randomGaussian(50, 5);
			Object[] o = fromDoubleArr(g);
			
			writeCSV(o);
			DataSet d = readCSVComplex(true);
			
			assertTrue(MatUtils.equalsExactly(g, d.getDataRef().getDataRef()));
			System.out.println();
		} finally {
			Files.delete(path);
		}
	}
	
	@Test
	public void testParallelBig() throws IOException {
		double[][] g = MatUtils.randomGaussian(5000, 150);
		Object[] o = fromDoubleArr(g);
		writeCSV(o);
		
		try {
			for(boolean parallel: new boolean[]{false, true}) {
					System.out.println((parallel?"Parallel":"Serial")+" parsing task");
					DataSet d = readCSVComplex(parallel);
					
					assertTrue(MatUtils.equalsExactly(g, d.getDataRef().getDataRef()));
					System.out.println();
			}

		} finally {
			Files.delete(path);
		}
		
	}
	
	@Test(expected=MatrixParseException.class)
	public void testParallelBigNFE1() throws IOException {
		double[][] g = MatUtils.randomGaussian(5000, 150);
		Object[] o = fromDoubleArr(g);
		o[15] = new Object[]{"asdf"};
		writeCSV(o);
		
		try {
			readCSVComplex(true);
			System.out.println();

		} finally {
			Files.delete(path);
		}
	}
	
	@Test(expected=MatrixParseException.class)
	public void testParallelBigDME1() throws IOException {
		double[][] g = MatUtils.randomGaussian(5000, 150);
		Object[] o = fromDoubleArr(g);
		o[15] = new Integer(1);
		writeCSV(o);
		
		try {
			readCSVComplex(true);
			System.out.println();

		} finally {
			Files.delete(path);
		}
	}
}