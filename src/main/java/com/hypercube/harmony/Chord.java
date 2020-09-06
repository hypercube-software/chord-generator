package com.hypercube.harmony;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Chord {
	private ChordFormula formula;
	private Integer baseNote;
	private String type;
	private List<Integer> offsets;
	private List<Integer> transposedOffsets;
	private Scale scale;
	private String chordName;

	public double computeDissonantScore() {
		int score = 0;
		for (Integer interval : formula.getIntervals())
		{
			if (interval == Intervals.getInterval("m2") ||
				interval == Intervals.getInterval("M2") ||
				interval == Intervals.getInterval("m7") ||
				interval == Intervals.getInterval("M7") ||
				interval == Intervals.getInterval("Tritone")
				)
			{
				score++;
			}
		}
	    // the final score is a % of how many dissonant intervals are found between all intervals
		// In practice it is from 0 to 50%
		return score/(double)formula.getIntervals().size();
	}
	public ChordFormula getFormula() {
		return formula;
	}
	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		this.scale = scale;
		this.chordName = null;
	}

	@Override
	public String toString() {
		String r = getChordName()+" {"+getChordNotes()+"} offsets {";
		for (int i = 0; i < getOffsets().size(); i++) {
			if (i > 0)
				r += ",";
			r += getOffsets().get(i);
		}
		return r+"} formula: "+formula.toString();
	}

	public String getType() {
		return type;
	}

	public Chord(ChordFormula formula, int baseNote) {
		this.type = Intervals.getChordType(formula);
		this.baseNote = baseNote;
		this.formula = formula;

		offsets = new ArrayList<Integer>();

		for (int i = 0; i < formula.getIntervals().size(); i++) {
			int currentNoteOffset = baseNote + formula.getIntervals().get(i);
			offsets.add(currentNoteOffset);
		}
				
		int reduce = offsets.get(0) / 12;
		for (int i = 0; i < offsets.size(); i++) {
			offsets.set(i, offsets.get(i)- reduce * 12);
		}
		
	}

	public Integer getBaseNote() {
		return baseNote;
	}

	public List<Integer> getOffsets() {
		if (transposedOffsets!=null)
			return transposedOffsets;
		else
			return offsets;
	}
	
	public int getAverageOffset() {
		int result = 0;
		for (Integer i : offsets)
		{
			result += i;
		}
		return result/offsets.size();
	}
	public int distance(Chord chord)
	{
		int nbOverlap = 0;
		int minGap = Integer.MAX_VALUE;
		int maxGap = 0;
		for (Integer o : chord.getOffsets())
		{
			for (Integer o2 : getOffsets())
			{
				if (o==o2)
					nbOverlap++;
				minGap = Math.min(minGap, Math.abs(o2-o));
				maxGap = Math.max(maxGap, Math.abs(o2-o));
			}			
		}
		int OverlapInverse = offsets.size()-nbOverlap;
		return (OverlapInverse<<16) + (minGap<<8) + (maxGap);
	}
	public String getChordName() {
		if (chordName==null)
		{
			chordName = computeChordName();
		}
		return chordName;
	}
	/**
	 * Compute the chord name. If the scale is set, it use the right note name
	 * to respect the diatonic names (no double note like in C minor: G followed by Ab and not G#)
	 * @return
	 */
	private String computeChordName() {
		int note = getBaseNote()%12;
		if (scale!=null)
		{
			return scale.getNoteNames().get(scale.getDegree(note)) + getType();
		}
		else
		{
			return Intervals.getSharpNote(note) + getType();
		}
	}

	public String getChordNotes() {
		String r = "";
		List<Integer> notes = new ArrayList<Integer>();
		for (int i = 0; i < getOffsets().size(); i++) {
			int n = getOffsets().get(i);
			notes.add(n);
		}
		int reduce = notes.get(0) / 12;
		for (int i = 0; i < notes.size(); i++) {
			int n = notes.get(i) - reduce * 12;

			if (i > 0)
				r += ",";
			
			int note = n%12;
			if (scale!=null)
				r += scale.getNoteNames().get(note);
			else
				r += Intervals.getSharpNote(note);
			if (n / 12 > 0) {
				r += (n / 12);
			}
		}
		return r;
	}

	public Integer getComplexity() {
		int c = getOffsets().size();
		if (this instanceof InvertedChord)
			c += 0x10;
		if (getChordName().contains("6"))
			c += 0x100;
		return c;
	}

	static private String convertDecimalToFraction(double x) {
		if (x < 0) {
			return "-" + convertDecimalToFraction(-x);
		}
		double tolerance = 1.0E-6;
		double h1 = 1;
		double h2 = 0;
		double k1 = 0;
		double k2 = 1;
		double b = x;
		do {
			double a = Math.floor(b);
			double aux = h1;
			h1 = a * h1 + h2;
			h2 = aux;
			aux = k1;
			k1 = a * k1 + k2;
			k2 = aux;
			b = 1 / (b - a);
		} while (Math.abs(x - h1 / k1) > x * tolerance);

		return (int)h1 + "/" + (int)k1;
	}

	public static double noteFrequency(int note) {
		double step = Math.pow(2, 1 / 12.0);
		// real frequency, equal temperament
		double f = 440/2 * Math.pow(step, note + 3); // +3 is the distance beetween A 440 and C
		
		return f;
	}
	public static double fakeFrequency(int note) {
		double[] freqOffsets = new double[] { 0, +4.64, -0.67, -2.84, +2.60, +0.40, +2.07, -0.44, -3.30, +3.94, -4.77,
				+3.33 };		
		double f = noteFrequency(note);
		f -= freqOffsets[note%12];
		return f;
	}

	public void dumpFreqRatio() {
		String[] notesNames = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
		double tonicFrequency = -1;
		double[] frequencies = new double[getOffsets().size()];
		for (int o = 0; o < getOffsets().size(); o++) {
			int i = getOffsets().get(o);
			int n = i % 12;
			frequencies[o] = fakeFrequency(i);
			if (n==baseNote)
				tonicFrequency = frequencies[o];
		}
		for (int o = 0; o < getOffsets().size(); o++) {
			int i = getOffsets().get(o);
			int n = i % 12;
			double f = frequencies[o];
			double r = f / tonicFrequency;
			DecimalFormat newFormat = new DecimalFormat("#.##");
			double rRounded =  Double.valueOf(newFormat.format(r).replace(',', '.'));
			/*logger.fine(notesNames[n] + ":" + String.format("%.2f", f) + "Hz ratio from tonic "
					+ notesNames[tonic] + " " + String.format("%.2f", tonicFrequency) + " Hz : " + String.format("%.2f", r) + " "
					+ convertDecimalToFraction(rRounded)+" "+rRounded
			);*/
		}
	}
}