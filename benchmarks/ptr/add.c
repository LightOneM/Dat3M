#include <stdio.h>
#include <stdlib.h>
int main(void) {
    size_t n = 5;
    int *p = malloc(n * sizeof *p);
    int *q = p + 2;
    printf("%d\n", *q);
    free(p);
    return 0;
}
