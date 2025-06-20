#include <stdio.h>
struct MyStruct {
    int a;
    int b;
};
int main() {
    struct s ptr;
    struct s *address = &ptr + 0;
    printf("%p\n", (void*)address);
    return 0;
}