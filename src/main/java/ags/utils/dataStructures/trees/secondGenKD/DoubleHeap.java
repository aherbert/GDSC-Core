package ags.utils.dataStructures.trees.secondGenKD;

/**
 * Class for tracking up to 'size' closest values
 */
public class DoubleHeap
{
	final double[] distance;
	private final int size;
	int values;
	public double removedDist;

	public DoubleHeap(int size)
	{
		this.distance = new double[size];
		this.size = size;
		this.values = 0;
	}

	public void addValue(double dist)
	{
		// If there is still room in the heap
		if (values < size)
		{
			// Insert new value at the end
			distance[values] = dist;
			upHeapify(values);
			values++;
		}
		// If there is no room left in the heap, and the new entry is lower
		// than the max entry
		else if (dist < distance[0])
		{
			// Replace the max entry with the new entry
			distance[0] = dist;
			downHeapify(0);
		}
	}

	public void removeLargest()
	{
		if (values == 0)
		{
			throw new IllegalStateException();
		}

		removedDist = distance[0];
		values--;
		distance[0] = distance[values];
		downHeapify(0);
	}

	private void upHeapify(int c)
	{
		for (int p = (c - 1) / 2; c != 0 && distance[c] > distance[p]; c = p, p = (c - 1) / 2)
		{
			double pDist = distance[p];
			distance[p] = distance[c];
			distance[c] = pDist;
		}
	}

	private void downHeapify(int p)
	{
		for (int c = p * 2 + 1; c < values; p = c, c = p * 2 + 1)
		{
			if (c + 1 < values && distance[c] < distance[c + 1])
			{
				c++;
			}
			if (distance[p] < distance[c])
			{
				// Swap the points
				double pDist = distance[p];
				distance[p] = distance[c];
				distance[c] = pDist;
			}
			else
			{
				break;
			}
		}
	}

	public double getMaxDist()
	{
		if (values < size)
		{
			return Double.POSITIVE_INFINITY;
		}
		return distance[0];
	}
	
	public double[] values()
	{
		return distance;
	}
}