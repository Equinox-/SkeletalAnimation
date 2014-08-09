package com.pi.skeleton;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.pi.math.Vector3D;
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
			SuperVertex sv = verticies.get(i);
			MeshVertex mv = mesh.getVerticies().get(i);
			GL11.glColor3f(sv.getColor().getRed() / 255f, sv.getColor()
					.getGreen() / 255f, sv.getColor().getBlue() / 255f);
			GL11.glTexCoord2f(mv.getTextureUV()[0], mv.getTextureUV()[1]);
			GL11.glNormal3f(mv.getNormal().x, mv.getNormal().y,
					mv.getNormal().z);
			GL11.glVertex3f(sv.getCalculatedLocation().x,
					sv.getCalculatedLocation().y, sv.getCalculatedLocation().z);
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		for (SuperVertex sv:verticies) {
			if (sv.bindNormal != null) {
				GL11.glColor3f(0f,0f,1f);
				GL11.glVertex3f(sv.getCalculatedLocation().x,
						sv.getCalculatedLocation().y, sv.getCalculatedLocation().z);
				Vector3D other = sv.middleBase;//sv.getCalculatedLocation().clone().add(sv.bindNormal.clone().multiply(0.01f));
				GL11.glVertex3f(other.x,other.y,other.z);
			}
		}
		GL11.glEnd();
	}
}