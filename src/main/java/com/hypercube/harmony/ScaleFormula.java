package com.hypercube.harmony;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Contains a set of intervals calculated from a string like "T-T-S-T-T-T-S" or "W-H-WH-H-H-WH-H"
 * 
 * @author hypercube software
 *
 */
public class ScaleFormula {
	static private String[] degreeNames = new String[] {"I","II","III","IV","V","VI","VII"};
	private List<Integer> offsets;
	private List<Integer> doubledOffsets;
	private String formula;
	public List<Integer> getOffets() {
		return offsets;
	}
	public List<Integer> getDoubledOffets() {
		return doubledOffsets;
	}
	
	public String getFormula() {
		return formula;
	}

	public ScaleFormula(List<Integer> intervals) {
		super();
		this.offsets = intervals;
	}

	public ScaleFormula(Integer[] intervals) {
		super();
		this.offsets =Arrays.asList(intervals);
	}

	public String getIntervalName(int start,int end)	
	{
		int interval = end-start;
		//logger.fine("interval "+start+"to "+end+" = "+interval+" "+Intervals.getInterval(interval)+" "+Intervals.getIntervalNames()[interval]);
		return Intervals.getInterval(interval);
	}
	public String getDegreeName(int index)
	{
		if (doubledOffsets==null)
			return "";
		if (index>=offsets.size()-1)
			return "";
		//https://music.stackexchange.com/a/39313
		int chordNote1 = doubledOffsets.get(index);
		int chordNote2 = doubledOffsets.get(index+2);
		int chordNote3 = doubledOffsets.get(index+4);
		int interval1 = chordNote2-chordNote1;
		int interval2 = chordNote3-chordNote2;
		
		boolean isMajor = Intervals.isMajor(interval1);
		boolean isMinor = Intervals.isMinor(interval1);
		boolean isDiminished = Intervals.isDiminished(interval1,interval2);
		boolean isAugmented = Intervals.isAugmented(interval1,interval2);
		
		String degree = ""; // some chords are neither major, minor or anything (it happen in weird scales)
				
		if (isDiminished) // diminished chord m3 m3
			degree =  degreeNames[index].toLowerCase()+"°";
		else if (isAugmented) // augmented chord M3 M3
			degree =  degreeNames[index].toLowerCase()+"+";
		else if (isMajor)
			degree = degreeNames[index];
		else if (isMinor)
			degree = degreeNames[index].toLowerCase();
		
		return degree;
		
	}
	public ScaleFormula(String formula) throws Exception {
		this.formula = formula;
		String[] values = formula.split("-");
		offsets = new ArrayList<Integer>();
		int o = 0;
		offsets.add(o);
		for (String v : values)
		{
			int step = 0;
			if (v.equals("T") || v.equals("W")) // Tone or Whole step
				step = 2;
			else if (v.equals("H") || v.equals("S")) // Semi Tone or Half step
				step = 1;
			else if (v.equals("WH")|| v.equals("TS")) // Whole Half step or Tone and semi tone step
				step = 3;
			else
				throw new Exception("Illegal symbol in scale formula:"+v+" at step "+offsets.size()+" in formula "+formula);
			o += step;
			if (o>12)
				throw new Exception("Scale formula cannot exceed one octave (illegal offset: "+o+"):"+v+" at step "+offsets.size()+" in formula "+formula);
			offsets.add(o);
		}
		// expand the offset to the next octaves (simplify further calculations)
		doubledOffsets = new ArrayList<Integer>();
		int max = offsets.size();
		for (int i=0;i<max;i++)
		{
			doubledOffsets.add(offsets.get(i));
		}
		for (int i=0;i<max;i++)
		{
			doubledOffsets.add(offsets.get(i)+12);
		}
		for (int i=0;i<max;i++)
		{
			doubledOffsets.add(offsets.get(i)+24);
		}
	}
}
