#include <assert.h>
#include <stdint.h>

int main() {
    int a = 0;
    int b = 1;
    char c = 'c';

    intptr_t int_a = (intptr_t)&a;
    intptr_t int_b = (intptr_t)&b;
    intptr_t int_c = (intptr_t)&c;

    assert((int*)int_a == &a);
    assert((int*)int_b == &b);
    assert((char*)int_c == &c);

    assert(((int*)int_a) + 1 == (&a + 1));
    assert(((int*)int_b) - 1 == (&b - 1));

    return 0;
}