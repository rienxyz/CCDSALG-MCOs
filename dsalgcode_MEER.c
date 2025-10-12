#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// Constants
#define MAX_STRING_LENGTH 1000
#define MAX_SIZES 100
#define MAX_SAMPLE_SIZE 1000
#define DNA_ALPHABET "acgt"

// Function prototypes
int compareSuffix(const char *str, int idx1, int idx2);
void selectionSort(const char *str, int *suffixArray, int n);
void shellSort(const char *str, int *suffixArray, int n);
void printAllSuffixes(const char *str, int n);
void printSortedSuffixes(const char *str, const int *suffixArray, int n);
void printSuffixArray(const int *suffixArray, int n);
void initializeSuffixArray(int *suffixArray, int n);
void generateRandomDNAString(char *str, int length);
int validateDNAString(const char *str);
void processSuffixArray(const char *str, int algorithm, const char *algorithmName);
double measureExecutionTime(const char *str, int algorithm);
void runEmpiricalAnalysis(void);
void printMenu(void);

// Compare two suffixes
int compareSuffix(const char *str, int idx1, int idx2) {
    return strcmp(str + idx1, str + idx2);
}

// Selection Sort
void selectionSort(const char *str, int *suffixArray, int n) {
    for (int i = 0; i < n - 1; i++) {
        int minIdx = i;
        
        // Find the minimum suffix in remaining array
        for (int j = i + 1; j < n; j++) {
            if (compareSuffix(str, suffixArray[j], suffixArray[minIdx]) < 0) {
                minIdx = j;
            }
        }
        
        // Swap if needed
        if (minIdx != i) {
            int temp = suffixArray[i];
            suffixArray[i] = suffixArray[minIdx];
            suffixArray[minIdx] = temp;
        }
    }
}

// Shell Sort
void shellSort(const char *str, int *suffixArray, int n) {
    for (int gap = n / 2; gap > 0; gap /= 2) {
        for (int i = gap; i < n; i++) {
            int temp = suffixArray[i];
            int j;
            
            // Shift elements until correct location is found
            for (j = i; j >= gap && compareSuffix(str, suffixArray[j - gap], temp) > 0; j -= gap) {
                suffixArray[j] = suffixArray[j - gap];
            }
            suffixArray[j] = temp;
        }
    }
}

// Print all suffixes with their indices
void printAllSuffixes(const char *str, int n) {
    printf("\nAll suffixes:\n");
    for (int i = 0; i < n; i++) {
        printf("%d: %s\n", i, str + i);
    }
}

// Print sorted suffixes
void printSortedSuffixes(const char *str, const int *suffixArray, int n) {
    printf("\nSorted suffixes:\n");
    for (int i = 0; i < n; i++) {
        printf("%d: %s\n", suffixArray[i], str + suffixArray[i]);
    }
}

// Print suffix array
void printSuffixArray(const int *suffixArray, int n) {
    printf("\nSuffix array:\n");
    for (int i = 0; i < n; i++) {
        printf("%d%s", suffixArray[i], (i < n - 1) ? " " : "");
    }
    printf("\n");
}

// Initialize suffix array with indices 0 to n - 1
void initializeSuffixArray(int *suffixArray, int n) {
    for (int i = 0; i < n; i++) {
        suffixArray[i] = i;
    }
}

// Generate random DNA string
void generateRandomDNAString(char *str, int length) {
    const char alphabet[] = DNA_ALPHABET;
    for (int i = 0; i < length; i++) {
        str[i] = alphabet[rand() % 4];
    }
    str[length] = '\0';
}

// Validate DNA string
int validateDNAString(const char *str) {
    for (int i = 0; str[i] != '\0'; i++) {
        if (strchr(DNA_ALPHABET, str[i]) == NULL) {
            return 0;
        }
    }
    return 1;
}

// Process a single string with a given algorithm
void processSuffixArray(const char *str, int algorithm, const char *algorithmName) {
    int n = strlen(str);
    int *suffixArray = (int *)malloc(n * sizeof(int));
    
    if (suffixArray == NULL) {
        printf("Memory allocation failed!\n");
        return;
    }

    initializeSuffixArray(suffixArray, n);

    printf("\n========================================\n");
    printf("Algorithm: %s\n", algorithmName);
    printf("Input string: %s\n", str);
    printf("String length: %d\n", n);
    printf("========================================\n");

    printAllSuffixes(str, n);

    // Sort based on selected algorithm
    if (algorithm == 1) {
        selectionSort(str, suffixArray, n);
    } else {
        shellSort(str, suffixArray, n);
    }

    printSortedSuffixes(str, suffixArray, n);
    printSuffixArray(suffixArray, n);

    free(suffixArray);
}

