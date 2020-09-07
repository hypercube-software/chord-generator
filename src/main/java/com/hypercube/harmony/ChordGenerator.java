package com.hypercube.harmony;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ChordGenerator {
	static Logger logger = Logger.getLogger(ChordGenerator.class.getName());
	
	public static void initLogs() {
	    try {
	        // Load a properties file from class path java.util.logging.config.file
	        final LogManager logManager = LogManager.getLogManager();
	        URL configURL = ChordGenerator.class.getResource("/logging.properties");
	        if (configURL != null) {
	            try (InputStream is = configURL.openStream()) {
	                logManager.readConfiguration(is);
	            }
	        } 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private class Edge {
		public Edge(String edgeId, String name, String from, String to, String color, boolean dashed) {
			this.edgeId = edgeId;
			this.from = from;
			this.to = to;
			this.name = name;
			this.color = color;
			this.dashed = dashed;
		}

		String edgeId;
		String name;
		String from;
		String to;
		String color;
		boolean dashed;
	}
	private Scale scale;
	private PrintWriter out = null;
	private List<Edge> edges = new ArrayList<Edge>();
	private HashMap<String,Chord> chords = new  HashMap<String,Chord>();
	
	public Collection<Chord> getChords() {
		return chords.values();
	}

	public ChordGenerator(Scale scale) {
		this.scale = scale;
	}

	private void printHeader() {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		out.println("<graphml\r\n" + " xmlns=\"http://graphml.graphdrawing.org/xmlns\"\r\n"
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ " xmlns:y=\"http://www.yworks.com/xml/graphml\"\r\n"
				+ " xmlns:yed=\"http://www.yworks.com/xml/yed/3\"\r\n"
				+ " xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\">\r\n");
		out.println("<key for=\"node\" id=\"d0\" yfiles.type=\"nodegraphics\"/>");
		out.println("<key for=\"edge\" id=\"d1\" yfiles.type=\"edgegraphics\"/>");
		out.println("<graph edgedefault=\"directed\" id=\"G\">");
	}

	private void printFooter() {
		for (Edge edge : edges) {
			emitEdge(edge);
		}
		out.println("</graph>");
		out.println("</graphml>");
		out.close();
	}

	private void emitNode(String id, String name, String color) {
		out.println("\t<node id=\"" + id + "\">");
		out.println("\t\t<data key=\"d0\">");
		out.println("\t\t\t<y:ShapeNode>");/*
											 * out.println("\t\t\t\t<y:Shape type=\"rectangle\"/>"); out.
											 * println("\t\t\t\t<y:Geometry height=\"30.0\" width=\"30.0\" x=\"0.0\" y=\"0.0\"/>"
											 * ); out.
											 * println("\t\t\t\t<y:BorderStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>"
											 * );
											 */
		out.println("\t\t\t\t<y:Fill color=\"" + color + "\" transparent=\"false\"/>");
		out.println("\t\t\t\t<y:NodeLabel>" + name + "</y:NodeLabel>");
		out.println("\t\t\t</y:ShapeNode>");
		out.println("\t\t</data>");
		out.println("\t</node>");
	}

	private void emitEdge(Edge edge) {
		out.println("\t<edge id=\"" + edge.edgeId + "\" source=\"" + edge.from + "\" target=\"" + edge.to + "\">");
		out.println("\t\t<data key=\"d1\">");
		out.println("\t\t\t<PolyLineEdge>");
		String type = edge.dashed ? "dashed" : "line";
		out.println("<y:LineStyle color=\"" + edge.color + "\" type=\"" + type + "\" width=\"4.0\"/>");
		out.println("\t\t\t\t<y:EdgeLabel alignment=\"center\" configuration=\"AutoFlippingLabel\">" + edge.name
				+ "</y:EdgeLabel>");
		out.println("\t\t\t</PolyLineEdge>");
		out.println("\t\t</data>");
		out.println("\t</edge>");
	}

	private int nodeCounter;

	public static void main(String[] args) throws Exception {		
		initLogs();
		//Scale s = new Scale("D Phrygian Major", 2, new ScaleFormula("H-WH-H-W-H-W-W"));
		Scale s = new Scale("C Major", 0, new ScaleFormula("T-T-S-T-T-T-S"));
		//Scale s = new Scale("E Major", 4, new ScaleFormula("T-T-S-T-T-T-S"));
		//Scale s = new Scale("C Minor", 4, new ScaleFormula("T-S-T-T-S-T-T"));
		
		ChordGenerator cg = new ChordGenerator(s);
		cg.generateChords();
		logger.fine(cg.getChords().size()+" chords");
	}
	
	public void generateChords() {
		try {
			String filename = (scale!=null?scale.getName()+" ":"")+"chords.graphml";
			filename = filename.replace("/", "-");
			String folder = "./chords";
			File f = new File(folder);
			f.mkdirs();
			out = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(folder+"/"+filename), StandardCharsets.UTF_8), true);
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE,"Unexpected error",e);
		}
		printHeader();
		startRecursion();
		printFooter();
	}
	/*
	 * Try to found a chord with a valid name given a chord without name
	 * We invert chord until we found the root chord
	 */
	private Chord getInvertedChord(Chord chord) throws Exception
	{
		for (int inversion = 1; inversion < chord.getOffsets().size(); inversion++) {
			ChordFormula icf = InvertedChord.invertedFormula(chord.getFormula(), inversion);
			if (icf==null)
				continue;
			String iname = Intervals.getChordType(icf);
			if (!iname.contains("?")) {
				int baseRootNote = (chord.getOffsets().get(inversion))%12;
				Chord baseChord = new Chord(icf, baseRootNote);
//				name = Intervals.getFlatNote(baseRootNote)+ iname +"/" + Intervals.getFlatNote((notes.get(0)+rootNote)%12);
				Chord ic = InvertedChord.forge(baseChord, chord.getOffsets().size()-inversion);				
				return ic;
			}
		}
		return null;
	}
	private void startRecursion() 
	{
		nodeCounter++;
		String nodeId = "n" + nodeCounter;
		String rootId = nodeId;
		emitNode(rootId, scale!=null?scale.getName():"", "#ffffff");
		for (int rn=0;rn<12;rn++)
		{
			if (scale!=null && !scale.getNotes().contains(rn))
			{
				continue;
			}
			List<Integer> notes = new ArrayList<Integer>();
			notes.add(0);
			String debugMessage = Intervals.getFlatNote(rn);
			recurseChords(rootId,rn,0,notes,debugMessage);
		}
	}
	private int recurseChords(String parentId, int rootNote,int interval, List<Integer> notes, String debugMessage) {
		// limit the recursion to 8 notes chords
		if (notes.size() == 8) {
			return 0;
		}
		
		// chord identification
		Chord chord = null;
		Chord invertedChord = null;
		try {
			String name = "?";
			if (notes.size() == 1)
				name = "P1";
			else if (ChordFormula.isValid(notes.toArray(new Integer[0]))) {
				ChordFormula cf = new ChordFormula(notes.toArray(new Integer[0]));
				chord = new Chord(cf, rootNote);
				invertedChord = getInvertedChord(chord);
				name = Intervals.getChordType(cf);
				if (name.contains("?") && invertedChord!=null) {					
					chord = invertedChord;
					invertedChord = null;
					name = chord.getChordName();
				}
				else if (notes.size() >= 3 && !name.startsWith("?"))
				{
					name = Intervals.getFlatNote(rootNote) + name;
				}
			}
						
			name += "\n";
			for (int i = 0; i < notes.size(); i++) {
				if (i > 0)
					name += ",";
				name += Intervals.getFlatNote((rootNote+notes.get(i)) % 12);
			}
			
			logger.fine(debugMessage);
			
			nodeCounter++;
			String nodeId = "n" + nodeCounter;

			//
			// depth first 
			//
			int nbChordFound = 0;
			for (int i = interval+1; i <= Intervals.getInterval("M13"); i++) {
				if (i-interval>6) // we go up to a phrygian/lydian triad which have the biggest gap between notes
					continue;
				if (scale!=null && !scale.getNotes().contains((rootNote+i)%12))
					continue;
				String _name = "," + Intervals.getInterval(i);
				List<Integer> newChord = new ArrayList<Integer>();
				newChord.addAll(notes);
				newChord.add(i);
				nbChordFound += recurseChords(nodeId, rootNote,i, newChord, debugMessage +_name);
			}
			//
			// Generate graphml file after
			//
			if (!name.contains("?")) {
				nbChordFound++;
			}
			if (nbChordFound > 0) {
				if (parentId != null) {
					edges.add(new Edge("e" + edges.size() + 1, Intervals.getInterval(interval), parentId, nodeId,
							"#000000", false));
				}
				if (name.contains("?"))
					emitNode(nodeId, name, "#ffffff");
				else if (notes.size() < 3)
					emitNode(nodeId, name, "#ffcc00");
				else
				{
					if (invertedChord!=null && !chords.containsKey(invertedChord.getChordName()))
					{
						chords.put(invertedChord.getChordName(),invertedChord);
					}
					if (chord!=null && !chords.containsKey(chord.getChordName()))
					{
						chords.put(chord.getChordName(), chord);
					}
					else if (chord==null)
					{
						throw new Exception("Impossible");
					}
					emitNode(nodeId, name, "#ff6600");
				}
			}
			return nbChordFound;
		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unexpected error",e);
			return 0;
		}
	}
}
