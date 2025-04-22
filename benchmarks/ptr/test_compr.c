#include <assert.h>
#include <stdio.h>

int main() {
    int arr[5] = {10, 20, 30, 40, 50};
    int *p = arr;
    assert(*(p + 1) == 20);
    assert(*(p + 1) != 50);
    assert(*(p + 4) == 50);
    assert(*(p + 4) != 20);
    int *start = &arr[0];
    int *end = &arr[4];
    assert(end - start == 4);
    assert(end - start != 5);
    for (int i = 0; i < 5; i++) {
        assert(*(p + i) == arr[i]);
        assert(*(p + i + 1) != arr[i]);
    }
    void *voidPtr = (void *)arr;
    int *intPtr = (int *)voidPtr;
    for (int i = 0; i < 5; i++) {
        assert(*(intPtr + i) == arr[i]);
        assert(*(intPtr + i + 1) != arr[i]);
    }
    *(p + 2) = 35;
    assert(arr[2] == 35);
    assert(arr[2] != 20);
    return 0;
}
