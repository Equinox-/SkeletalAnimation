package com.pi.skeleton;

import java.util.HashMap;
import java.util.Map;

import com.pi.math.Quaternion;

public class Pose {
	private Map<Integer, Quaternion> pose = new HashMap<Integer, Quaternion>();

	public Pose(String info) {
		String[] lines = info.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String[] parts = lines[i].split("\t");
			if (parts.length >= 5) {
				try {
					pose.put(
							Integer.valueOf(parts[0]),
							new Quaternion(Float.valueOf(parts[1]), Float
									.valueOf(parts[2]),
									Float.valueOf(parts[3]), Float
											.valueOf(parts[4])));
				} catch (Exception e) {
					System.out.println("Error in pose syntax: \""
							+ e.toString() + "\"");
				}
			}
		}
	}

	public Quaternion getTransformFor(int bone) {
		return pose.get(bone);
	}
}
