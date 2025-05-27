// frees a stack pointer
#include <stdlib.h>

int main() {
    int x = 42;
    free(&x);
    return 0;
}
