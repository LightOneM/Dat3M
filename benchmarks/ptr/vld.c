#include <stdio.h>
#include <stdlib.h>
int i;
int main() {
    int *a = malloc(sizeof(int));
    int *b = malloc(sizeof(int));

    if (a < b){i = 1;}
    free(a);
    free(b);
}