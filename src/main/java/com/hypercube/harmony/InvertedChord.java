package com.hypercube.harmony;

public class InvertedChord extends Chord {

	private Chord baseChord;
	private int inversion;
	
	public Chord getBaseChord() {
		return baseChord;
	}
	
	public static ChordFormula invertedFormula(ChordFormula formula,int inversion) throws Exception
	{
		// compute the inversions
		// {a,b,c,d}
		// {b,c,d,a} inv 1
		// {c,d,a,b} inv 2
		// {d,a,b,c} inv 3

		Integer[] newFormula = new Integer[formula.getIntervals().size()];
		// rotate to the right the list by "inversion" steps
		// {a,b,c,d} = {b,c,d,a}
		for (int i = 0; i < newFormula.length; i++) {
			newFormula[i] = formula.getIntervals().get((i + inversion) % formula.getIntervals().size());
			
		}
		// this formula is tricky to understand.
		// imagine you have a big chord with 0,3,7,10,14
		// you require the inversion 3 so it will be 10,14,0,3,7
		// so newFormula[inversion] is the latest bigest number before the invertion: 14
		// because this number is after one octave, raise will be equal to 2
		// I had a bug before because I did newFormula[0]
		int raise = (newFormula[newFormula.length-inversion-1] / 12)+1;
		// raise the last offsets to one octave
		for (int i = newFormula.length - inversion; i < newFormula.length; i++) {
			newFormula[i] += raise*12;
			
		}
		// reduce
		int reduce = newFormula[0] / 12;
		for (int i = 0; i < newFormula.length; i++) {
			newFormula[i] -= reduce * 12;
			
		}
		if (!ChordFormula.isValid(newFormula))
			return null;
		return new ChordFormula(newFormula);
	}
	public static InvertedChord forge(Chord baseChord,int inversion) throws Exception
	{
		if (baseChord instanceof InvertedChord)
			throw new Exception("Cannot invert an inverted chord");
		ChordFormula invertedFormula = invertedFormula(baseChord.getFormula(),inversion);
		if (invertedFormula==null)
			return null;
		else
			return new InvertedChord(invertedFormula,baseChord, inversion);
	}
	private InvertedChord(ChordFormula invertedFormula,Chord baseChord,int inversion) throws Exception {
		super(invertedFormula, baseChord.getOffsets().get(inversion));
		this.baseChord = baseChord;
		this.inversion = inversion;
		
	}
	@Override
	public String getChordName() {
		return baseChord.getChordName()+"/"+Intervals.getSharpNote(getOffsets().get(0));
	}
	@Override
	public String getType() {
		return baseChord.getType();
	}
}
