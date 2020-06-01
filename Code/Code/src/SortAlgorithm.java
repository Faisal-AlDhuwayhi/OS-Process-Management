
public class SortAlgorithm {
	
	// To be used when filling the job queue by reading from the file.
	public void mergeSort(Job A[], int l, int r) {
		if (l >= r)
			return;
		int m = (l + r) / 2;
		mergeSort(A, l, m); // Sort first half
		mergeSort(A, m + 1, r); // Sort second half
		merge(A, l, m, r); // Merge
	}
	
	// sorting Jobs by arrival time
	private void merge(Job A[], int l, int m, int r) {
		Job[] B = new Job[r - l + 1];
		int i = l, j = m + 1, k = 0;
		while (i <= m && j <= r)
			if (A[i].arrival_time <= A[j].arrival_time)
				B[k++] = A[i++];
			else
				B[k++] = A[j++];
		if (i > m)
			while (j <= r)
				B[k++] = A[j++];
		else
			while (i <= m)
				B[k++] = A[i++];
		for (k = 0; k < B.length; k++)
			A[k + l] = B[k];
	}

}
