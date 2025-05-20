// use after free simple

#include <stdlib.h>

int main(void) {
    int *ptr = malloc(sizeof(int));
    *ptr = 42;
    free(ptr);
    *ptr = 10;
    return 0;
}
