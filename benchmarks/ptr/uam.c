#include <stdlib.h>
#include <assert.h>
int main(void) {
    int *ptr = malloc(sizeof(int));
    *ptr = 10;
    free(ptr);
    ptr = (int *)malloc(sizeof(int));
    *ptr = 20;
    free(ptr);
    return 0;
}
