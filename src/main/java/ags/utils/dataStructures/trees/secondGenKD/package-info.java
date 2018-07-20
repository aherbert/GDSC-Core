/*
 * Copyright 2009 Rednaxela
 *
 * Modifications to the code have been made by Alex Herbert for a smaller 
 * memory footprint and optimised 2D processing for use with image data
 * as part of the Genome Damage and Stability Centre ImageJ Core Package.
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 *
 *    2. This notice may not be removed or altered from any source
 *    distribution.
 */

/**
 * Second generation code for constructing and searching a <a href="https://en.wikipedia.org/wiki/K-d_tree>KD-Tree</a>. 
 * <p>
 * The code contained here has been taken from <a href="https://bitbucket.org/rednaxela/knn-benchmark">https://bitbucket.org/rednaxela/knn-benchmark</a>.
 * <p>
 * The KNN benchmark project contains various implementations of Kd-trees for performing efficient K-Nearest Neighbour
 * searches. There are many implementations available, this package contains one of the fastest in the standard
 * benchmark tests.
 * <p>
 * The code is Copyright 2009 Rednaxela.
 * <p>
 * Modifications to the code have been made by Alex Herbert for a smaller memory footprint and optimised 2D processing.
 */
package ags.utils.dataStructures.trees.secondGenKD;
