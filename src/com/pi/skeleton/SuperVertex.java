package com.pi.skeleton;

import com.pi.math.MathUtil;
import com.pi.math.Vector3D;

public class SuperVertex {
	private static final float MAXIMUM_SECONDARY_DIFFERENCE = 0.00025f;
	private static final float DOT_PRODUCT_WEIGHT = 0.5f;

	// Saved data
	private Vector3D naturalPosition;
	private Vector3D normal;

	private Vector3D boneOffset;
	private Bone binding;
	private float bindDistance;
	private float bindNormality;
	private float bindWeight;

	private Vector3D secondaryBoneOffset;
	private Bone secondaryBinding;
	private float secondaryBindDistance;
	private float secondaryBindNormality;
	private float secondaryBindWeight;

	// Calculated
	private Vector3D vertexLocation = new Vector3D();

	public SuperVertex(Vector3D location, Vector3D normal, Skeleton skeleton) {
		this.naturalPosition = location;
		this.normal = normal;
		this.bindWeight = Float.MAX_VALUE;
		this.secondaryBindWeight = Float.MAX_VALUE;
		getBestBone(skeleton.getRootBone());

		boneOffset = binding.getLocalToWorld().inverse()
				.multiply(naturalPosition);
		if (Math.abs((secondaryBindDistance - bindDistance)
				+ (secondaryBindNormality - bindNormality)) > MAXIMUM_SECONDARY_DIFFERENCE) {
			secondaryBoneOffset = null;
			secondaryBinding = null;
		} else {
			secondaryBoneOffset = secondaryBinding.getLocalToWorld().inverse()
					.multiply(naturalPosition);
		}
	}

	private void getBestBone(Bone b) {
		float[] dist = MathUtil.getRelationToLine(naturalPosition,
				b.getBoneStart(), b.getBoneEnd());
		Vector3D bNormal = (b.getBoneEnd().clone().subtract(b.getBoneStart())
				.normalize().multiply(dist[1])).add(b.getBoneStart())
				.subtract(naturalPosition).reverse().normalize();
		float cosDot = Vector3D.dotProduct(bNormal, normal);
		float bNormality = Math.abs(cosDot) * DOT_PRODUCT_WEIGHT;
		float bWeight = dist[0] + (DOT_PRODUCT_WEIGHT - bNormality);
		if (bWeight < secondaryBindWeight) {
			if (cosDot > -0.5f) {
				if (bWeight < bindWeight) {
					secondaryBindDistance = bindDistance;
					secondaryBinding = binding;
					secondaryBindNormality = bindNormality;
					secondaryBindWeight = bindWeight;
					binding = b;
					bindDistance = dist[0];
					bindNormality = bNormality;
					bindWeight = bWeight;
				} else {
					secondaryBinding = b;
					secondaryBindDistance = dist[0];
					secondaryBindNormality = bNormality;
					secondaryBindWeight = bWeight;
				}
			}
		}
		for (Bone child : b.getChildren()) {
			getBestBone(child);
		}
	}

	public void calculate() {
		if (secondaryBoneOffset == null) {
			vertexLocation.set(binding.getLocalToWorld().multiply(boneOffset));
		} else {
			Vector3D primaryPosition = binding.getLocalToWorld()
					.multiply(boneOffset).multiply(secondaryBindWeight + 1f);
			Vector3D secondaryPosition = secondaryBinding.getLocalToWorld()
					.multiply(secondaryBoneOffset).multiply(bindWeight + 1f);
			vertexLocation.set(primaryPosition.add(secondaryPosition).multiply(
					1f / (bindWeight + secondaryBindWeight + 2f)));
		}
	}

	public Vector3D getCalculatedLocation() {
		return vertexLocation;
	}
}