package com.hypercube.harmony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Intervals are from the root note
 * inner intervals are relative to the previous note in the chord
 * 
 * C major: 
 * intervals = 0 4 7 = P1,M3,P5
 * inner intervals = 0,4,3 = P1,M3,m3
 * 
 * 
 * @author hypercube software
 *
 */
public class ChordFormula {
	private List<Integer> intervals = new ArrayList<Integer>();
	private List<Integer> innerIntervals = new ArrayList<Integer>();
	
	public List<Integer> getInnerIntervals() {
		return innerIntervals;
	}
	/**
	 * If we collapse all notes in a single octave, 
	 * We should have no collisions
	 * 
	 * @param offsets
	 * @return
	 */
	public static boolean isValid(Integer[] offsets) {
		HashSet<Integer> notes = new HashSet<Integer>();
		int prevOffset = -1;
		int maxSpread = 0;
		for (Integer i : offsets)
		{	
			if (i<prevOffset)
				return false;
			notes.add(i%12);
			if (prevOffset!=-1)
			{
				maxSpread = Math.max(maxSpread, i-prevOffset);
			}
			prevOffset = i;			
		}
		boolean valid = (notes.size()==offsets.length) && (offsets.length<=2 || maxSpread>1);
		return valid;
	}
	/**
	 * As input we get a list of notes (offsets from the beginning of the the octave)
	 * Internally we compute the intervals that build this chord
	 * 
	 * @param offsets
	 * @throws Exception
	 */
	public ChordFormula(Integer[] offsets) throws Exception {
		super();
		
		if (!isValid(offsets))
		{
			throw new Exception("Illegal formula, duplicate notes");
		}
		
		for (Integer i : offsets)
		{			
			int offset = i-offsets[0];
			if (offset<0)
				throw new Error("invalid offset");
			this.intervals.add(offset);
			
		}

		Collections.sort(intervals);
		Integer prevInterval = 0;
		for (Integer i : intervals)
		{
			innerIntervals.add(i-prevInterval);
			prevInterval = i;
		}
	}

	public List<Integer> getIntervals() {
		return intervals;
	}

	public String toString() {
		String l1 = "";
		String l2 = "";
		for (int i : intervals)
		{
			if (l1.length()>0)
			{
				l1+=",";
				l2+=",";
			}
			l1+=i;
			l2+= Intervals.getInterval(i);
		}
		return l1+" "+l2;
	}
	public int getFifthIndex()
	{
		for (int i=0;i<intervals.size();i++)
		{
			if (intervals.get(i)==7)
				return i;
		}
		return -1;
	}
}
