#include <stdlib.h>
#include <assert.h>
int main(void) {
    int *ptr = malloc(sizeof(int));
    *ptr = 1;
    free(ptr);
    free(ptr);
    return 0;
}