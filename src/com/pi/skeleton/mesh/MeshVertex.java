package com.pi.skeleton.mesh;

import com.pi.math.Vector3D;

public class MeshVertex {
	private Vector3D position;
	private Vector3D normal;
	private float[] textureUV;

	public MeshVertex(Vector3D pos, Vector3D normal, float[] tex) {
		this.position = pos;
		this.normal = normal;
		this.textureUV = tex;
	}
	
	public Vector3D getPosition(){
		return position;
	}
	
	public Vector3D getNormal() {
		return normal;
	}
	
	public float[] getTextureUV() {
		return textureUV;
	}
}
