package com.pi.skeleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class Animation {
	private List<Pose> poses = new ArrayList<Pose>();
	private List<Float> timing = new ArrayList<Float>();
	private float totalTime;

	public Animation(File f) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(f));
		StringBuilder poseInfo = null;
		float currentTime = -1;
		while (true) {
			String line = r.readLine();
			if (line == null) {
				break;
			}
			if (line.startsWith("#")) {
				continue;
			}
			if (currentTime < 0f) {
				currentTime = Float.valueOf(line);
			} else if (line.trim().equalsIgnoreCase("end-pose")) {
				poses.add(new Pose(poseInfo.toString()));
				timing.add(currentTime);
			} else if (line.trim().equalsIgnoreCase("start-pose")) {
				poseInfo = new StringBuilder();
			} else if (poseInfo != null) {
				poseInfo.append(line);
				poseInfo.append('\n');
			} else if (line.startsWith("end-time:")) {
				totalTime = Float.valueOf(line.substring(9));
			}
		}
		r.close();
	}

	public void animate(Skeleton s, float time) {
		time = time % totalTime;
		int i = -1;
		for (i = poses.size(); i >= 0; i--) {
			if (i >= timing.get(i)) {
				break;
			}
		}
		if (i >= 0) {
			Pose a = poses.get(i);
			int next = i + 1;
			if (next >= poses.size()) {
				next -= poses.size();
			}
			Pose b = poses.get(next);
			float progress = (time - timing.get(i));
			if (i + 1 >= poses.size()) {
				progress /= (totalTime - timing.get(i));
			} else {
				progress /= timing.get(i + 1) / timing.get(i);
			}
			Iterator<Entry<Integer, Bone>> itr = s.getBones();
			while (itr.hasNext()) {
				Entry<Integer, Bone> bone = itr.next();
				bone.getValue().slerp(a.getTransformFor(bone.getKey()),
						b.getTransformFor(bone.getKey()), progress);
			}
		}
	}
}
