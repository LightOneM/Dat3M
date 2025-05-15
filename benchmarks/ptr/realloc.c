// this test fails because realloc is not yet supported

#include <stdio.h>
#include <stdlib.h>

int main() {
    int *arr = malloc(10 * sizeof(int));
    int *ptr = &arr[9];
    arr = realloc(arr, 5 * sizeof(int));
    printf("%d\n", *ptr);
    free(arr);
    return 0;
}
