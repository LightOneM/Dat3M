#include <stdio.h>
#include <string.h>

int int_array[3] = {1, 1, 1};

struct Data {
    int values[2];
    char name[5];
};

struct Data struct_array[3] = {
    {{1, 1}, "break"},
    {{1, 1}, "break"},
    {{1, 1}, "break"}
};

int main() {
    int oob_int = int_array[4];
    int oob_struct_val = struct_array[3].values[0];
    int *vp = &struct_array[1].values[0];
    int oob_ptr_val = *(vp + 3);
    char *cp = struct_array[2].name;
    char oob_char = *(cp + 6);
    return 0;
}
