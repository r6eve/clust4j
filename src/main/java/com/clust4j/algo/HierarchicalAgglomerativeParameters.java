/*******************************************************************************
 *    Copyright 2015, 2016 Taylor G Smith
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/

package com.clust4j.algo;

import java.util.Random;

import org.apache.commons.math3.linear.AbstractRealMatrix;

import com.clust4j.algo.HierarchicalAgglomerative.Linkage;
import com.clust4j.algo.preprocess.FeatureNormalization;
import com.clust4j.metrics.pairwise.GeometricallySeparable;

final public class HierarchicalAgglomerativeParameters 
		extends BaseClustererParameters 
		implements UnsupervisedClassifierParameters<HierarchicalAgglomerative> {

	private static final long serialVersionUID = -1333222392991867085L;
	private Linkage linkage = HierarchicalAgglomerative.DEF_LINKAGE;
	private int num_clusters = 2;

	public HierarchicalAgglomerativeParameters() {
	}

	public HierarchicalAgglomerativeParameters(Linkage linkage) {
		this();
		this.linkage = linkage;
	}

	@Override
	public HierarchicalAgglomerative fitNewModel(AbstractRealMatrix data) {
		return new HierarchicalAgglomerative(data, this.copy()).fit();
	}

	@Override
	public HierarchicalAgglomerativeParameters copy() {
		return new HierarchicalAgglomerativeParameters(linkage)
			.setMetric(metric)
			.setScale(scale)
			.setSeed(seed)
			.setVerbose(verbose)
			.setNumClusters(num_clusters)
			.setNormalizer(norm)
			.setForceParallel(parallel);
	}

	public Linkage getLinkage() {
		return linkage;
	}

	public HierarchicalAgglomerativeParameters setLinkage(Linkage l) {
		this.linkage = l;
		return this;
	}
	
	public int getNumClusters() {
		return num_clusters;
	}

	public HierarchicalAgglomerativeParameters setNumClusters(final int d) {
		this.num_clusters = d;
		return this;
	}

	@Override
	public HierarchicalAgglomerativeParameters setForceParallel(boolean b) {
		this.parallel = b;
		return this;
	}

	@Override
	public HierarchicalAgglomerativeParameters setScale(boolean b) {
		this.scale = b;
		return this;
	}

	@Override
	public HierarchicalAgglomerativeParameters setSeed(final Random seed) {
		this.seed = seed;
		return this;
	}

	@Override
	public HierarchicalAgglomerativeParameters setVerbose(boolean b) {
		this.verbose = b;
		return this;
	}

	@Override
	public HierarchicalAgglomerativeParameters setMetric(GeometricallySeparable dist) {
		this.metric = dist;
		return this;
	}

	@Override
	public HierarchicalAgglomerativeParameters setNormalizer(FeatureNormalization norm) {
		this.norm = norm;
		return this;
	}
}
