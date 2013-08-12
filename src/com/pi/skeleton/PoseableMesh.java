package com.pi.skeleton;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.pi.skeleton.mesh.Mesh;
import com.pi.skeleton.mesh.MeshVertex;

public class PoseableMesh {
	private List<SuperVertex> verticies = new ArrayList<SuperVertex>();
	private Skeleton sk;
	private Mesh mesh;

	public PoseableMesh(Skeleton sk, Mesh m) {
		this.sk = sk;
		this.mesh = m;
		for (MeshVertex v : m.getVerticies()) {
			verticies.add(new SuperVertex(v.getPosition(), v.getNormal(), sk));
		}
	}

	public void calculate() {
		sk.calculate();
		for (SuperVertex v : verticies) {
			v.calculate();
		}
	}

	public void draw() {
		GL11.glBegin(mesh.getGLType());
		for (int i : mesh.getIndicies()) {
			GL11.glTexCoord2f(mesh.getVerticies().get(i).getTextureUV()[0],
					mesh.getVerticies().get(i).getTextureUV()[1]);
			GL11.glNormal3f(mesh.getVerticies().get(i).getNormal().x, mesh
					.getVerticies().get(i).getNormal().y, mesh.getVerticies()
					.get(i).getNormal().z);
			GL11.glVertex3f(verticies.get(i).getCalculatedLocation().x,
					verticies.get(i).getCalculatedLocation().y, verticies
							.get(i).getCalculatedLocation().z);
		}
		GL11.glEnd();
	}
}