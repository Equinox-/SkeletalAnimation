package com.pi.skeleton;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

import com.pi.math.MathUtil;
import com.pi.math.Vector3D;

public class SuperVertex {
	private static final float MAXIMUM_SECONDARY_DIFFERENCE = 0.25f;
	private static final float MAXIMUM_SECONDARY_DISTANCE = 0.25f;
	private static final float DOT_PRODUCT_WEIGHT = 0.15f;

	// Saved data
	private Vector3D naturalPosition;
	private Vector3D normal;

	Vector3D bindNormal;

	private Vector3D boneOffset;
	private Bone binding;
	private float[] bindDistance;
	private float bindNormality;
	private float bindWeight;
	private float bindJointyness;

	private Vector3D secondaryBoneOffset;
	private Bone secondaryBinding;
	private float[] secondaryBindDistance;
	private float secondaryBindNormality;
	private float secondaryBindWeight;
	private float secondaryBindJointyness;

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
		if (secondaryBinding == null
				|| (!secondaryBinding.getChildren().contains(binding) && !binding
						.getChildren().contains(secondaryBinding))
				|| Math.max(bindJointyness, secondaryBindJointyness) > MAXIMUM_SECONDARY_DIFFERENCE
				|| secondaryBindDistance[0] > MAXIMUM_SECONDARY_DISTANCE) {
			secondaryBoneOffset = null;
			secondaryBinding = null;
			bindNormal = null;
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
		float jointyness = Math.min(Math.abs(dist[2]),
				Math.abs(b.getLength() - dist[2]));
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
					secondaryBindJointyness = bindJointyness;
					binding = b;
					bindDistance = dist;
					bindNormality = bNormality;
					bindWeight = bWeight;
					bindJointyness = jointyness;
				} else {
					secondaryBinding = b;
					secondaryBindDistance = dist;
					secondaryBindNormality = bNormality;
					secondaryBindWeight = bWeight;
					secondaryBindJointyness = jointyness;
				}
				bindNormal = normal;
			}
		}
		for (Bone child : b.getChildren()) {
			getBestBone(child);
		}
	}

	Vector3D middleBase = null;

	public void calculate() {
		if (secondaryBoneOffset == null) {
			vertexLocation.set(binding.getLocalToWorld().multiply(boneOffset));
		} else {
			Vector3D primaryPosition = binding.getLocalToWorld().multiply(
					boneOffset);
			Vector3D secondaryPosition = secondaryBinding.getLocalToWorld()
					.multiply(secondaryBoneOffset);
			Vector3D lurpee = secondaryBindDistance[0] < bindDistance[0] ? secondaryPosition
					: primaryPosition;

			// Slerp da lerp
			Vector3D base = null;
			middleBase = null;
			if (binding.getChildren().contains(secondaryBinding)) {
				base = binding.getBoneEnd().clone()
						.add(secondaryBinding.getBoneStart()).multiply(0.5f);
			} else {
				base = binding.getBoneStart().clone()
						.add(secondaryBinding.getBoneEnd()).multiply(0.5f);
			}

			Vector3D middleBaseA = binding
					.getBoneStart()
					.clone()
					.add(binding.getDirection().clone().normalize()
							.multiply(bindDistance[2]));
			Vector3D middleBaseB = secondaryBinding
					.getBoneStart()
					.clone()
					.add(secondaryBinding.getDirection().clone().normalize()
							.multiply(secondaryBindDistance[2]));
			if (secondaryBindDistance[0] > bindDistance[0]) {
				middleBase = middleBaseA;
			} else {
				middleBase = middleBaseB;
			}

			primaryPosition = binding.getLocalToWorld().multiply(boneOffset)
					.subtract(middleBase);
			float primaryMag = primaryPosition.magnitude();
			primaryPosition.multiply(1f / primaryMag);
			secondaryPosition = secondaryBinding.getLocalToWorld()
					.multiply(secondaryBoneOffset).subtract(middleBase);
			float secondaryMag = secondaryPosition.magnitude();
			secondaryPosition.multiply(1f / secondaryMag);

			float jointy = (bindJointyness + secondaryBindJointyness) / 2.0f / 0.2f;
			float thickness = secondaryBindDistance[0] > bindDistance[0] ? bindDistance[3]
					: secondaryBindDistance[3];

			Vector3D slurpee = Vector3D
					.slerp(primaryPosition,
							secondaryPosition,
							0.5f
									+ (secondaryBindDistance[0] < bindDistance[0] ? -1.0f
											: 1.0f) * (jointy * 0.5f))
					.normalize().multiply(thickness).add(middleBase);// .subtract(middleBase);
			// if (Vector3D.dotProduct(slurpee,
			// lurpee.clone().subtract(middleBase)) < 0) {
			// slurpee.multiply(-1f);
			// }
			// slurpee.add(middleBase).add(base);

			vertexLocation.set(lurpee.multiply(jointy)).add(
					slurpee.multiply(1f - jointy));
			vertexLocation.subtract(middleBase).normalize().multiply(thickness)
					.add(middleBase);
		}
	}

	public ReadableColor getColor() {
		return secondaryBinding != null ? Color.GREEN : Color.DKGREY;
	}

	public Vector3D getCalculatedLocation() {
		return vertexLocation;
	}
}