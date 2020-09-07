package com.hypercube.harmony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
/**
 * A scale is an instance of a ScaleTemplate at a given note
 * The ScaleTemplate provide a formula to apply to the rootNote of the scale
 * 
 * @author hypercube software
 *
 */
public class Scale {
	/**
	 * contains the ScaleTemplate offsets shifted by the root note of the scale
	 */
	private List<Integer> notes = new ArrayList<Integer>();
	private List<String> noteNames = new ArrayList<String>();
	private HashMap<Integer,Integer> noteToDegree = new HashMap<Integer,Integer>();
	private HashMap<Integer,Integer> degreeToNote = new HashMap<Integer,Integer>();
	private int bitmask;
	private String url;
	private String rootName;
	
	public int getBitmask() {
		return bitmask;
	}

	public List<String> getNoteNames() {
		return noteNames;
	}
	public String getRootName() {
		return rootName;
	}
	private ScaleFormula formula;
	public ScaleFormula getFormula() {
		return formula;
	}
	private Integer rootNote;
	private String name;

	@Override
	public String toString() {
		String r = name+" [root "+rootNote+"] [offsets ";
		for (int i = 0; i < notes.size(); i++) {
			if (i > 0)
				r += ",";
			r += notes.get(i);
		}
		r += "] [notes ";
		for (int i = 0; i < noteNames.size(); i++) {
			if (i > 0)
				r += ",";
			r += noteNames.get(i);
		}
		r+="]";
		return r;
	}

	public String getName() {
		return name;
	}

	public Scale(String name)
	{
		this.name = name;
	}
	public Scale(String name, Integer rootNote, ScaleFormula formula) {
		this.rootNote = rootNote;
		this.name = name;
		this.formula = formula;
		for (int degree=0;degree<formula.getOffets().size();degree++)
		{
			int offset = formula.getOffets().get(degree);
			int note = offset+rootNote;
			degreeToNote.put(degree, note);
			notes.add(note%12);
		}
		notes.remove(notes.size()-1);//remove last since it is the first one
		bitmask = 0;
		for (int degree=0;degree<notes.size();degree++)
		{
			int note = notes.get(degree);
			noteToDegree.put(note, degree);	
			bitmask = bitmask | (1<<note);
		}
		url = "https://ianring.com/musictheory/scales/"+bitmask;
		Collections.sort(notes);
		computeNoteNames();
	}
	/**
	 * This is how to compute diatonic names (no duplicate names)
	 * Good examples to test:
	 * C minor: C D Eb F G Ab Bb
	 * C Locrian bb7: C Db Eb Fb Gb Ab Bbb
	 */
	private void computeNoteNames() {
		String prevName = null;
		for (int n : notes)
		{
			String name = Intervals.getSharpNote(n);
			if (prevName!=null && prevName.charAt(0)==name.charAt(0))
			{
				name = Intervals.getFlatNote(n);
				if (prevName!=null && prevName.charAt(0)==name.charAt(0))
				{
					name = Intervals.getDoubleFlatNote(n);
					if (prevName!=null && prevName.charAt(0)==name.charAt(0))
					{
						name = Intervals.getDoubleSharpNote(n);
					}
				}
				
			}
			noteNames.add(name);
			if (n == rootNote)
			{
				rootName = name;
			}
			prevName = name;
		}
	}

	public Integer getDegree(int note) {
		if (noteToDegree.containsKey(note%12))
			return noteToDegree.get(note%12);
		else
			return -1;
	}
	public Integer getNote(int degree) {
		if (degree>=getSize())
			degree = notes.size()-1;
		return degreeToNote.get(degree);
	}
	public Integer getNoteWithoutModulo(int degree) {
		int note = degreeToNote.get(degree);
		if (note<=rootNote)
			return note+12;
		else
			return note;
	}
	public Integer getRootNote() {
		return rootNote;
	}
	public int getSize() {
		return notes.size();
	}
	public List<Integer> getNotes() {
		return notes;
	}
	public String getDegreeName(int index) {
		return formula.getDegreeName(index);
	}
	public String getIntervalName(int start,int end) {
		return formula.getIntervalName(start,end);
	}

	
	
	
}