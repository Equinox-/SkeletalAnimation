package com.pi.skeleton.demo;

import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CONSTANT_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LINEAR_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_QUADRATIC_ATTENUATION;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLightf;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.pi.math.Quaternion;
import com.pi.math.Vector3D;
import com.pi.skeleton.Bone;
import com.pi.skeleton.PoseableMesh;
import com.pi.skeleton.Skeleton;
import com.pi.skeleton.mesh.WavefrontLoader;

public class BoneDemo {
	private static double horizontalTan = Math.tan(Math.toRadians(25));
	static FloatBuffer l0_position = (FloatBuffer) BufferUtils
			.createFloatBuffer(4).put(new float[] { 0.0f, 0.0f, 1.0f, 0.0f })
			.rewind();
	static FloatBuffer l0_ambient = (FloatBuffer) BufferUtils
			.createFloatBuffer(4).put(new float[] { 0.2f, 0.2f, 0.2f, 1.0f })
			.rewind();

	List<int[]> movements = new ArrayList<int[]>();
	float[] times;

	public BoneDemo() throws LWJGLException, IOException {
		movements.add(new int[] { 20, Keyboard.KEY_R, Keyboard.KEY_F });
		movements.add(new int[] { 21, Keyboard.KEY_T, Keyboard.KEY_G });
		movements.add(new int[] { 22, Keyboard.KEY_Y, Keyboard.KEY_H });

		movements.add(new int[] { 2, Keyboard.KEY_U, Keyboard.KEY_J });
		movements.add(new int[] { 3, Keyboard.KEY_I, Keyboard.KEY_K });
		movements.add(new int[] { 4, Keyboard.KEY_O, Keyboard.KEY_L });

		Display.setDisplayMode(new DisplayMode(768, 768));
		Display.create();
		Skeleton sk = new Skeleton(new File("mesh.skl"));
		sk.calculate();
		PoseableMesh m = new PoseableMesh(sk,
				WavefrontLoader.loadWavefrontObject(new File("mesh.obj")));
		m.calculate();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		double aspect = 1f;
		GL11.glFrustum(-horizontalTan, horizontalTan, aspect * -horizontalTan,
				aspect * horizontalTan, 1, 100000);
		// GL11.glOrtho(-256, 256, -256, 256, -1000, 1000);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
		GL11.glCullFace(GL11.GL_BACK);

		// glEnable(GL_LIGHTING);
		glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_DEPTH_TEST);
		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
		glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0.05f);
		glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0.01f);
		glEnable(GL_LIGHT0);

		float pitch = 0;
		float yaw = 0;
		float off = 10;
		times = new float[movements.size()];

		Vector3D trans = new Vector3D();

		while (!Display.isCloseRequested()) {
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glLight(GL_LIGHT0, GL_AMBIENT, l0_ambient);
			GL11.glLight(GL_LIGHT0, GL_POSITION, l0_position);

			GL11.glTranslatef(0, 0, -off/10f);
			GL11.glRotatef(pitch, 1, 0, 0);
			GL11.glRotatef(yaw, 0, 1, 0);
			GL11.glTranslatef(-trans.x/100f, -trans.y/100f, -trans.z/100f);

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			GL11.glColor3f(1f, 1f, 1f);
			m.draw();

			GL11.glColor3f(1f, 0f, 0f);
			GL11.glBegin(GL11.GL_LINES);
			drawBone(sk.getRootBone());
			GL11.glEnd();

			Display.update();
			Display.sync(60);
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				yaw++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				pitch++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				yaw--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				pitch--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				off -= 1;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				off += 1;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				trans.x += Math.cos(Math.toRadians(yaw));
				trans.z -= Math.sin(Math.toRadians(yaw));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				trans.x -= Math.cos(Math.toRadians(yaw));
				trans.z += Math.sin(Math.toRadians(yaw));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				trans.y++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				trans.y--;
			}
			for (int q = 0; q < movements.size(); q++) {
				int[] i = movements.get(q);
				if (Keyboard.isKeyDown(i[1])) {
					sk.getBone(i[0])
							.slerp(new Quaternion(1, 0, 0, 0),
									new Quaternion().setRotation(0,
											(float) Math.PI, 0), times[q]);
					times[q] += 0.05f;
					m.calculate();
				}
				if (Keyboard.isKeyDown(i[2])) {
					sk.getBone(i[0])
							.slerp(new Quaternion(1, 0, 0, 0),
									new Quaternion().setRotation(0,
											(float) Math.PI, 0), times[q]);
					times[q] -= 0.05f;
					m.calculate();
				}
			}
			Display.setTitle("Pitch: " + pitch + ", Yaw: " + yaw + ", Off: "
					+ off);
		}
	}

	public void drawBone(Bone b) {
		GL11.glVertex3f(b.getBoneStart().x, b.getBoneStart().y,
				b.getBoneStart().z);
		GL11.glVertex3f(b.getBoneEnd().x, b.getBoneEnd().y, b.getBoneEnd().z);
		for (Bone s : b.getChildren()) {
			drawBone(s);
		}
	}

	public static void main(String[] args) throws LWJGLException, IOException {
		new BoneDemo();
	}
}