// Measure execution time in milliseconds
double measureExecutionTime(const char *str, int algorithm) {
    int n = strlen(str);
    int *suffixArray = (int *)malloc(n * sizeof(int));
    
    if (suffixArray == NULL) {
        return -1.0;
    }

    initializeSuffixArray(suffixArray, n);

    clock_t start = clock();
    
    if (algorithm == 1) {
        selectionSort(str, suffixArray, n);
    } else {
        shellSort(str, suffixArray, n);
    }
    
    clock_t end = clock();
    
    free(suffixArray);
    
    return ((double)(end - start) / CLOCKS_PER_SEC) * 1000.0;
}

// Run empirical analysis
void runEmpiricalAnalysis(void) {
    int numSizes, sampleSize;
    
    printf("\nEnter the number of different string sizes to test (1-%d): ", MAX_SIZES);
    scanf("%d", &numSizes);
    
    if (numSizes <= 0 || numSizes > MAX_SIZES) {
        printf("Invalid number of sizes! Must be between 1 and %d.\n", MAX_SIZES);
        return;
    }
    
    int *sizes = (int *)malloc(numSizes * sizeof(int));
    if (sizes == NULL) {
        printf("Memory allocation failed!\n");
        return;
    }
    
    printf("Enter %d string sizes (e.g., 128 256 512 1024 2048): ", numSizes);
    for (int i = 0; i < numSizes; i++) {
        scanf("%d", &sizes[i]);
        if (sizes[i] <= 0) {
            printf("Invalid size! All sizes must be positive.\n");
            free(sizes);
            return;
        }
    }
    
    printf("Enter the sample size k (number of trials per size, 1-%d): ", MAX_SAMPLE_SIZE);
    scanf("%d", &sampleSize);
    
    if (sampleSize <= 0 || sampleSize > MAX_SAMPLE_SIZE) {
        printf("Invalid sample size! Must be between 1 and %d.\n", MAX_SAMPLE_SIZE);
        free(sizes);
        return;
    }
    
    printf("\n========================================\n");
    printf("Task 4: Empirical Running Time Analysis\n");
    printf("========================================\n");
    printf("%-10s %-20s %-15s %-15s\n", "n", "Algorithm", "Avg. Time (ms)", "Sample Size");
    printf("------------------------------------------------------------\n");

    for (int i = 0; i < numSizes; i++) {
        int n = sizes[i];
        double totalTimeSelection = 0.0;
        double totalTimeShell = 0.0;

        for (int j = 0; j < sampleSize; j++) {
            char *testStr = (char *)malloc((n + 1) * sizeof(char));
            if (testStr == NULL) {
                printf("Memory allocation failed for size %d!\n", n);
                free(sizes);
                return;
            }
            
            generateRandomDNAString(testStr, n);
            totalTimeSelection += measureExecutionTime(testStr, 1);
            totalTimeShell += measureExecutionTime(testStr, 2);
            free(testStr);
        }
        
        double avgTimeSelection = totalTimeSelection / sampleSize;
        double avgTimeShell = totalTimeShell / sampleSize;

        printf("%-10d %-20s %-15.6f %-15d\n", n, "Selection Sort", avgTimeSelection, sampleSize);
        printf("%-10d %-20s %-15.6f %-15d\n", n, "Shell Sort", avgTimeShell, sampleSize);
        
        if (i < numSizes - 1) {
            printf("%-10s %-20s %-15s %-15s\n", "", "", "", "");
        }
    }
    
    free(sizes);
}

// Print main menu
void printMenu(void) {
    printf("\n==========================================\n");
    printf("         MCO1 CCDSALG - GROUP 14\n");
    printf("==========================================\n");
    printf("1. Sorting Algorithms (Shell & Selection)\n");
    printf("2. Empirical Running Time Analysis\n");
    printf("3. Exit\n");
    printf("Enter your choice: ");
}

int main(void) {
    srand((unsigned int)time(NULL));
    char inputStr[MAX_STRING_LENGTH];
    int choice;
    
    while (1) {
        printMenu();
        
        if (scanf("%d", &choice) != 1) {
            printf("Invalid input! Please enter a number.\n");
            while (getchar() != '\n'); // Clear input buffer
            continue;
        }
        getchar(); // Consume newline
        
        switch (choice) {
            case 1:
                printf("\nEnter a DNA string (only 'a', 'c', 'g', 't'): ");
                if (fgets(inputStr, sizeof(inputStr), stdin) == NULL) {
                    printf("Error reading input!\n");
                    break;
                }
                inputStr[strcspn(inputStr, "\n")] = '\0';
                
                if (!validateDNAString(inputStr)) {
                    printf("Invalid DNA string! Only characters 'a', 'c', 'g', 't' are allowed.\n");
                } else {
                    processSuffixArray(inputStr, 1, "Selection Sort");
                    processSuffixArray(inputStr, 2, "Shell Sort");
                }
                break;
                
            case 2:
                runEmpiricalAnalysis();
                break;
                
            case 3:
                printf("\nGoodbye!\n");
                return 0;
                
            default:
                printf("\nInvalid choice! Please enter 1, 2, or 3.\n");
        }
    }
    
    return 0;
}
