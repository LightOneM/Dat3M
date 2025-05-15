#include <stdio.h>
#include <stdlib.h>

int main() {
    int *arr = malloc(10 * sizeof(int));
    int *ptr = &arr[9];
    int *new_arr = malloc(5 * sizeof(int));
    free(arr);
    arr = new_arr;
    printf("%d\n", *ptr);
    free(arr);
    return 0;
}