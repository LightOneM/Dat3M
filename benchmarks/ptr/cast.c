#include <stdio.h>
#include <stdint.h>

#define ITERATIONS 100

int main() {
    int array[ITERATIONS];
    for (int i = 0; i < ITERATIONS; ++i) {
        array[i] = i;
    }

    uintptr_t ptr_as_int;
    void *int_as_ptr;
    int *p;
    volatile int sum = 0;

    for (int i = 0; i < ITERATIONS - 1; ++i) {
        ptr_as_int = (uintptr_t)&array[i];
        ptr_as_int += sizeof(int);
        int_as_ptr = (void*)ptr_as_int;
        p = (int*)int_as_ptr;
        sum += *p;
    }

    printf("Sum: %d\n", sum);

    return 0;
}