#include <stdio.h>
struct MyStruct {
    int a;
    int b;
};
int main() {
    struct MyStruct ptr;
    struct MyStruct *address = &ptr + 0;
    printf("%p\n", (void*)address);
    return 0;
}