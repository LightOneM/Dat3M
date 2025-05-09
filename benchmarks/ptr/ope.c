#include <pthread.h>
void* f(void* a) { return 0; }
int main() {
    pthread_t t;
    void* result;
    pthread_create(&t, 0, f, 0);
    pthread_join(t, &result);
}

