#include <stdio.h>

int int_array[5];

struct Data {
    int value;
};

struct Data struct_array[5];

int main() {
    int a = int_array[3];
    int b = struct_array[3].value;
    int arr[] = {10, 20, 30};
    int *end = arr + 3;
    return 0;
}