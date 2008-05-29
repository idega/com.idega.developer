package com.idega.development.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Command line utility to convert resource string files with native characters into escape-based UTF strings
 * Takes 2 parameters - first is the input file name (with full path) which exists locally, second is the name of the result output file.
 */
public class StringConverterUtility {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 2) {
			String inputFile = args[0];
			String outputFile = args[1];
			
			try {
	            String line;
	            BufferedReader in = new BufferedReader(new FileReader(inputFile));
	            FileWriter out = new FileWriter(outputFile);
	            while( (line = in.readLine()) != null) {
	            		
	            	String UTF8Str = new String(line.getBytes(), "UTF-8");
	            	
	            	int index = UTF8Str.indexOf("=");
	            	if(index > 0) {
	            		int temp = UTF8Str.length();
	            		String key = UTF8Str.substring(0, index);
	            		String value = UTF8Str.substring(index + 1, UTF8Str.length());
	            		value = saveConvert(value, false);
	            		
	            		out.write(key);
	            		out.write("=");
	            		out.write(value);
	            		out.write("\n");
	            	}
	            }
	            
	            out.flush();
	            out.close();
			} catch(FileNotFoundException e) {
				System.out.println("Input file not found: " + inputFile);
			} catch(Exception ex) {
				System.out.println("Error converting: " + ex.getMessage());
			}
		}

	}
	
	private static String saveConvert(String theString, boolean escapeSpace) {
		if(theString == null) {theString="";}
		
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len*2);

		for(int x=0; x<len; x++) {
			char aChar = theString.charAt(x);
			switch(aChar) {
		case ' ':
			if (x == 0 || escapeSpace) {
				outBuffer.append('\\');
			}

			outBuffer.append(' ');
			break;
				case '\\':outBuffer.append('\\'); outBuffer.append('\\');
						  break;
				case '\t':outBuffer.append('\\'); outBuffer.append('t');
						  break;
				case '\n':outBuffer.append('\\'); outBuffer.append('n');
						  break;
				case '\r':outBuffer.append('\\'); outBuffer.append('r');
						  break;
				case '\f':outBuffer.append('\\'); outBuffer.append('f');
						  break;
				default:
					if ((aChar < 0x0020) || (aChar > 0x007e)) {
						outBuffer.append('\\');
						outBuffer.append('u');
						outBuffer.append(toHex((aChar >> 12) & 0xF));
						outBuffer.append(toHex((aChar >>  8) & 0xF));
						outBuffer.append(toHex((aChar >>  4) & 0xF));
						outBuffer.append(toHex( aChar        & 0xF));
					} else {
						if (specialSaveChars.indexOf(aChar) != -1) {
							outBuffer.append('\\');
						}
						outBuffer.append(aChar);
					}
			}
		}
		return outBuffer.toString();
	}
	
	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}
	
	private static final char[] hexDigit = {
		'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
	};
	
	private static final String specialSaveChars = "=: \t\r\n\f#!";

}
