package com.pi.skeleton;

import java.util.ArrayList;
import java.util.List;

import com.pi.math.Quaternion;
import com.pi.math.TransMatrix;
import com.pi.math.Vector3D;

public class Bone {
	// Parameters
	private Bone parent;
	private List<Bone> children = new ArrayList<Bone>();
	private Quaternion baseRotation = new Quaternion();
	private Quaternion bonusRotation = new Quaternion();
	private float length;
	private Vector3D base;

	// Calculated
	private Quaternion calcRotation = new Quaternion();
	private Vector3D boneStart = new Vector3D();
	private Vector3D boneEnd = new Vector3D();
	private TransMatrix localToWorld = new TransMatrix();

	public Bone(Vector3D pos) {
		this(new Vector3D(), pos);
	}

	public Bone(Vector3D base, Vector3D pos) {
		this.base = base;

		Vector3D u = new Vector3D(0, 0, 1);
		Vector3D v = pos.subtract(base).clone().normalize();

		this.length = pos.magnitude();

		if (u.equals(Vector3D.negative(v))) {
			baseRotation.setRaw(0, 0, 1, 0);
		} else {
			Vector3D half = u.clone().add(v).normalize();
			baseRotation.setRaw(Vector3D.dotProduct(u, half),
					Vector3D.crossProduct(u, half));
		}
	}

	public Bone(Bone parent, Vector3D base, Vector3D pos) {
		this(parent.getLocalToWorld().inverse().multiply(base)
				.subtract(new Vector3D(0, 0, parent.length)), parent
				.getLocalToWorld().inverse().multiply(pos)
				.subtract(new Vector3D(0, 0, parent.length)));
		// System.out.println(parent.getLocalToWorld().inverse().multiply(base));
		this.parent = parent;
		this.parent.children.add(this);
		calculate();
	}

	public void addChild(Bone b) {
		if (b.parent != this && b.parent != null) {
			throw new RuntimeException("This child is already claimed.");
		}
		if (!children.contains(b)) {
			children.add(b);
		}
		b.parent = this;
	}

	public void calculate() {
		calcRotation = baseRotation.clone().multiply(bonusRotation);
		if (parent != null) {
			Vector3D parentOffset = new Vector3D(0, 0, parent.length).add(base);
			boneStart.set(parent.localToWorld.multiply(parentOffset));
			// Parent matrix * local matrix
			TransMatrix me = new TransMatrix(calcRotation, parentOffset.x,
					parentOffset.y, parentOffset.z);
			localToWorld.copy(parent.localToWorld).multiply(me);
		} else {
			boneStart.set(base);
			localToWorld.setQuaternion(calcRotation, boneStart.x, boneStart.y,
					boneStart.z);
		}
		boneEnd = localToWorld.multiply(new Vector3D(0, 0, length));
	}

	public void calculateRecursive() {
		calculate();
		for (Bone b : children) {
			b.calculateRecursive();
		}
	}
	
	public float getLength() {
		return length;
	}

	public List<Bone> getChildren() {
		return children;
	}

	public Vector3D getBoneStart() {
		return boneStart;
	}

	public Vector3D getBoneEnd() {
		return boneEnd;
	}
	
	public Vector3D getDirection() {
		return boneEnd.clone().subtract(boneStart).normalize();
	}

	public TransMatrix getLocalToWorld() {
		return localToWorld;
	}

	public void slerp(Quaternion from, Quaternion to, float time) {
		bonusRotation.slerp(from, to, time);
	}
}
