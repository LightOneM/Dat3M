#include <assert.h>
#include <stdint.h>

int main() {
    int a = 0;
    int b = 1;
    char c = 'c';


    assert(b > a);

    intptr_t int_a = (intptr_t)&a;
    intptr_t int_b = (intptr_t)&b;
    intptr_t int_c = (intptr_t)&c;

    assert((int*)int_a == (int*)&a);
    assert((int*)int_b == (int*)&b);
    assert((int*)int_c == (int*)&c);

    assert((void*)&a < (void*)&b);
    assert((void*)&b < (void*)&c);

    return 0;
}