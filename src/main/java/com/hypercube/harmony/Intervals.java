package com.hypercube.harmony;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Intervals {
	static Logger logger = Logger.getLogger(Intervals.class.getName());
	
	private static String[] notesNames1 = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	private static String[] notesNames2 = new String[] { "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };
	private static String[] notesNames3 = new String[] { "Dbb", "Eb", "Ebb", "Fbb", "Fb", "Gbb", "Gb", "Abb", "Ab", "Bbb", "Cbb", "Cb" };
	private static String[] notesNames4 = new String[] { "B#", "B##", "C##", "D#", "D##", "E#", "E##", "F##", "G#", "G##", "A#", "A##" };

	public static String getSharpNote(int note)
	{
		return notesNames1[note%12];
	}
	public static String getFlatNote(int note)
	{
		return notesNames2[note%12];
	}
	public static String getDoubleFlatNote(int note) {
		return notesNames3[note%12];
	}
	public static String getDoubleSharpNote(int note) {
		return notesNames4[note%12];
	}
	// https://en.wikipedia.org/wiki/Interval_(music)
	static private String[] intervalNames = new String[] { 
			"Unison", 
			"Minor Second", 
			"Major Second", 
			"Minor Third",
			"Major Third", 
			"Perfect Fourth", 
			"Diminished Fifth (or Augmented fourth)", 
			"Perfect Fifth", 
			"Minor Sixth", 
			"Major Sixth", 
			"Minor Seventh",
			"Major Seventh", 
			"Perfect Octave" ,			
			"Minor Ninth",
			"Major Ninth",
			"Minor tenth",
			"Major tenth",
			"Perfect eleventh",
			"Diminished twelfth (or Augmented eleventh)",
			"Tritave",
			"Minor thirteenth",
			"Major Thirteenth",
			"Minor fourteenth",
			"Major Fourteenth",
			"Perfect fifteenth (or Double Octave)"
			};
	static private String[] intervalCodes = new String[] { 
			"P1", 
			"m2", 
			"M2", 
			"m3", 
			"M3", 
			"P4", 
			"Tritone", 
			"P5", 
			"m6",
			"M6", 
			"m7", 
			"M7", 
			"P8",
			"m9",
			"M9",
			"m10",
			"M10",
			"P11",
			"d12",
			"P12",
			"m13",
			"M13",
			"m14",
			"M14",
			"P15"
			};
	public static void printIntervals() throws Exception
	{		
		for (int i=0;i<intervalCodes.length;i++)
		{
			logger.fine(i+" "+intervalCodes[i]+" "+intervalNames[i]);
		}
		if (intervalCodes.length!=intervalNames.length)
			throw new Error("Bad arrays");
	}
	public static int getInterval(String name) {
		for (int i=0;i<intervalCodes.length;i++)
		{
			if (intervalCodes[i].equals(name))
				return i;
		}
		return -1;
	}
	public static String[] getIntervalNames() {
		return intervalNames;
	}

	public static String getInterval(int idx) {
		if (idx < intervalCodes.length)
			return intervalCodes[idx];
		else
			return "I"+idx;
	}

	public static boolean isMajor(int interval) {
		return getInterval(interval).startsWith("M");
	}

	public static boolean isMinor(int interval) {
		return getInterval(interval).startsWith("m");
	}

	public static boolean isDiminished(int interval1, int interval2) {
		return getInterval(interval1).equals("m3")
				&& getInterval(interval2).equals("m3");
	}

	public static boolean isAugmented(int interval1, int interval2) {
		return getInterval(interval1).equals("M3")
				&& getInterval(interval2).equals("M3");
	}

	public static String getChordType(ChordFormula cf) {
		String[] intervals = cf.getIntervals().stream().map(e -> intervalCodes[e % intervalCodes.length])
				.collect(Collectors.toList()).toArray(new String[0]);
		//https://www.scales-chords.com/chord/piano/C#tweak_this_chord
		int size = intervals.length;
		String type = "";
		if (size == 7) { // COUMPOUNT CHORD
			if (intervals[1].equals("m3") && intervals[2].equals("P5") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11") && intervals[6].equals("M13"))
				type = "min13";// minor
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("M7") && intervals[4].equals("M9") && intervals[5].equals("P11") && intervals[6].equals("M13"))
				type = "maj13";// major
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11")&& intervals[6].equals("M13"))
				type = "dom13";
			else if (intervals[1].equals("M3") && intervals[2].equals("Tritone") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11")&& intervals[6].equals("M13"))
				type = "dom13(b5)";
			else if (intervals[1].equals("M3") && intervals[2].equals("m6") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11")&& intervals[6].equals("M13"))
				type = "dom13(#5)";			
			else
				type = "?";
		} 
		else if (size == 6) { // COUMPOUNT CHORD
			if (intervals[1].equals("m3") && intervals[2].equals("P5") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11"))
				type = "min11";// minor
			else if (intervals[1].equals("m3") && intervals[2].equals("Tritone") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11"))
				type = "min11(b5)"; // pas sur
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("M7") && intervals[4].equals("M9") && intervals[5].equals("P11"))
				type = "maj11";// major
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11"))
				type = "dom11";
			else if (intervals[1].equals("M3") && intervals[2].equals("Tritone") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11"))
				type = "dom11(b5)";
			else if (intervals[1].equals("M3") && intervals[2].equals("m6") && intervals[3].equals("m7") && intervals[4].equals("M9") && intervals[5].equals("P11"))
				type = "dom11(#5)";
			else
				type = "?";
		} else if (size == 5) { // COUMPOUNT CHORD
			if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("m7") && intervals[4].equals("M9"))
				type = "dom9";
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("M7") && intervals[4].equals("M9"))
				type = "maj9"; 
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("M7") && intervals[4].equals("m10"))
				type = "maj7(#9)"; 
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("M7") && intervals[4].equals("m9"))
				type = "maj7(b9)";
			else if (intervals[1].equals("m3") && intervals[2].equals("P5") && intervals[3].equals("m7") && intervals[4].equals("M9"))
				type = "m9"; // minor
			else if (intervals[1].equals("M2") && intervals[2].equals("M3") && intervals[3].equals("P5") && intervals[4].equals("m7"))
				type = "9#7";
			else if (intervals[1].equals("m3") && intervals[2].equals("M3") && intervals[3].equals("P5") && intervals[4].equals("M7"))
				type = "maj7(#9)";
			else if (intervals[1].equals("m2") && intervals[2].equals("M3") && intervals[3].equals("P5") && intervals[4].equals("M7"))
				type = "7(b9)";
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("m7") && intervals[4].equals("m9"))
				type = "dom7(b9)";
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("m7") && intervals[4].equals("m10"))
				type = "dom7(#9)";
			else if (intervals[1].equals("M3") && intervals[2].equals("Tritone") && intervals[3].equals("m7") && intervals[4].equals("M9"))
				type = "dom9(b5)";
			else if (intervals[1].equals("M3") && intervals[2].equals("Tritone") && intervals[3].equals("m7") && intervals[4].equals("m9"))
				type = "dom7(b5b9)";
			else if (intervals[1].equals("M3") && intervals[2].equals("m6") && intervals[3].equals("m7") && intervals[4].equals("M9"))
				type = "dom9(#5)";
			else if (intervals[1].equals("M3") && intervals[2].equals("m6") && intervals[3].equals("m7") && intervals[4].equals("m9"))
				type = "dom9(#5b9)";
			else if (intervals[1].equals("M3") && intervals[2].equals("m6") && intervals[3].equals("m7") && intervals[4].equals("m10"))
				type = "dom7(#5#9)";
			else if (intervals[1].equals("m3") && intervals[2].equals("P5") && intervals[3].equals("M7") && intervals[4].equals("M9"))
				type = "m(maj9)";
			else if (intervals[1].equals("m3") && intervals[2].equals("Tritone") && intervals[3].equals("M7") && intervals[4].equals("M9"))
				type = "dim(maj9)";
			else if (intervals[1].equals("m3") && intervals[2].equals("m6") && intervals[3].equals("M7") && intervals[4].equals("M9"))
				type = "m(maj9)#5";
			else if (intervals[1].equals("m3") && intervals[2].equals("m6") && intervals[3].equals("M7") && intervals[4].equals("m9"))
				type = "m7(b9)add(7)";
			else if (intervals[1].equals("m3") && intervals[2].equals("m6") && intervals[3].equals("m7") && intervals[4].equals("M9"))
				type = "m9#5";			
			else 
				type = "?";
		} else if (size == 4) {
			if (intervals[1].equals("m3") && intervals[2].equals("P5") && intervals[3].equals("m7"))
				type = "m7";
			else if (intervals[1].equals("m3") && intervals[2].equals("P5") && intervals[3].equals("M7"))
				type = "m+7";
			else if (intervals[1].equals("P4") && intervals[2].equals("P5") && intervals[3].equals("m7"))
				type = "7sus4";
			else if (intervals[1].equals("M2") && intervals[2].equals("P5") && intervals[3].equals("m7"))
				type = "7sus2";
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("M7"))
				type = "maj7";
			else if (intervals[1].equals("M3") && intervals[2].equals("m6") && intervals[3].equals("M7"))
				type = "maj7#5";
			else if (intervals[1].equals("P4") && intervals[2].equals("P5") && intervals[3].equals("M7"))
				type = "maj7sus4";
			else if (intervals[1].equals("M2") && intervals[2].equals("P5") && intervals[3].equals("M7"))
				type = "maj7sus2";
			else if (intervals[1].equals("M3") && intervals[2].equals("P5") && intervals[3].equals("m7"))
				type = "dom7";
			else if (intervals[1].equals("m3") && intervals[2].equals("Tritone") && intervals[3].equals("M6"))
				type = "dim7";
			else if (intervals[1].equals("M3") && intervals[2].equals("Tritone") && intervals[3].equals("m7"))
				type = "7b5";
			else if (intervals[1].equals("M3") && intervals[2].equals("m6") && intervals[3].equals("m7"))
				type = "7#5";
			else if (intervals[1].equals("m3") && intervals[2].equals("Tritone") && intervals[3].equals("m7"))
				type = "m7b5";
			else if (intervals[1].equals("m3") && intervals[2].equals("m6") && intervals[3].equals("m7"))
				type = "m7#5";
			else if (intervals[1].equals("M3") && intervals[2].equals("Tritone") && intervals[3].equals("M7"))
				type = "maj7b5";
			else if (intervals[1].equals("m3") && intervals[2].equals("m6") && intervals[3].equals("M6"))
				type = "m6#5";
			else if (intervals[1].equals("M3") && intervals[2].equals("m6") && intervals[3].equals("M6"))
				type = "6#5";
			else if (intervals[1].equals("Tritone") && intervals[2].equals("P5") && intervals[3].equals("M7"))
				type = " Lydian Maj7";
			else if (intervals[1].equals("Tritone") && intervals[2].equals("P5") && intervals[3].equals("m7"))
				type = " Lydian b7";
			else if (intervals[1].equals("m2") && intervals[2].equals("P5") && intervals[3].equals("M7"))
				type = " Phrygian Maj7";
			else if (intervals[1].equals("m2") && intervals[2].equals("P5") && intervals[3].equals("m7"))
				type = " Phrygian b7";
			else if (intervals[1].equals("P4") && intervals[2].equals("Tritone") && intervals[3].equals("M7"))
				type = " Locrian Maj7";
			else if (intervals[1].equals("P4") && intervals[2].equals("Tritone") && intervals[3].equals("m7"))
				type = " Locrian b7";
			else
				type = "?";
		} else if (size == 3) {
			if (intervals[1].equals("M2") && intervals[2].equals("P5"))
				type = "sus2";
			else if (intervals[1].equals("P4") && intervals[2].equals("P5"))
				type = "sus4";
			else if (intervals[1].equals("Tritone") && intervals[2].equals("P5"))
				type = " Lydian";
			else if (intervals[1].equals("m2") && intervals[2].equals("P5"))
				type = " Phrygian";
			else if (intervals[1].equals("P4") && intervals[2].equals("Tritone"))
				type = " Locrian";
			else if (intervals[1].equals("M3") && intervals[2].equals("P5"))
				type = ""; // major
			else if (intervals[1].equals("m3") && intervals[2].equals("P5"))
				type = "m"; // minor
			else if (intervals[1].equals("m3") && intervals[2].equals("Tritone"))
				type = "°"; // diminished, both fifth and second are diminished
			else if (intervals[1].equals("M3") && intervals[2].equals("Tritone"))
				type += "(b5)"; // flat fifth only, the second is major
			else if (intervals[1].equals("M3") && intervals[2].equals("m6"))
				type = "+"; // or (#5)
			else if (intervals[1].equals("m3") && intervals[2].equals("m6"))
				type = "?";
			else if (intervals[1].equals("P4") && intervals[2].equals("m6"))
				type = "?";
			else if (intervals[1].equals("P4") && intervals[2].equals("M6"))
				type = "?";
			else if (intervals[1].equals("M3") && intervals[2].equals("M6"))
				type = "6";
			else if (intervals[1].equals("m3") && intervals[2].equals("M6"))
				type = "m6";
			else if (intervals[1].equals("P4") && intervals[2].equals("Tritone"))
				type = "sus4(b5)";			
			else
				type = "?";
				
		} else if (size == 2)
		{
			type = intervals[1];
		}
		return type;
	}
	
}
