package com.pi.skeleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.pi.math.Vector3D;

public class Skeleton {
	private Bone rootBone;
	private Map<Integer, Bone> bones = new HashMap<Integer, Bone>();

	public Skeleton(File f) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(f));
		while (true) {
			String s = r.readLine();
			if (s == null) {
				break;
			}
			s = s.trim();
			if (s.startsWith("#")) {
				continue;
			}
			String[] chunks = s.split("\t");
			if (chunks.length >= 4) {
				try {
					int id = Integer.valueOf(chunks[0]);
					int parent = Integer.valueOf(chunks[1]);
					String[] start = chunks[2].split(",");
					String[] end = chunks[3].split(",");
					Vector3D startPos = new Vector3D(Float.valueOf(start[0]),
							Float.valueOf(start[1]), Float.valueOf(start[2]));
					Vector3D endPos = new Vector3D(Float.valueOf(end[0]),
							Float.valueOf(end[1]), Float.valueOf(end[2]));
					Bone b;
					if (parent < 0) {
						b = new Bone(startPos, endPos);
						rootBone = b;
					} else {
						b = new Bone(bones.get(parent), startPos, endPos);
					}
					b.calculate();
					bones.put(id, b);
				} catch (Exception e) {
					System.out.println("Invalid bone line: \"" + s + "\"\t: "
							+ e.getMessage());
				}
			}
		}
		r.close();
	}

	public void calculate() {
		rootBone.calculateRecursive();
	}

	public Iterator<Entry<Integer, Bone>> getBones() {
		return bones.entrySet().iterator();
	}

	public Bone getRootBone() {
		return rootBone;
	}

	public Bone getBone(int i) {
		return bones.get(i);
	}
}
