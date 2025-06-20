#include <stdio.h>

int main() {
    int arr[] = {10, 20, 30, 40, 50};
    int *ptr = &arr[4];
    int *sub_ptr = ptr - 2;
    printf("Value at new_ptr: %d\n", *sub_ptr);
    return 0;
}