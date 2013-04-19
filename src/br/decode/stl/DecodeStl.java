package br.decode.stl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

@SuppressLint("ViewConstructor")
public class DecodeStl extends View{

	private Integer rawResource;
	private String pathFile;

	public DecodeStl(Context context, int rawResource) {
		super(context);
		this.rawResource = rawResource;
	}

	public DecodeStl(Context context, String pathFile) {
		super(context);
		this.pathFile = pathFile;
	}

	@SuppressWarnings({ "resource", "deprecation" })
	public StlBean read() {
		LittleEndianDataInputStream ledis = new LittleEndianDataInputStream(getInputStreamFile());
		String firstLine = null;
		try {
			firstLine = ledis.readLine().trim();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (firstLine != null && firstLine.contains("solid")){
			return decodeAscii(new LittleEndianDataInputStream(getInputStreamFile()));
		}
		return decodeBinary(new LittleEndianDataInputStream(getInputStreamFile()));
	}

	private StlBean decodeBinary(LittleEndianDataInputStream ledis){
    	StlBean stl = new StlBean();

		Integer qtdTriangles = 0;
		Float centroX = 0.f, centroY = 0.f, centroZ = 0.f;

		try {
			for (int i = 0; i < 80; i++) ledis.readByte();
			qtdTriangles = ledis.readInt();
			long l = qtdTriangles & 0xFFFFFFFFL;
			qtdTriangles = (int) l;
		} catch (IOException e) {
			e.printStackTrace();
		}

		float vertices[] = new float[qtdTriangles * 9];
		float normais[] = new float[qtdTriangles * 3];

		int count = 0;
		int indice_normais = 0;
		int indice_vertices = 0;

		float maiorX = 0;
		float menorX = 0;
		float maiorY = 0;
		float menorY = 0;
		float maiorZ = 0;
		float menorZ = 0;

		while (count < qtdTriangles){
			try{
				normais[indice_normais++] = ledis.readFloat(); // normalX
				normais[indice_normais++] = ledis.readFloat(); // normalY
				normais[indice_normais++] = ledis.readFloat(); // normalZ

				float vertice1X = ledis.readFloat();
				float vertice1Y = ledis.readFloat();
				float vertice1Z = ledis.readFloat();

				float vertice2X = ledis.readFloat();
				float vertice2Y = ledis.readFloat();
				float vertice2Z = ledis.readFloat();

				float vertice3X = ledis.readFloat();
				float vertice3Y = ledis.readFloat();
				float vertice3Z = ledis.readFloat();

				vertices[indice_vertices++] = vertice1X;
				vertices[indice_vertices++] = vertice1Y;
				vertices[indice_vertices++] = vertice1Z;
				vertices[indice_vertices++] = vertice2X;
				vertices[indice_vertices++] = vertice2Y;
				vertices[indice_vertices++] = vertice2Z;
				vertices[indice_vertices++] = vertice3X;
				vertices[indice_vertices++] = vertice3Y;
				vertices[indice_vertices++] = vertice3Z;

				if (vertice1X > maiorX) maiorX = vertice1X;
				if (vertice1X < menorX) menorX = vertice1X;
				if (vertice2X > maiorX) maiorX = vertice2X;
				if (vertice2X < menorX) menorX = vertice2X;
				if (vertice3X > maiorX) maiorX = vertice3X;
				if (vertice3X < menorX) menorX = vertice3X;

				if (vertice1Y > maiorY) maiorY = vertice1Y;
				if (vertice1Y < menorY) menorY = vertice1Y;
				if (vertice2Y > maiorY) maiorY = vertice2Y;
				if (vertice2Y < menorY) menorY = vertice2Y;
				if (vertice3Y > maiorY) maiorY = vertice3Y;
				if (vertice3Y < menorY) menorY = vertice3Y;

				if (vertice1Z > maiorZ) maiorZ = vertice1Z;
				if (vertice1Z < menorZ) menorZ = vertice1Z;
				if (vertice2Z > maiorZ) maiorZ = vertice2Z;
				if (vertice2Z < menorZ) menorZ = vertice2Z;
				if (vertice3Z > maiorZ) maiorZ = vertice3Z;
				if (vertice3Z < menorZ) menorZ = vertice3Z;

				ledis.readByte();
				ledis.readByte();

				count++;
			} catch (IOException e){
				e.printStackTrace();
			}
		}

		centroX = (maiorX - menorX) / 2;
		centroY = (maiorY - menorY) / 2;
		centroZ = (maiorZ - menorZ) / 2;

		stl.setQtdTriangles(qtdTriangles);
		stl.setNormais(normais);
		stl.setVertices(vertices);
		stl.setCentroX(centroX);
		stl.setCentroY(centroY);
		stl.setCentroZ(centroZ);

		return stl;
	}

	@SuppressWarnings("deprecation")
	private StlBean decodeAscii(LittleEndianDataInputStream ledis){
    	StlBean stl = new StlBean();

		Float centroX = 0.f, centroY = 0.f, centroZ = 0.f;
    	String[] lineArray = new String[50];
    	ArrayList<Float> normais = new ArrayList<Float>();
    	ArrayList<Float> vertices = new ArrayList<Float>();
    	Integer qtdTriangles = 0;
		float maiorX = 0;
		float menorX = 0;
		float maiorY = 0;
		float menorY = 0;
		float maiorZ = 0;
		float menorZ = 0;

    	try {
			ledis.readLine(); // solid [name]

			lineArray = removeSpacesFromArray(ledis.readLine()); // facet normal ni nj nk (PRIMEIRO APENAS)

			while (!lineArray[0].equalsIgnoreCase("endsolid")){
				qtdTriangles++;
				normais.add(Float.valueOf(lineArray[2]));
				normais.add(Float.valueOf(lineArray[3]));
				normais.add(Float.valueOf(lineArray[4]));

				ledis.readLine(); // outer loop
				
				lineArray = removeSpacesFromArray(ledis.readLine()); // vertex v1x v1y v1z
				float vertice1X = Float.valueOf(lineArray[1]);
				float vertice1Y = Float.valueOf(lineArray[2]);
				float vertice1Z = Float.valueOf(lineArray[3]);

				lineArray = removeSpacesFromArray(ledis.readLine()); // vertex v1x v1y v1z
				float vertice2X = Float.valueOf(lineArray[1]);
				float vertice2Y = Float.valueOf(lineArray[2]);
				float vertice2Z = Float.valueOf(lineArray[3]);

				lineArray = removeSpacesFromArray(ledis.readLine()); // vertex v1x v1y v1z
				float vertice3X = Float.valueOf(lineArray[1]);
				float vertice3Y = Float.valueOf(lineArray[2]);
				float vertice3Z = Float.valueOf(lineArray[3]);

				vertices.add(vertice1X);
				vertices.add(vertice1Y);
				vertices.add(vertice1Z);
				vertices.add(vertice2X);
				vertices.add(vertice2Y);
				vertices.add(vertice2Z);
				vertices.add(vertice3X);
				vertices.add(vertice3Y);
				vertices.add(vertice3Z);

				if (vertice1X > maiorX) maiorX = vertice1X;
				if (vertice1X < menorX) menorX = vertice1X;
				if (vertice2X > maiorX) maiorX = vertice2X;
				if (vertice2X < menorX) menorX = vertice2X;
				if (vertice3X > maiorX) maiorX = vertice3X;
				if (vertice3X < menorX) menorX = vertice3X;

				if (vertice1Y > maiorY) maiorY = vertice1Y;
				if (vertice1Y < menorY) menorY = vertice1Y;
				if (vertice2Y > maiorY) maiorY = vertice2Y;
				if (vertice2Y < menorY) menorY = vertice2Y;
				if (vertice3Y > maiorY) maiorY = vertice3Y;
				if (vertice3Y < menorY) menorY = vertice3Y;

				if (vertice1Z > maiorZ) maiorZ = vertice1Z;
				if (vertice1Z < menorZ) menorZ = vertice1Z;
				if (vertice2Z > maiorZ) maiorZ = vertice2Z;
				if (vertice2Z < menorZ) menorZ = vertice2Z;
				if (vertice3Z > maiorZ) maiorZ = vertice3Z;
				if (vertice3Z < menorZ) menorZ = vertice3Z;

				ledis.readLine(); // endloop
				ledis.readLine(); // endfacet

				lineArray = removeSpacesFromArray(ledis.readLine()); // facet normal ni nj nk
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

    	int index = 0;
    	float[] normaisFinal = new float[normais.size()];
    	for (Float f : normais) {
			normaisFinal[index++] = f;
		}
    	
    	index = 0;
    	float[] verticesFinal = new float[vertices.size()];
    	for (Float f : vertices) {
			verticesFinal[index++] = f;
		}

		centroX = (maiorX - menorX) / 2;
		centroY = (maiorY - menorY) / 2;
		centroZ = (maiorZ - menorZ) / 2;

    	stl.setQtdTriangles(qtdTriangles);
    	stl.setNormais(normaisFinal);
    	stl.setVertices(verticesFinal);
    	stl.setCentroX(centroX);
    	stl.setCentroY(centroY);
    	stl.setCentroZ(centroZ);
    	return stl;
	}

	private String[] removeSpacesFromArray(String line){
    	String[] lineArray = new String[50];
		lineArray = line.split(" ");

		// Retira os espaços em branco do vetor
		String[] lineArrayFinal = new String[lineArray.length];
		int index_final = 0;
		for (int i = 0; i < lineArray.length; i++) {
			if (!lineArray[i].equalsIgnoreCase("")){
				lineArrayFinal[index_final++] = lineArray[i];
			}
		}
		return lineArrayFinal;
	}

	private InputStream getInputStreamFile(){
        InputStream is = null;
		if (this.rawResource != null){
			is = getResources().openRawResource(rawResource);
		}else if (this.pathFile != null){
			try {
				is = new FileInputStream(this.pathFile);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		return is;
	}
}
