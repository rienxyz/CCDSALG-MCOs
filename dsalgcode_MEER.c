#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

int compareSuffix(char *str, int idx1, int idx2)
{
    return strcmp(str + idx1, str + idx2);
}

void selectionSort(char *str, int *suffixArray, int n)
{
    int i, j, minIdx, temp;

    for (i = 0; i < n - 1; i++)
    {
        minIdx = i;
        for (j = i + 1; j < n; j++)
        {
            if (compareSuffix(str, suffixArray[j], suffixArray[minIdx]) < 0)
            {
                minIdx = j;
            }
        }
        if (minIdx != i)
        {
            temp = suffixArray[i];
            suffixArray[i] = suffixArray[minIdx];
            suffixArray[minIdx] = temp;
        }
    }
}

void shellSort(char *str, int *suffixArray, int n)
{
    int i, j, gap, temp;
    for (gap = n / 2; gap > 0; gap /= 2)
    {
        for (i = gap; i < n; i++)
        {
            temp = suffixArray[i];
            j = i;
            while (j >= gap && compareSuffix(str, suffixArray[j - gap], temp) > 0)
            {
                suffixArray[j] = suffixArray[j - gap];
                j -= gap;
            }
            suffixArray[j] = temp;
        }
    }
}