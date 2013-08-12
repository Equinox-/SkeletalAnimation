package com.pi.skeleton.mesh;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class Mesh {
	private List<MeshVertex> verticies;
	private List<Integer> indicies;
	private int polygonSize;

	public Mesh(List<MeshVertex> verts, List<Integer> indz, int polygonSize) {
		this.verticies = verts;
		this.indicies = indz;
		this.polygonSize = polygonSize;
	}

	public List<MeshVertex> getVerticies() {
		return verticies;
	}

	public List<Integer> getIndicies() {
		return indicies;
	}
	
	public int getPolygonSize() {
		return polygonSize;
	}
	
	public int getGLType() {
		switch(polygonSize) {
		case 1:
			return GL11.GL_POINTS;
		case 2:
			return GL11.GL_LINES;
		case 3:
			return GL11.GL_TRIANGLES;
		case 4:
			return GL11.GL_QUADS;
		default:
			return GL11.GL_POLYGON;
		}
	}
}
