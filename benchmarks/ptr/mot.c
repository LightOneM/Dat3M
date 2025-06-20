#include <stdlib.h>

int main() {
    int* x = malloc(sizeof(int));
    int* y = malloc(sizeof(int));
    int* ptr;
    ptr = x + 1;
    if ((int)ptr == (int)y) {
        *y = 13;
        *ptr = 14;
    }
    return 0;
}
